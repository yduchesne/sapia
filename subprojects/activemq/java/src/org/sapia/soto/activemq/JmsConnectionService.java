package org.sapia.soto.activemq;

import javax.jms.JMSException;
import javax.jms.QueueConnectionFactory;
import javax.jms.TopicConnectionFactory;

/**
 * This interface specifies behavior for acquiring topic or
 * queue connection factories.
 *  
 * @author yduchesne
 *
 */
public interface JmsConnectionService {
  
  
  /**
   * @return a <code>TopicConnectionFactory</code>, or <code>null</code>
   * if no such instance could be acquired.
   * 
   * @throws JMSException
   */
  public TopicConnectionFactory createTopicFactory() throws JMSException;
  
  /**
   * @return a <code>QueueConnectionFactory</code>, or <code>null</code>
   * if no such instance could be acquired.
   * 
   * @throws JMSException
   */  
  public QueueConnectionFactory createQueueFactory() throws JMSException;

}
