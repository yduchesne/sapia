package org.sapia.soto.activemq;

import javax.jms.MessageListener;

/**
 * A convenience interface that specifies <code>MessageListener</code>
 * creation behavior.
 * 
 * @author yduchesne
 *
 */
public interface MessageListenerFactory {
  
  /**
   * @return a newly created <code>MessageListener</code>
   */
  public MessageListener createMessageListener();

}
