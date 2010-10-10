/*
 * TestNamingService.java
 *
 * Created on August 19, 2005, 8:41 AM
 *
 */

package org.sapia.soto.ubik;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.sapia.ubik.rmi.naming.remote.discovery.ServiceDiscoListener;
import org.sapia.ubik.rmi.naming.remote.discovery.ServiceDiscoveryEvent;

/**
 *
 * @author yduchesne
 */
public class TestNamingService implements NamingService{
  
  private Map _services = new HashMap();
  private List _listeners = new ArrayList();
  
  /** Creates a new instance of TestNamingService */
  public TestNamingService() {
  }

  public void bind(String name, Object o) throws NamingException{
    _services.put(name, o);
    for(int i = 0; i < _listeners.size(); i++){
      ServiceDiscoListener listener = (ServiceDiscoListener)_listeners.get(i);
      ServiceDiscoveryEvent evt = new ServiceDiscoveryEvent(new Properties(), name, o);
      listener.onServiceDiscovered(evt);
    }
  }

  public Object lookup(String name) throws NamingException,
      NameNotFoundException{
    Object o = _services.get(name);
    if(o == null)
      throw new NameNotFoundException(name);
    return o;
  }
  
  public void register(ServiceDiscoListener listener){
    _listeners.add(listener);
  }
  
  public void unregister(ServiceDiscoListener listener){
    _listeners.remove(listener);
  }
  
  public List getDiscoListeners(){
    return _listeners;
  }
}
