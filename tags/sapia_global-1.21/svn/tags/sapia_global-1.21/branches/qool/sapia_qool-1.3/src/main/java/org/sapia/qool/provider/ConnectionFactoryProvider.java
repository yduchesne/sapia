package org.sapia.qool.provider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.ConnectionFactory;

import org.sapia.qool.Debug;
import org.sapia.qool.Debug.Level;

/**
 * This class provides static methods to register {@link Connector}s, and to retrieve {@link ConnectionFactory}
 * instances, given URIs.
 * <p>
 * Connectors are bound using a so-called "prefix", which is the first component of URIs that are passed
 * to the {@link #createFactory(String)} method.
 * <p>
 * Built-in connectors are already pre-registered with this class:
 * <ul>
 *   <li><b>amq</b>: maps to an {@link ActiveMQConnector}.
 *   <li><b>jndi</b>: maps to a {@link JndiConnector}.
 * </ul>
 * 
 * @author yduchesne
 *
 */
public class ConnectionFactoryProvider {
  
  private static Map<String, Connector> impls = new ConcurrentHashMap<String, Connector>();
  private static final Connector DEFAULT_IMPL = new ActiveMQConnector();
  private static Debug debug = Debug.createInstanceFor(ConnectionFactoryProvider.class, Level.DEBUG);
  
  static{
    impls.put("amq", DEFAULT_IMPL);
    impls.put("jndi", new JndiConnector());
  }
  
  /**
   * @param urlPrefix the prefix under which to bind the given connector.
   * @param impl a {@link Connector} implementation.
   */
  public static void registerConnector(String urlPrefix, Connector impl){
    impls.put(urlPrefix, impl);
  }
  
  /**
   * The URI passed to this method is expected to be prefixed by a string under that
   * identifies which connector to use. The URI must be formatted as follows:
   * 
   * <pre>&lt;prefix&gt;:&lt;vendor-specific-part%gt;</pre>
   * 
   * For example:
   *  
   * <pre>&lt;prefix&gt;amq:vm://localhost</pre>
   * 
   * Where <code>amq</code> (follow by the colon) consists of the so-called connector prefix. 
   * Only the "vendor-specific part" of the URI is passed to the underlying {@link Connector}.
   * 
   * @param uri a URI.
   * @return a new {@link ConnectionFactory} instance.
   * @throws Exception
   */
  public static ConnectionFactory createFactory(String uri) throws Exception{
    int idx = uri.indexOf(':');
    if(idx > 0){
      String connectorPrefix = uri.substring(0, idx);
      String connectorUri = uri.substring(idx+1);
      Connector impl = impls.get(connectorPrefix);
      if(impl == null){
        debug.error("No connector for prefix: " + connectorPrefix + "; falling back to: " + DEFAULT_IMPL);
        return DEFAULT_IMPL.createConnectionFactory(uri);
      }
      else{
        debug.debug("Using connector: " + impl);
        return impl.createConnectionFactory(connectorUri);     
      }
    }
    
    // falling back to AMQ
    debug.error("Falling back to default provider: " + DEFAULT_IMPL);
    return DEFAULT_IMPL.createConnectionFactory(uri);
  }

}
