package org.sapia.beeq.queue;

import org.sapia.beeq.Message;
import org.sapia.beeq.MessageID;

public interface Queue {
  
  /**
   * @param msg a {@link Message} to add to this instance
   */
  public MessageID add(Message message);

}
