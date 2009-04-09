package org.sapia.qool.provider;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * Create {@link ActiveMQConnectionFactory} instances.
 * 
 * @author yduchesne
 *
 */
public class ActiveMQConnector implements Connector{

  public ConnectionFactory createConnectionFactory(String url) throws Exception {
    return new ActiveMQConnectionFactory(url);
  }
  
}
