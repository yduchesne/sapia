package org.sapia.regis.remote.client;

import java.util.Properties;

import javax.naming.InitialContext;

import org.sapia.regis.Registry;
import org.sapia.regis.remote.RegistryExporter;
import org.sapia.ubik.rmi.naming.remote.RemoteInitialContextFactory;
import org.sapia.ubik.rmi.server.Hub;

/**
 * An instance of this class is used to lookup a remote registry that has been
 * exported by a <code>RegistryExporter</code>.
 * 
 * @see org.sapia.regis.remote.RegistryExporter
 * 
 * @author yduchesne
 *
 */
public class RegistryImporter {
  
  /**
   * This method looks up a remote registry that listens on the given address:port.
   *  
   * @param address the remote address to which the remote registry has been bound.
   * @param port the port to which the remote registry has been bound. 
   * @return the <code>Registry</code> that could be found on the given port.
   * @throws Exception if no registry could be found, or if a problem occurred while
   * looking up.
   */
  public Registry lookup(String String, int port) throws Exception{
    return (Registry)Hub.connect(String, port);
  }
  
  /**
   * This method takes Ubik properties to lookup a remote registry. For more info
   * concerning the actual properties to use, see the <code>RegistryExporter</code>
   * class.
   *  
   * @param jndiName the name of the remote registry to lookup
   * @param props the <code>Properties</code> containing the lookup information
   * @return the <code>Registry</code> that has been found
   * @throws Exception if no registry could be found, or if a problem occurred while
   * looking up.
   * 
   * @see RegistryExporter#bind(String, int, Properties)
   */
  public Registry lookup(String jndiName, Properties props) throws Exception{
    props.setProperty(InitialContext.INITIAL_CONTEXT_FACTORY, RemoteInitialContextFactory.class.getName());
    InitialContext ctx = new InitialContext(props);
    try{
      return new RemoteRegistryProxy((Registry)ctx.lookup(jndiName));
    }finally{
      ctx.close();
    }
  }

}
