package org.sapia.qool;

import java.util.HashMap;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;

import org.sapia.qool.Constants.Type;
import org.sapia.qool.Debug.Level;

/**
 * Wraps a vendor-specific {@link ConnectionFactory}. An instance of this class
 * creates {@link Connection}s that provide session pooling behavior.
 * 
 * @author yduchesne
 *
 */
public class PooledJmsConnectionFactory implements ConnectionFactory, TopicConnectionFactory, QueueConnectionFactory{
  
  private Map<Type, JmsConnectionPool> pools = new HashMap<Type, JmsConnectionPool>();
  private Debug debug = Debug.createInstanceFor(PooledJmsConnectionFactory.class, Level.ERROR);

  /**
   * @param delegate a vendor-specific {@link ConnectionFactory}.
   */
  public PooledJmsConnectionFactory(ConnectionFactory delegate){
    pools.put(Type.QUEUE,   new JmsConnectionPool(Type.QUEUE, delegate));
    pools.put(Type.TOPIC,   new JmsConnectionPool(Type.TOPIC, delegate));
    pools.put(Type.GENERIC, new JmsConnectionPool(Type.GENERIC, delegate));
  }
  
  public void setConfig(Config conf) {
    pool(Type.GENERIC).setConfig(conf);
    pool(Type.QUEUE).setConfig(conf);
    pool(Type.TOPIC).setConfig(conf);
    debug.setLevel(conf.getDebugLevel());
    
    debug.info("Registering VM shutdown hook");
    
    if(conf.isRegisterShutdownHooks()){
      Runtime.getRuntime().addShutdownHook(new Thread(){
    
        @Override
        public void run() {
          debug.info("VM shutdown hook executing...");
          shutdown();
          debug.info("VM shutdown hook execution completed");
        }
      });
    }
  }
  
  public Connection createConnection() throws JMSException {
    return doAcquire(Type.GENERIC, null, null);
  }
  
  public Connection createConnection(String arg0, String arg1)
      throws JMSException {
    return doAcquire(Type.GENERIC, arg0, arg1);
  }
  
  public TopicConnection createTopicConnection() throws JMSException {
    return (TopicConnection)doAcquire(Type.TOPIC, null, null);
  }
  
  public TopicConnection createTopicConnection(String arg0, String arg1)
      throws JMSException {
    return (TopicConnection)doAcquire(Type.TOPIC, arg0, arg1); 
  }
  
  public QueueConnection createQueueConnection() throws JMSException {
    return (QueueConnection)doAcquire(Type.QUEUE, null, null);
  }
 
  public QueueConnection createQueueConnection(String arg0, String arg1)
      throws JMSException {
    return (QueueConnection)doAcquire(Type.QUEUE, arg0, arg1);
  }
  
  //////////////////////// Restricted methods
  
  synchronized void shutdown(){
    for(JmsConnectionPool p:pools.values()){
      p.close();
    }
  }
  
  synchronized void release(PooledJmsConnection c) {
    pool(c.getType()).release(c);
   }
  
  private JmsConnectionPool pool(Type t){
    JmsConnectionPool pool = this.pools.get(t);
    if(pool == null){
      throw new IllegalStateException("No session pool for " + t);
    }
    return pool;
  }
  
  private PooledJmsConnection doAcquire(Type t, String username, String password)
    throws JMSException{
    try{
      PooledJmsConnection c = pool(t).acquire(username, password);
      c.setOwner(this);
      return c;
    }catch(InterruptedException e){
      JMSException jex = new JMSException("Could not acquire JMS connection");
      jex.setLinkedException(e);
      throw jex;
    }    
  }
 }
 
