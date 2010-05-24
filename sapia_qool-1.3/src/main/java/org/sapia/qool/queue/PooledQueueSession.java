package org.sapia.qool.queue;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;

import org.sapia.qool.PooledJmsSession;
import org.sapia.qool.Constants.Type;

/**
 * Extends the {@link PooledJmsSession} class by implementing the {@link QueueSession} 
 * over a vendor-specific {@link QueueSession}
 * 
 * @author yduchesne
 *
 */
public class PooledQueueSession extends PooledJmsSession implements QueueSession{

  public PooledQueueSession(Session delegate, boolean transacted) {
    super(Type.QUEUE, delegate, transacted);
  }

  public QueueReceiver createReceiver(Queue arg0, String arg1)
      throws JMSException {
    return ((QueueSession)delegate).createReceiver(arg0, arg1);
  }

  public QueueReceiver createReceiver(Queue arg0) throws JMSException {
    return ((QueueSession)delegate).createReceiver(arg0);
  }

  public QueueSender createSender(Queue arg0) throws JMSException {
    return ((QueueSession)delegate).createSender(arg0);
  }

}
