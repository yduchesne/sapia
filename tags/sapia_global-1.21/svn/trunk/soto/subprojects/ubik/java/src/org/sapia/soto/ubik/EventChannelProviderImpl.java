package org.sapia.soto.ubik;

import org.sapia.soto.Service;
import org.sapia.ubik.mcast.EventChannel;
import org.sapia.ubik.rmi.Consts;

public class EventChannelProviderImpl 
  implements EventChannelProvider, Service{

  private String _domain = Consts.DEFAULT_DOMAIN;
  private String _addr = Consts.DEFAULT_MCAST_ADDR;
  private int    _mcastPort = Consts.DEFAULT_MCAST_PORT;
  private EventChannel _channel;
  
  /**
   * @param domain
   *          the name of the Ubik domain.
   */
  public void setDomain(String domain) {
    _domain = domain;
  }
  
  /**
   * @param addr the multicast address to use for discovery.
   */
  public void setMulticastAddress(String addr){
    _addr = addr;
  }

  /**
   * @param port the multicast port to use for discovering remote
   * JNDI servers.
   */
  public void setMulticastPort(int port){
    _mcastPort = port;
  }
  
  public void init() throws Exception {
    _channel = new EventChannel(_domain, _addr, _mcastPort);
    _channel.start();
  }
  
  public void start() throws Exception {
  }
  
  public void dispose() {
    if(_channel != null){
      _channel.close();
    }
  }
  
  public EventChannel getEventChannel() {
    if(_channel == null){
      throw new IllegalStateException("Event channel is null; not properly initialized");
    }
    return _channel;
  }
}
