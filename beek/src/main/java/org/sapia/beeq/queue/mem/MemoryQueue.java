package org.sapia.beeq.queue.mem;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.sapia.beeq.Message;
import org.sapia.beeq.MessageID;
import org.sapia.beeq.hibernate.queue.QueueElement;
import org.sapia.beeq.queue.Queue;
import org.sapia.beeq.queue.QueueListener;

/**
 * A {@link Queue} that keeps {@link QueueElement}s in memory.
 * @author yduchesne
 *
 */
public class MemoryQueue implements Queue{
  
  private QueueListener listener;
  private java.util.Queue<Message> messages = new ConcurrentLinkedQueue<Message>();
  
  public MemoryQueue(QueueListener listener){
    this.listener = listener;
  }
  
  public MessageID add(Message msg) {
    messages.offer(msg);
    fireOnMessage();
    return msg.getID();
  }
  
  public void fireOnMessage(){
    Message msg;
    while((msg = messages.poll()) != null){
      try{
        listener.onMessage(msg);
      }catch(Exception e){
      }
    }
  }

}
