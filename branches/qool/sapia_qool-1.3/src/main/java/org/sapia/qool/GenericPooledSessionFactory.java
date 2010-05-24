package org.sapia.qool;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;

import org.sapia.qool.Constants.Type;

/**
 * A factory of {@link PooledJmsSession}s.
 * 
 * @author yduchesne
 *
 */
public class GenericPooledSessionFactory implements PooledSessionFactory{
  
  public PooledJmsSession createSession(Connection delegate, boolean transacted, int ackMode)
      throws JMSException {
    
    Session s = delegate.createSession(transacted, ackMode);
    PooledJmsSession pooled = new PooledJmsSession(Type.GENERIC, s, transacted); 
    return pooled;
  }
  
  
}
