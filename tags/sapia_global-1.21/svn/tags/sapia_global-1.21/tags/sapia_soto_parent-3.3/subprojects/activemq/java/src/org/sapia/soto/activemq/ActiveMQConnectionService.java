package org.sapia.soto.activemq;

import javax.jms.QueueConnectionFactory;
import javax.jms.TopicConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ConfigurationException;
import org.sapia.soto.Service;

/**
 * Implements the JmsConnectionService interface on top of an ActiveMQConnectionFactory.
 * 
 * @author yduchesne
 */
public class ActiveMQConnectionService implements JmsConnectionService, Service{
  
  private ActiveMQConnectionFactory _factory;
  private String _connectionUri;
  
  public void setConnectionFactory(ActiveMQConnectionFactory fac){
    _factory = fac;
  }
  
  public void setConnectionUri(String uri){
    _connectionUri = uri;
  }
  
  /// Soto Service IF methods
  public void init() throws Exception {
    if(_factory == null){
      if(_connectionUri == null){
        throw new ConfigurationException("Connection URI not set");
      }
      _factory = new ActiveMQConnectionFactory(_connectionUri);
    }
  }
  
  public void start() throws Exception {
  }
  
  public void dispose() {
  }
  
  // JmsService IF methods
  public QueueConnectionFactory createQueueFactory() {
    return _factory;
  }
  
  public TopicConnectionFactory createTopicFactory() {
    return _factory;
  }
  
}
