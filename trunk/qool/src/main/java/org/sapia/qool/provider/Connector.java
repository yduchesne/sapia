package org.sapia.qool.provider;

import javax.jms.ConnectionFactory;

/**
 * Specifies the behavior for creating {@link ConnectionFactory} instances, given a URL.
 * 
 * @author yduchesne
 *
 */
public interface Connector {
  
  public ConnectionFactory createConnectionFactory(String url) throws Exception;

}
