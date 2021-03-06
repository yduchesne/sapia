/*
 * NamingService.java
 *
 * Created on August 18, 2005, 10:25 AM
 */

package org.sapia.soto.ubik;

import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.sapia.ubik.rmi.naming.remote.discovery.ServiceDiscoListener;

/**
 * Specifies the behavior of a service that services as a broker between
 * a remote Ubik JNDI provider and in-VM Soto services.
 *
 * @author yduchesne
 */
public interface NamingService {
  
  /**
   * Binds the given object to the remote Ubik JNDI provider to which this instance
   * corresponds.
   * 
   * @param name
   *          the name under which to bind the object.
   * @param o
   *          the object to bind.
   */
  public void bind(String name, Object o) throws NamingException;

  /**
   * Looks up the object under the given name.
   * 
   * @param name
   *          the name of the remote object to lookup.
   */
  public Object lookup(String name) throws NamingException,
      NameNotFoundException;
  
  /**
   * Registers the given listener with this instance.
   * 
   * @param listener a <code>ServiceDiscoListener</code>.
   */
  public void register(ServiceDiscoListener listener);
  
/**
   * Unregisters the given listener from this instance.
   * 
   * @param listener a <code>ServiceDiscoListener</code>.
   */
  public void unregister(ServiceDiscoListener listener);  
}
