package org.sapia.ubik.rmi.server.stub;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.naming.Name;

import org.sapia.ubik.log.Category;
import org.sapia.ubik.log.Log;
import org.sapia.ubik.mcast.AsyncEventListener;
import org.sapia.ubik.mcast.DomainName;
import org.sapia.ubik.mcast.EventChannel;
import org.sapia.ubik.mcast.MulticastAddress;
import org.sapia.ubik.mcast.RemoteEvent;
import org.sapia.ubik.module.Module;
import org.sapia.ubik.module.ModuleContext;
import org.sapia.ubik.rmi.naming.remote.archie.SyncPutEvent;
import org.sapia.ubik.rmi.server.EventChannelTable;
import org.sapia.ubik.util.Collections2;
import org.sapia.ubik.util.Condition;

/**
 * This class manages stateless stubs on the client-side and insures that
 * notications are properly dispatched.
 *
 * @author yduchesne
 */
public class StatelessStubTable implements Module {

  private static final String SYNC_STATELESS_REF_EVENT_TYPE = "org.sapia.ubik.rmi.server.stub.sync.event";

  private Category log = Log.createCategory(getClass());
  private List<DomainContexts> domainContexts = new ArrayList<DomainContexts>();
  private EventChannelTable eventChannelTable;
  private ThreadLocal<RemoteEvent> attachedRemoteEvent = new ThreadLocal<>();

  @Override
  public void init(ModuleContext context) {
  }

  @Override
  public void start(ModuleContext context) {
    eventChannelTable = context.lookup(EventChannelTable.class);
  }

  @Override
  public synchronized void stop() {
    domainContexts.clear();
  }

  /**
   * @param ref
   *          a {@link RemoteRefStateless} that will be internally kept.
   */
  public void registerStatelessRef(RemoteRefStateless ref, Collection<RemoteRefContext> newContexts) {
    log.debug("Registering stateless stub %s", ref);

    DomainName dn = DomainName.parse(ref.getDomain());
    DomainContexts dc = getDomainContextsFor(dn, ref.getMulticastAddress());

    dc.registerStatelessRef(ref, newContexts);
  }

  private synchronized DomainContexts getDomainContextsFor(DomainName domainName, MulticastAddress addr) {
    for (DomainContexts dc : domainContexts) {
      if (dc.domainName.contains(domainName)) {
        return dc;
      }
    }

    DomainContexts dc = new DomainContexts(eventChannelTable, domainName, addr);
    domainContexts.add(dc);
    dc.registerWithEventChannel();
    return dc;
  }

  // --------------------------------------------------------------------------

  class DomainContexts implements AsyncEventListener {

    private Category log = Log.createCategory(getClass());
    private DomainName domainName;
    private MulticastAddress multicastAddress;
    private EventChannelTable eventChannelTable;

    private Map<String, Contexts> contextsByObjectName = new HashMap<String, Contexts>();

    DomainContexts(EventChannelTable table, DomainName domainName, MulticastAddress address) {
      this.eventChannelTable = table;
      this.domainName = domainName;
      this.multicastAddress = address;
    }

    synchronized void synchronizeStatelessRef(final StatelessRefData ref) {
      if (log.isTrace() && !contextsByObjectName.isEmpty()) {
        log.trace("Got contexts for names:");
        Collections2.forEach(contextsByObjectName.entrySet(), new Condition<Map.Entry<String, Contexts>>() {
          @Override
          public boolean apply(Entry<String, Contexts> item) {
            log.trace("  => %s : %s", item.getKey(), item.getValue());
            return true;
          }
        });
      }
      String name = ref.name.toString();
      Contexts contexts = contextsByObjectName.get(name);
      if (contexts == null) {
        contexts = new Contexts();
        contextsByObjectName.put(name, contexts);
      }

      log.debug("Synchronizing %s new stub endpoints %s for name %s", ref.contexts.size(), ref.contexts, ref.name);
      contexts.addAll(ref.contexts);
      if (log.isTrace()) {
        log.trace("Now got %s endpoints under %s", contexts.getContexts().size(), name);
        Collections2.forEach(contexts.getContexts(), new Condition<RemoteRefContext>() {
          @Override
          public boolean apply(RemoteRefContext item) {
            log.trace("  => %s", item);
            return true;
          }
        });
      }
    }

