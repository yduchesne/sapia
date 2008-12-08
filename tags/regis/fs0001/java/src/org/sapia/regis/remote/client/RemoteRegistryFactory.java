package org.sapia.regis.remote.client;

import java.util.Properties;

import org.sapia.regis.Registry;
import org.sapia.regis.RegistryFactory;

/**
 * This class implements a factory that looks up a remote registry. The
 * remote registry is a registry server stub that is made available on the network in
 * one of two ways (using a <code>RegistryExporter</code>):
 * <ul>
 *   <li>by binding the registry stub to a specific port.
 *   <li>by binding the registry stub under a given name, in a distributed
 *   Ubik JNDI server.
 * </ul>
 * <p>
 * Then, an instance of this class is used to lookup the registry. The instance
 * expects properties that specify either the JNDI name of the registry and connection URL
 * of the JNDI server in which it is published; or the IP address/port to which the registry
 * is bound.  
 * 
 * @see org.sapia.regis.remote.RegistryExporter
 * 
 * @author yduchesne
 *
 */
public class RemoteRegistryFactory implements RegistryFactory{
  
  /**
   * This constant corresponds to the property that specifies the JNDI name under
   * which to lookup the remote registry.
   */
  public static final String JNDI_NAME = "org.sapia.regis.remote.jndi.name";
  
  /**
   * This constant corresponds to the property that specifies the address of
   * the host on which the remote registry is bound.
   */
  public static final String ADDRESS   = "org.sapia.regis.remote.address";
  
  /**
   * This constant corresponds to the property that specifies the port to
   * which the remote registry is bound.
   */
  public static final String PORT      = "org.sapia.regis.remote.port";  
  
  public Registry connect(Properties props) throws Exception {
    String jndiName = props.getProperty(JNDI_NAME);
    String portStr = props.getProperty(PORT);
    if(portStr == null){
      throw new IllegalStateException("Registry server jndi name or port must be specified");
    }
    String addr = props.getProperty(ADDRESS);
    if(addr == null){
      throw new IllegalStateException("Address must be specified");
    }
    
    if(jndiName != null){
      RegistryImporter imp = new RegistryImporter();
      props.setProperty("java.naming.provider.url", "ubik://"+addr+":"+portStr);
      return imp.lookup(jndiName, props);
    }
    else{
      RegistryImporter imp = new RegistryImporter();
      return imp.lookup(addr, Integer.parseInt(portStr));
    }
  }
}
