package org.sapia.qool;

import javax.jms.Connection;
import javax.jms.JMSException;

/**
 * This interface specifies the behavior expected from factories that create {@link PooledJmsSession}s.
 * @author yduchesne
 *
 */
public interface PooledSessionFactory{

  /**
   * @param delegate the {@link Connection} to use to create the session.
   * @param transacted <code>true</code> if the session must be created in the context of
   * a transaction.
   * @param ackMode the acknowledgement mode of the session to create.
   * @return a PooledJmsSession.
   * @throws JMSException
   */
  public PooledJmsSession createSession(Connection delegate, boolean  transacted, int ackMode) throws JMSException;
  
}
