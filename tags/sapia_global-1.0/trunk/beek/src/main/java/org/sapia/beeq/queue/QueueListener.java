package org.sapia.beeq.queue;

import org.sapia.beeq.Message;

public interface QueueListener {
  
  public void onMessage(Message msg) throws Exception;

}
