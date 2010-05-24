package org.sapia.qool.queue;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.QueueSession;

import org.sapia.qool.PooledJmsSession;
import org.sapia.qool.PooledSessionFactory;

/**
 * A factory of {@link PooledQueueSession}s.
 * 
 * @author yduchesne
 *
 */
public class PooledQueueSessionFactory implements PooledSessionFactory{
  
  public PooledJmsSession createSession(Connection delegate, boolean transacted, int ackMode) throws JMSException {
    QueueSession s = ((QueueConnection)delegate).createQueueSession(transacted, ackMode);
    PooledQueueSession pooled = new PooledQueueSession(s, transacted); 
    return pooled;
  }
  
  
}
