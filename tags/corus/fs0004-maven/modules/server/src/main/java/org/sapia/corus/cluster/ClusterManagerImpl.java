package org.sapia.corus.cluster;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.sapia.corus.client.services.cluster.ClusterManager;
import org.sapia.corus.core.ModuleHelper;
import org.sapia.ubik.mcast.AsyncEventListener;
import org.sapia.ubik.mcast.EventChannel;
import org.sapia.ubik.mcast.RemoteEvent;
import org.sapia.ubik.net.ServerAddress;
import org.sapia.ubik.rmi.Consts;
import org.sapia.ubik.rmi.replication.ReplicationEvent;
import org.sapia.ubik.rmi.server.Hub;
import org.sapia.ubik.rmi.server.invocation.ServerPreInvokeEvent;


/**
 * @author Yanick Duchesne
 */
public class ClusterManagerImpl extends ModuleHelper
  implements ClusterManager, AsyncEventListener {
  static ClusterManagerImpl instance;
  private String _multicastAddress = Consts.DEFAULT_MCAST_ADDR;
  private int _multicastPort       = Consts.DEFAULT_MCAST_PORT;
  private EventChannel      _channel;
  private Set<ServerAddress>   _hostsAddresses = Collections.synchronizedSet(new HashSet<ServerAddress>());
  
  /**
   * @param addr this instance's multicast address.
   */
  public void setMcastAddress(String addr){
    _multicastAddress = addr;
  }
  
  /**
   * @param port this instance's multicast port.
   */
  public void setMcastPort(int port){
    _multicastPort = port;
  }

  /**
   * @see org.sapia.corus.core.soto.Service#init()
   */
  public void init() throws Exception {
    instance = this;
    _channel = new EventChannel(
        _serverContext.getDomain(), 
        _multicastAddress,
        _multicastPort);
    _channel.registerAsyncListener(CorusPubEvent.class.getName(), this);
    _channel.start();
    _channel.setBufsize(4000);    
    _log.info("Signaling presence to cluster on: " + _multicastAddress + ":" + _multicastPort);
    _channel.dispatch(CorusPubEvent.class.getName(),
            new CorusPubEvent(true, serverContext().getServerAddress()));
		ServerSideClusterInterceptor interceptor = new ServerSideClusterInterceptor(_log, serverContext());
		
    Hub.serverRuntime.addInterceptor(ServerPreInvokeEvent.class, interceptor);
		Hub.serverRuntime.addInterceptor(ReplicationEvent.class, interceptor);    
  }
  
  /**
   * @see org.sapia.corus.core.soto.Service#dispose()
   */
  public void dispose() {
    _channel.close();
  }

  /*////////////////////////////////////////////////////////////////////
                        Module INTERFACE METHOD
  ////////////////////////////////////////////////////////////////////*/

  /**
   * @see org.sapia.corus.client.Module#getRoleName()
   */
  public String getRoleName() {
    return ClusterManager.ROLE;
  }

  /*////////////////////////////////////////////////////////////////////
                     ClusterManager and instance METHODS
  ////////////////////////////////////////////////////////////////////*/

  /**
   * @see ClusterManager#getHostAddresses()
   */
  public synchronized Set<ServerAddress> getHostAddresses() {
    return new HashSet<ServerAddress>(_hostsAddresses);
  }

  /**
   * @see ClusterManager#getEventChannel()
   */
  public EventChannel getEventChannel() {
    return _channel;
  }

  /**
   * @see AsyncEventListener#onAsyncEvent(org.sapia.ubik.mcast.RemoteEvent)
   */
  public void onAsyncEvent(RemoteEvent remote) {
    Object event = null;

    try {
      event = remote.getData();
    } catch (IOException e) {
      _log.debug("Could not get event data", e);

      return;
    }

    if (event instanceof CorusPubEvent) {
      CorusPubEvent evt  = (CorusPubEvent) event;
      ServerAddress  addr = evt.getOrigin();
      _hostsAddresses.add(evt.getOrigin());

      if (evt.isNew()) {
        _log.debug("New corus discovered: " + addr);

        try {
          _channel.dispatch(CorusPubEvent.class.getName(),
                  new CorusPubEvent(false, serverContext().getTransport().getServerAddress()));
        } catch (IOException e) {
          _log.debug("Event channel could not dispatch event", e);
        }
      } else {
        _log.debug("Existing corus discovered: " + addr);
      }
    } 
  }
}
