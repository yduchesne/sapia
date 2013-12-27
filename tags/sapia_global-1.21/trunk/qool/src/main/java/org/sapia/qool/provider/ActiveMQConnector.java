package org.sapia.qool.provider;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * This provider creates {@link ActiveMQConnectionFactory} instances. It expects any valid ActiveMQ URI 
 * (see the ActiveMQ doc for more details).
 * 
 * @author yduchesne
 *
 */
public class ActiveMQConnector implements Connector{

  public ConnectionFactory createConnectionFactory(String url) throws Exception {
    return new ActiveMQConnectionFactory(url);
  }
  
}
