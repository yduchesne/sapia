package org.sapia.qool.topic;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

import org.sapia.qool.PooledJmsSession;
import org.sapia.qool.Constants.Type;

/**
 * Extends the {@link PooledJmsSession} class by implementing the {@link TopicSession} 
 * over a vendor-specific {@link TopicSession}
 * 
 * @author yduchesne
 *
 */
public class PooledTopicSession extends PooledJmsSession implements TopicSession{

  
  public PooledTopicSession(Session delegate, boolean transacted) {
    super(Type.TOPIC, delegate, transacted);
  }

  public TopicPublisher createPublisher(Topic arg0) throws JMSException {
    return ((TopicSession)delegate).createPublisher(arg0);
  }

  public TopicSubscriber createSubscriber(Topic arg0, String arg1, boolean arg2)
      throws JMSException {
    return ((TopicSession)delegate).createSubscriber(arg0, arg1, arg2);
  }

  public TopicSubscriber createSubscriber(Topic arg0) throws JMSException {
    return ((TopicSession)delegate).createSubscriber(arg0);
  }

}
