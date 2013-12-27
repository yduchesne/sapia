package org.sapia.qool;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.QueueConnectionFactory;
import javax.jms.TopicConnectionFactory;

import org.sapia.qool.Constants.Type;
import org.sapia.qool.util.AbstractPool;

/**
 * This class implements a pool of JMS sessions.
 * 
 * @author yduchesne
 *
 */
public class JmsConnectionPool extends AbstractPool<PooledJmsConnection, PooledConnectionConfig> {
  
  private Type type;
  private ConnectionFactory internal;
  private Config config = new Config();
 
  JmsConnectionPool(Type type, ConnectionFactory internal) {
    super();
    this.type = type;
    this.internal = internal;
  }
  
  /**
   * @param max this instance's configuration.
   */
  synchronized void setConfig(Config conf){
    this.setMaxSize(conf.getMaxConnections());
    this.config = conf;
    this.debug.setLevel(conf.getDebugLevel());
  }
  
  @Override
  protected PooledJmsConnection doCreate(PooledConnectionConfig connConfig) throws JMSException{
    PooledJmsConnection pooledConnection;
    if(type == Type.GENERIC){
      if(connConfig.username != null && connConfig.password != null){
        Connection internalConnection = internal.createConnection(connConfig.username, connConfig.password);
        pooledConnection = new PooledJmsConnection(type, internalConnection);
      }
      else{
        Connection internalConnection = internal.createConnection();
        pooledConnection = new PooledJmsConnection(type, internalConnection);
      }
    }
    else if(type == Type.QUEUE){
      if(connConfig.username != null && connConfig.password != null){
        Connection internalConnection = ((QueueConnectionFactory)internal).createConnection(connConfig.username, connConfig.password);
        pooledConnection = new PooledJmsConnection(type, internalConnection);
      }
      else{
        Connection internalConnection = ((QueueConnectionFactory)internal).createConnection();
        pooledConnection = new PooledJmsConnection(type, internalConnection);
      }
    }
    else if(type == Type.TOPIC){
      if(connConfig.username != null && connConfig.password != null){
        Connection internalConnection = ((TopicConnectionFactory)internal).createConnection(connConfig.username, connConfig.password);
        pooledConnection = new PooledJmsConnection(type, internalConnection);
      }
      else{
        Connection internalConnection = ((TopicConnectionFactory)internal).createConnection();
        pooledConnection = new PooledJmsConnection(type, internalConnection);
      }
    }
    else{
      throw new IllegalStateException("Unknown connection type: " + type);
    }
    pooledConnection.setConfig(config);
    return pooledConnection;
  }
}
