package org.sapia.ubik.rmi.server.gc;

import org.sapia.ubik.jmx.MBeanContainer;
import org.sapia.ubik.jmx.MBeanFactory;
import org.sapia.ubik.rmi.Consts;
import org.sapia.ubik.rmi.PropUtil;
import org.sapia.ubik.rmi.server.*;
import org.sapia.ubik.rmi.server.perf.HitStatFactory;
import org.sapia.ubik.rmi.server.perf.HitsPerMinStatistic;
import org.sapia.ubik.taskman.Task;
import org.sapia.ubik.taskman.TaskContext;
import org.sapia.ubik.taskman.TaskManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.ObjectName;


/**
 * This class implements the server-side distributed garbage
 * collection algorithm.
 *
 * @author Yanick Duchesne
 * 2002-08-09
 */
public class ServerGC implements Task, ServerGCMBean, MBeanFactory {

  /* delay after which client that haved not performed a ping are considered down. */
  public static final long GC_TIMEOUT = ClientGC.GC_CLEAN_INTERVAL * 3 * 15;

  /* interval at which GC checks timed-out clients. */
  public static final long GC_INTERVAL  = ClientGC.GC_CLEAN_INTERVAL * 3;
  
  private static long      _gcTimeout   = GC_TIMEOUT;
  private static long      _gcInterval  = GC_INTERVAL;
  private static HitsPerMinStatistic _gcRefPerMin;
  private static HitsPerMinStatistic _gcDerefPerMin;  
  
  private Map<VmId, ClientInfo> _clientTable = new ConcurrentHashMap<VmId, ClientInfo>();

  /**
   * Creates a new {@link ServerGC} instance.
   *
   * @param taskman
   */
  public ServerGC(TaskManager taskman) {
    PropUtil pu = new PropUtil().addProperties(System.getProperties());
    _gcInterval = pu.getLongProperty(Consts.SERVER_GC_INTERVAL, GC_INTERVAL);
    _gcTimeout  = pu.getLongProperty(Consts.SERVER_GC_TIMEOUT, GC_TIMEOUT); 
    if (_gcInterval > 0) {
      taskman.addTask(new TaskContext("UbikRMI.ServerGC", _gcInterval), this);
    } else {
      Log.warning(getClass(), "Will be disabled; client timeouts will not be monitored");
    }
    
    _gcRefPerMin = HitStatFactory.createHitsPerMin("ServerGCRefPerMin", 0, Hub.statsCollector);
    _gcDerefPerMin = HitStatFactory.createHitsPerMin("ServerGCDerefPerMin", 0, Hub.statsCollector);
  }

  /**
   * Returns the total number of references held on the given object
   * identifier.
   *
   * @param id a {@link VmId}.
   * @param oid an {@link OID}.
   *
   * @return a reference count, as an <code>int</code>.
   */
  public int getRefCount(VmId id, OID oid) {
    return getClientInfo(id).getRefCount(oid);
  }

  /**
   * Returns the total number of references held on the given object
   * by the client whose host corresponds to the passed in {@link VmId}.
   * 
   * @param id a {@link VmId}
   * @param oid an {@link OID}
   */
  public int getSpecificCount(VmId id, OID oid) {
    return getClientInfo(id).getSpecificCount(oid);
  }

  /**
   * Returns true if this instance contains the passed in
   * {@link VmId}.
   *
   * @return <code>true</code> if this instance contains the passed in
   * {@link VmId}.
   */
  public boolean containsClient(VmId id) {
    return _clientTable.containsKey(id);
  }

  /**
   * Increments the reference count of the given object identifier,
   * for the client whose {@link VmId} is given.
   *
   * @param id the client's {@link VmId}.
   * @param oid the {@link OID} of the object whose reference count 
   * should be incremented.
   */
  public void reference(VmId id, OID oid) {
    if (Log.isDebug()) {
      Log.debug(ServerGC.class, "referencing from: " + id + " on object: " + oid);
    }
    
    _gcRefPerMin.hit();
    ClientInfo inf = getClientInfo(id);
    inf.reference(oid);
  }

  /**
   * Registers a given object internally so that it is not garbage collected before
   * clients themselves garbage collect it.
   *
   * @param id the {@link VmId} of the client to whom a stub
   * corresponding to the passed in object is returned (this in fact creates
   * a remote reference on the object).
   * @param oid the {@link OID} that identifies the passed in object
   * locally.
   * @param o the {@link Object} for which a stub is eventually returned to the client.
   */
  public void registerRef(VmId id, OID oid, Object o) {
    if (Log.isInfo()) {
      Log.info(ServerGC.class, "reference created from: " + id + " on object: " + oid + " - " + o.getClass().getName());
    }
    
    _gcRefPerMin.hit();
    ClientInfo inf = getClientInfo(id);
    inf.registerRef(oid, o);
  }

  /**
   * Dereferences a given object identifier.
   *
   * @param id the {@link VmId} of the client from which the
   * dereferencing call comes.
   * @param oid the {@link OID} to dereference.
   */
  public void dereference(VmId id, OID oid) {
    if (Log.isDebug()) {
      Log.debug(ServerGC.class, "dereferencing from: " + id + " on object: " + oid);
    }
    
    _gcDerefPerMin.hit();
    ClientInfo inf = getClientInfo(id);
    inf.dereference(oid);
  }

