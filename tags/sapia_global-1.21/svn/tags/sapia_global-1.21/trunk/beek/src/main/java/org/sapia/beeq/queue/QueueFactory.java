package org.sapia.beeq.queue;

public interface QueueFactory {
  
  public Queue createQueueFor(QueueListener listener);

}
