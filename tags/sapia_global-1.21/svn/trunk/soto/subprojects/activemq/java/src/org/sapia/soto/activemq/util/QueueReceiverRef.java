package org.sapia.soto.activemq.util;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;

public class QueueReceiverRef implements MessageListener {
  
  private QueueReceiver _receiver;
  private QueueSession _session;
  private MessageListener _delegateListener;
  private long _messageCount;
  
  public QueueReceiverRef(QueueReceiver receiver, 
                          QueueSession session,
                          MessageListener aDelegateListener) throws JMSException {
    _receiver = receiver;
    _session = session;
    _delegateListener = aDelegateListener;
    if (aDelegateListener != null) {
      _receiver.setMessageListener(this);
    }
  }
  
  public void setDelegateListener(MessageListener aListener) throws JMSException {
    _delegateListener = aListener;
    if (aListener == null) {
      _receiver.setMessageListener(null);
    } else {
      _receiver.setMessageListener(this);
    }
  }
  
  public QueueReceiver getReceiver() {
    return _receiver;
  }
  
  public QueueSession getSession() {
    return _session;
  }
  
  public long getMessageCount() {
    return _messageCount;
  }
  
  public void close() throws JMSException {
    _receiver.setMessageListener(null);
    _receiver.close();
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