  /**
   * Touches the client info of the {@link VmId} passed in.
   * 
   * @param id a {@link VmId}
   */
  public void touch(VmId id) {
    if (Log.isDebug()) {
      Log.debug(getClass(), "touching client info of vm id " + id);
    }
    
    synchronized (_clientTable) {
      ClientInfo info = _clientTable.get(id);
      
      if (info != null) {
        info.touch();
      } else {
        Log.warning(getClass(), "NO CLIENT INFO FOUND FOR vm id " + id);
      }
    }
  }

  public void exec(TaskContext ctx) {
    Log.debug(getClass(), "runner server GC...");
    removeTimedOutClients();
  }

  public void clear() {
    synchronized (_clientTable) {
      _clientTable.clear();
      Hub.serverRuntime.objectTable.clear();
    }
  }

  ClientInfo getClientInfo(VmId id) {
    synchronized (_clientTable) {
      ClientInfo inf = _clientTable.get(id);
  
      if (inf == null) {
        inf = new ClientInfo(id);
        _clientTable.put(id, inf);
      }
  
      return inf;
    }
  }
  
  ////// JMX-related
  
  public long getInterval() {
    return _gcInterval;
  }
  
  public long getTimeout() {
    return _gcTimeout;
  }
  
  public void setTimeout(long timeout) {
    _gcTimeout = timeout;
  }
  
  public int getClientCount() {
    return _clientTable.size();
  }
  
  //////// MBeanFactory
  
  public MBeanContainer createMBean() throws Exception{
    ObjectName name = new ObjectName("sapia.ubik.rmi:type=ServerGC");
    return new MBeanContainer(name, this);
  }  
  
  ////// Private methods

  /**
   * Removes the clients that have not performed a ping for a given amount
   * of time. This delay can be set through the <code>ubik.rmi.server.gc.timeout</code>
   * property.
   */
  private synchronized void removeTimedOutClients() {
    synchronized (_clientTable) {
      ClientInfo[] infos = _clientTable.values().toArray(new ClientInfo[_clientTable.size()]);

      for (int i = 0; i < infos.length; i++) {
        if (!infos[i].isValid(_gcTimeout)) {
          if (Log.isInfo()) {
            Log.info(getClass(), "removing timed-out client's references " + infos[i].vmid());
          }

          infos[i].unregisterRefs();
          _clientTable.remove(infos[i].vmid());
        }
      }
    }
  }

  ////////////////////////////////////////////////////////////
  //        						INNER CLASSES
  ////////////////////////////////////////////////////////////	
  static class Count {
    int count = 0;
  }

  static class ClientInfo {
    private Map<OID, Count> _oids = new HashMap<OID, Count>();
    private long _lastAccess = System.currentTimeMillis();
    private VmId _id;

    ClientInfo(VmId id) {
      _id = id;
      if (Log.isInfo()) {
        Log.info(getClass(), "Created a new client info for vmId " + _id);
      }
    }

    VmId vmid() {
      return _id;
    }

    void touch() {
      _lastAccess = System.currentTimeMillis();
      if (Log.isDebug()) {
        Log.debug(getClass(), "Touched this client info: " + toString());
      }
    }

    boolean isValid(long timeout) {
      return (System.currentTimeMillis() - _lastAccess) < timeout;
    }

    void reference(OID oid) {
      synchronized (_oids) {
        Count count = _oids.get(oid);
  
        if (count == null) {
          count = new Count();
          _oids.put(oid, count);
        }
  
        count.count++;
        Hub.serverRuntime.objectTable.reference(oid);
      }
    }

    void registerRef(OID oid, Object obj) {
      synchronized (_oids) {
        Count c = new Count();
        c.count++;
        _oids.put(oid, c);
        Hub.serverRuntime.objectTable.register(oid, obj);
      }
    }

    void dereference(OID oid) {
      synchronized (_oids) {
        Count count;
  
        if ((count = _oids.get(oid)) != null) {
          Hub.serverRuntime.objectTable.dereference(oid, count.count);
          _oids.remove(oid);
        }
      }
    }

    void unregisterRefs() {
      synchronized (_oids) {
        OID[] oids = _oids.keySet().toArray(new OID[_oids.size()]);
  
        for (int i = 0; i < oids.length; i++) {
          if(Log.isDebug()){
            Log.debug(getClass(), "Dereferencing: " + oids[i]);
          }
          Hub.serverRuntime.objectTable.dereference(oids[i], (_oids.get(oids[i])).count);
        }
  
        _oids.clear();
      }
    }

    int getSpecificCount(OID oid) {
      Count c = _oids.get(oid);

      if (c == null) {
        return 0;
      } else {
        return c.count;
      }
    }

    int getRefCount(OID oid) {
      return Hub.serverRuntime.objectTable.getRefCount(oid);
    }
    
    public String toString() {
      return super.toString() + "[vmId=" + _id + " oidCount=" + _oids.size() + " lastAccess=" + _lastAccess;
    }
  }
  
}
