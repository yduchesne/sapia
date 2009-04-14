package org.sapia.qool;

import javax.jms.Connection;
import javax.jms.JMSException;

import org.sapia.qool.Debug.Level;
import org.sapia.qool.util.AbstractPool;

/**
 * This class implements a pool of JMS sessions.
 * 
 * @author yduchesne
 *
 */
public class JmsSessionPool extends AbstractPool<PooledJmsSession, PooledSessionConfig>{
  
  private PooledSessionFactory creator;
  private Connection internal;
  private Debug debug = Debug.createInstanceFor(JmsSessionPool.class, Level.ERROR);
  
  JmsSessionPool(Connection internal, PooledSessionFactory creator) {
    this.creator = creator;
    this.internal = internal;
  }
  
  /**
   * @param max this instance's configuration.
   */
  synchronized void setConfig(Config conf){
    setMaxSize(conf.getMaxSessions());
    debug.setLevel(conf.getDebugLevel());
  }

  @Override
  protected PooledJmsSession doCreate(PooledSessionConfig config)
      throws JMSException {
    PooledJmsSession pooledSession = creator.createSession(this.internal, config.transactional, config.ackMode);
    return pooledSession;
  }
  
}
