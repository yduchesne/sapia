/**
 * 
 */
package org.sapia.soto.activemq.util;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

/**
 *
 * @author Jean-Cédric Desrochers
 */
public class TopicSubscriberRef implements MessageListener {

  private TopicSubscriber _subscriber;
  private TopicSession _session;
  private MessageListener _delegateListener;
  private long _messageCount;
  
  public TopicSubscriberRef(TopicSubscriber aSubscriber, TopicSession aSession, MessageListener aMessageListener) throws JMSException {
    _subscriber = aSubscriber;
    _session = aSession;
    _delegateListener = aMessageListener;
    if (aMessageListener != null) {
      _subscriber.setMessageListener(this);
    }
  }
  
  public void setDelegateListener(MessageListener aListener) throws JMSException {
    _delegateListener = aListener;
    if (aListener == null) {
      _subscriber.setMessageListener(null);
    } else {
      _subscriber.setMessageListener(this);
    }
  }
  
  public TopicSubscriber getSubscriber() {
    return _subscriber;
  }
  
  public TopicSession getSession() {
    return _session;
  }
  
  public long getMessageCount() {
    return _messageCount;
  }
  
  public void close() throws JMSException {
    _subscriber.setMessageListener(null);
    _subscriber.close();
    _session.close();
  }

  /* (non-Javadoc)
   * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
   */
  public void onMessage(Message aMessage) {
    _messageCount++;
    _delegateListener.onMessage(aMessage);
  }
}
