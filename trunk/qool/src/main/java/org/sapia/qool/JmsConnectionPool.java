package org.sapia.qool;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.QueueConnectionFactory;
import javax.jms.TopicConnectionFactory;

import org.sapia.qool.Constants.Type;

/**
 * This class implements a pool of JMS sessions.
 * 
 * @author yduchesne
 *
 */
public class JmsConnectionPool {
  
  private AtomicInteger busyCount = new AtomicInteger(0);
  private AtomicBoolean closed = new AtomicBoolean(false);
  
  private LinkedList<PooledJmsConnection> available = new LinkedList<PooledJmsConnection>();
  private Type type;
  private ConnectionFactory internal;
  private Config config = new Config();
  private Debug debug = Debug.createInstanceFor(JmsConnectionPool.class, config.getDebugLevel());
 
  JmsConnectionPool(Type type, ConnectionFactory internal) {
    this.type = type;
    this.internal = internal;
  }
  
  /**
   * @param max this instance's configuration.
   */
  synchronized void setConfig(Config conf){
    this.config = conf;
    this.debug.setLevel(conf.getDebugLevel());
  }
  
  /**
   * @param username the username, or <code>null</code> if no username or password is to be used.
   * @param password the password, or <code>null</code> if no username or password is to be used.
   * @return a {@link PooledJmsConnection}
   * @throws InterruptedException
   * @throws JMSException
   */
  synchronized PooledJmsConnection acquire(String username, String password) throws InterruptedException, JMSException{
    if(closed.get()){
      throw new IllegalStateException("Connection pool is closed");
    }
    if(config.getMaxConnections() > 0 && busyCount.get() >= config.getMaxConnections()){
      while(busyCount.get() >= config.getMaxConnections()){
        if(debug.isDebug()) debug.debug("waiting for connection... (busy count: " + busyCount.get() + " - available: " + available.size());
        wait();
      }
    }
 
    PooledJmsConnection pooledConnection;
    if(available.size() == 0){
      if(type == Type.GENERIC){
        if(username != null && password != null){
          Connection internalConnection = internal.createConnection(username, password);
          pooledConnection = new PooledJmsConnection(type, internalConnection);
        }
        else{
          Connection internalConnection = internal.createConnection();
          pooledConnection = new PooledJmsConnection(type, internalConnection);
        }
      }
      else if(type == Type.QUEUE){
        if(username != null && password != null){
          Connection internalConnection = ((QueueConnectionFactory)internal).createConnection(username, password);
          pooledConnection = new PooledJmsConnection(type, internalConnection);
        }
        else{
          Connection internalConnection = ((QueueConnectionFactory)internal).createConnection();
          pooledConnection = new PooledJmsConnection(type, internalConnection);
        }
      }
      else if(type == Type.TOPIC){
        if(username != null && password != null){
          Connection internalConnection = ((TopicConnectionFactory)internal).createConnection(username, password);
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
      if(debug.isDebug()) debug.debug("acquiring: creating new connection (busy count: " + busyCount.get() + " - available: " + available.size());
      pooledConnection.setConfig(config);
    }
    else{
      if(debug.isDebug()) debug.debug("acquiring: reusing connection (busy count: " + busyCount.get() + " - available: " + available.size());
      pooledConnection = available.removeFirst();
    }
    busyCount.incrementAndGet();

    return pooledConnection;
  }
  
  synchronized void release(PooledJmsConnection c) {
    if(closed.get()){
      throw new IllegalStateException("Connection Pool is closed");
    }
  
    boolean isValid = c.recycle();

    if(isValid){
      this.available.addLast(c);
      if(debug.isDebug()) debug.debug("releasing: adding connection back to pool (busy count: " + busyCount.get() + " - available: " + available.size());
    }  
    busyCount.decrementAndGet();
 
    notify();
  }
  
  synchronized void close(){
    if(!this.closed.get()){
      this.closed.set(true);
      for(PooledJmsConnection c:this.available){
        try{
          c.getDelegate().close();
        }catch(Exception e){
          debug.warn("Error closing JMS connection", e);
        }
      }
    }
  }
  
}