    synchronized void registerStatelessRef(final RemoteRefStateless ref, Collection<RemoteRefContext> newContexts) {
      if (log.isTrace() && !contextsByObjectName.isEmpty()) {
        log.trace("Got contexts for names:");
        Collections2.forEach(contextsByObjectName.entrySet(), new Condition<Map.Entry<String, Contexts>>() {
          @Override
          public boolean apply(Entry<String, Contexts> item) {
            log.trace("  => %s : %s", item.getKey(), item.getValue());
            return true;
          }
        });
      }
      String name = ref.getName().toString();
      Contexts contexts = contextsByObjectName.get(name);
      if (contexts == null) {
        contexts = new Contexts();
        contextsByObjectName.put(name, contexts);
      }

      log.debug("Registering %s new stub endpoints %s for name %s", newContexts.size(), newContexts, ref.getName());

      RemoteEvent event = attachedRemoteEvent.get();
      if (event == null) {
        log.trace("No remote event associated to thread");
      }
      // if this new ref comes from a remote event (a SyncPutEvent), make sure we synchronize the new node
      // with this instance's remote refs.
      if (event != null && !event.getType().equals(SYNC_STATELESS_REF_EVENT_TYPE)) {
        log.debug("Synching stateless references with remote node: %s", event.getNode());
        EventChannel channel = eventChannelTable.getEventChannelFor(ref.getDomain(), ref.getMulticastAddress());
        Set<RemoteRefContext> contextsToSync = new HashSet<>(contexts.getContexts());
        log.trace("Got current statless ref endpoints: %s", contextsToSync);
        log.trace("Got incoming statless ref endpoints: %s", newContexts);
        contextsToSync.removeAll(newContexts);
        if (!contextsToSync.isEmpty()) {
          if (log.isTrace()) {
            log.trace("Sending stateless ref endpoints:");
            Collections2.forEach(contextsToSync, new Condition<RemoteRefContext>() {
              @Override
              public boolean apply(RemoteRefContext item) {
                log.trace("  => %s", item.toString());
                return true;
              }
            });
          }
          try {
            StatelessRefData statelessRef = new StatelessRefData(ref.getName(), ref.getDomain(), ref.getMulticastAddress(), contextsToSync);
            channel.dispatch(event.getUnicastAddress(), SYNC_STATELESS_REF_EVENT_TYPE, statelessRef);
          } catch (IOException e) {
            log.warning("Could not send stateless stub update to %s", event.getNode());
          }
        }
      }

      // subscribe stub's context list for future modifications
      contexts.addUpdateListener(ref.getContextList());

      // adding ref's contexts to the Contexts instance
      contexts.addAll(newContexts);
      if (log.isTrace()) {
        log.trace("Incoming ref updated with %s endpoints: %s", ref.getContextList().count(), ref.getContextList());
        Collection<RemoteRefContext> contextsForObjectName = contexts.getContexts();
        log.trace("Got %s shared endpoints registered under name: %s", contextsForObjectName.size(), ref.getName());
        Collections2.forEach(contextsForObjectName, new org.sapia.ubik.util.Condition<RemoteRefContext>() {
          @Override
          public boolean apply(RemoteRefContext item) {
            log.trace("  => %s", item.toString());
            return true;
          }
        });
      }

      // this synchronizes the ContextList kept at the stub level with the
      // shared
      // Contexts instance (using anonymous class to avoid circular dependency
      // between Contexts and ContextList)
      final Contexts toSynchronize = contexts;
      ref.getContextList().addRemovalListener(new ContextList.RemovalListener() {
        @Override
        public void onRemoval(RemoteRefContext removed) {
          toSynchronize.remove(removed);
        }
      });
    }

    void registerWithEventChannel() {
      eventChannelTable.getEventChannelFor(domainName.toString(), multicastAddress).registerAsyncListener(SyncPutEvent.class.getName(), this);
      eventChannelTable.getEventChannelFor(domainName.toString(), multicastAddress).registerAsyncListener(SYNC_STATELESS_REF_EVENT_TYPE, this);

    }

    @Override
    public synchronized void onAsyncEvent(RemoteEvent evt) {
      try {
        if (evt.getType().equals(SyncPutEvent.class.getName())) {
          log.debug("Remote binding event received: %s", evt.getType());
          // attaching remote event before deserialization
          log.trace("Associating remote event to thread");
          attachedRemoteEvent.set(evt);
          SyncPutEvent bEvt = (SyncPutEvent) evt.getData();
          log.debug("Got object %s", bEvt.getValue());
        } else if (evt.getType().equals(SYNC_STATELESS_REF_EVENT_TYPE)) {
          log.debug("Remote sync event received: %s", evt.getType());
          StatelessRefData refData = (StatelessRefData) evt.getData();
          synchronizeStatelessRef(refData);
        }
      } catch (IOException e) {
        log.error("IO error receiving bound object", e);
      } catch (ClassNotFoundException e) {
        log.error("Class not found error receiving bound object", e);
      } catch (RuntimeException e) {
        log.error("Runtime error receiving bound object", e);
      }
    }

  }



  public static class StatelessRefData implements Externalizable {
    private Name name;
    private String domain;
    private MulticastAddress multicastAddress;
    private Set<RemoteRefContext> contexts;

    public StatelessRefData() {
    }

    public StatelessRefData(Name name, String domain, MulticastAddress address, Set<RemoteRefContext> contexts) {
      this.name = name;
      this.domain = domain;
      this.multicastAddress = address;
      this.contexts = contexts;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      name = (Name) in.readObject();
      domain = in.readUTF();
      multicastAddress = (MulticastAddress) in.readObject();
      contexts = (Set<RemoteRefContext>) in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
      out.writeObject(name);
      out.writeUTF(domain);
      out.writeObject(multicastAddress);
      out.writeObject(contexts);
    }
  }

}
