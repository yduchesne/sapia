package org.sapia.qool;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.jms.Connection;
import javax.jms.JMSException;

import org.sapia.qool.Debug.Level;

/**
 * This class implements a pool of JMS sessions.
 * 
 * @author yduchesne
 *
 */
public class JmsSessionPool {
  
  private AtomicInteger busyCount = new AtomicInteger(0);
  private AtomicBoolean closed = new AtomicBoolean(false);
  
  private LinkedList<PooledJmsSession> available = new LinkedList<PooledJmsSession>();
  private PooledSessionFactory creator;
  private Connection internal;
 
  private Config conf = new Config();
  private Debug debug = Debug.createInstanceFor(JmsSessionPool.class, Level.ERROR);
  
  JmsSessionPool(Connection internal, PooledSessionFactory creator) {
    this.creator = creator;
    this.internal = internal;
  }
  
  /**
   * @param max this instance's configuration.
   */
  synchronized void setConfig(Config conf){
    this.conf = conf;
    debug.setLevel(conf.getDebugLevel());
  }
  
  /**
   * @param transacted <code>true</code> if the created session is created in the context
   * of a transaction
   * @param ackMode acknowledgement mode.
   * @return a PooledJmsSession
   * @throws InterruptedException
   * @throws JMSException
   */
  synchronized PooledJmsSession acquire(boolean transacted, int ackMode) throws InterruptedException, JMSException{
    if(closed.get()){
      throw new IllegalStateException("Session pool is closed");
    }
    if(conf.getMaxSessions() > 0 && busyCount.get() >= conf.getMaxSessions()){
      while(busyCount.get() >= conf.getMaxSessions()){
        if(debug.isDebug()) debug.debug("waiting for connection... (busy count: " + busyCount.get() + " - available: " + available.size());
        wait();
      }
    }

    PooledJmsSession pooledSession;
    if(available.size() == 0){
      if(debug.isDebug()) debug.debug("acquiring: creating new session (busy count: " + busyCount.get() + " - available: " + available.size());
      pooledSession = creator.createSession(this.internal, transacted, ackMode);
    }
    else{
      if(debug.isDebug()) debug.debug("acquiring: reusing session (busy count: " + busyCount.get() + " - available: " + available.size());
      pooledSession = available.removeFirst();
      if(transacted){
        pooledSession.flagTransacted();
      }
    }
    busyCount.incrementAndGet();

    return pooledSession;
  }
  
  synchronized void release(PooledJmsSession s) {
    if(closed.get()){
      throw new IllegalStateException("Session pool is closed");
    }
  
    boolean isValid = s.recycle();

    if(isValid){
      this.available.addLast(s);
      if(debug.isDebug()) debug.debug("releasing: adding connection back to pool (busy count: " + busyCount.get() + " - available: " + available.size());
    }  
    busyCount.decrementAndGet();
    notify();
  }
  
  synchronized void close(){
    this.closed.set(true);
    for(PooledJmsSession s:this.available){
      try{
        s.getDelegate().close();
      }catch(Exception e){
        debug.warn("Error closing JMS session", e);
      }
    }
  }
  
}
