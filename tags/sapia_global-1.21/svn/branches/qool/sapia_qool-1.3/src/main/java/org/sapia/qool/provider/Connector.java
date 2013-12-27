package org.sapia.qool.provider;

import javax.jms.ConnectionFactory;

/**
 * Specifies the behavior for creating {@link ConnectionFactory} instances, given a URI.
 * 
 * @author yduchesne
 *
 */
public interface Connector {
  
  /**
   * @param uri a URI.
   * @return a {@link ConnectionFactory}
   * @throws Exception
   */
  public ConnectionFactory createConnectionFactory(String uri) throws Exception;

}
