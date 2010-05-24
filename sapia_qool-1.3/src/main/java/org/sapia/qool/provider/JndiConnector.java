package org.sapia.qool.provider;

import java.io.InputStream;
import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.naming.InitialContext;

/**
 * This connector looks up {@link ConnectionFactory} instances from a JNDI provider, using application-specified
 * properties.
 * 
 * @author yduchesne
 *
 */
public class JndiConnector implements Connector{
  
  /**
   * The URI expected by this implementation must have the following format:
   * 
   * <pre>
   * &lt;connection-jndi-name&gt;/&lt;jndi-properties-file-path&gt;
   * </pre>
   * 
   * Where:
   * 
   * <ul>
   *   <li><b>connection-jndi-name</b> is the name to use to loop the connection factory.
   *   <li><b>jndi-properties-file-path</b> is the path to the JNDI property file.
   * </ul>
   */
  public ConnectionFactory createConnectionFactory(String uri) throws Exception {
    int idx = uri.indexOf('/');
    if(idx <= 0){
      throw new IllegalArgumentException("Expected URI of form: <connection-jndi-name>/<path-to-jndi-properties>; got: " + uri);
    }
    String jndiName = uri.substring(0, idx);
    String propertiesPath = uri.substring(idx+1);
    InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(propertiesPath);

    if(is == null) {
      throw new IllegalArgumentException("JNDI properties file not found: " + propertiesPath);
    }
    
    try {
      Properties props = new Properties(System.getProperties());
      props.load(is);
      InitialContext ctx = new InitialContext(props);
      ConnectionFactory conn = (ConnectionFactory)ctx.lookup(jndiName);
      return conn;
    } finally {
      is.close();
    }
  }

}
