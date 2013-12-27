package org.sapia.qool.topic;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.TopicConnection;
import javax.jms.TopicSession;

import org.sapia.qool.PooledJmsSession;
import org.sapia.qool.PooledSessionFactory;

/**
 * A factory of {@link PooledTopicSession}s.
 * 
 * @author yduchesne
 *
 */
public class PooledTopicSessionFactory implements PooledSessionFactory{

  public PooledJmsSession createSession(Connection delegate, boolean transacted, int ackMode)
      throws JMSException {
    
    TopicSession s = ((TopicConnection)delegate).createTopicSession(transacted, ackMode);
    PooledTopicSession pooled = new PooledTopicSession(s, transacted); 
    return pooled;
  }
  
  
}
