package org.sapia.qool.provider;

import java.util.HashMap;
import java.util.Map;

import javax.jms.ConnectionFactory;

/**
 * This class provides static methods to register {@link Connector}s, and to retrieve {@link ConnectionFactory}
 * instances, given URLs.
 * 
 * @author yduchesne
 *
 */
public class ConnectionFactoryProvider {
  
  private static Map<String, Connector> impls = new HashMap<String, Connector>();
  private static final Connector DEFAULT_IMPL = new ActiveMQConnector();
  
  static{
    impls.put("amq", DEFAULT_IMPL);
  }
  
  /**
   * @param urlPrefix the prefix under which to bind the given connector.
   * @param impl a {@link Connector} implementation.
   */
  public static void registerConnector(String urlPrefix, Connector impl){
    impls.put(urlPrefix, impl);
  }
  
  /**
   * The URL passed to this method is expected to be prefixed by a string under that
   * identifies which connector to use. The URL must be formatted as follows:
   * 
   * <pre><prefix>:&lt;vendor-specific-part%gt;</pre>
   * 
   * For example:
   *  
   * <pre><prefix>amq:vm://localhost</pre>
   * 
   * Where <code>amq</code> (follow by the colon) consists of the so-called connector prefix. Only the remaining part of
   * the URL is passed to the underlying {@link Connector}.
   * 
   * @param url a URL.
   * @return a new {@link ConnectionFactory} instance.
   * @throws Exception
   */
  public static ConnectionFactory createFactory(String url) throws Exception{
    int idx = url.indexOf(':');
    if(idx <= 0){
    }
    String connectorPrefix = url.substring(0, idx);
    String connectorUrl = url.substring(idx+1);
    Connector impl = impls.get(connectorPrefix);
    
    // falling back to AMQ
    if(impl == null){
      return DEFAULT_IMPL.createConnectionFactory(url);
    }
    
    // using found connector...
    else{
      return impl.createConnectionFactory(connectorUrl);
    }
    
  }

}
