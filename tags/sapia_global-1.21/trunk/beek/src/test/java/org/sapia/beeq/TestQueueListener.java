package org.sapia.beeq;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.sapia.beeq.queue.QueueListener;

public class TestQueueListener implements QueueListener{
  
  Queue<Message> messages = new ConcurrentLinkedQueue<Message>();
  
  public synchronized void onMessage(Message msg) throws Exception {
    messages.add(Messages.copy(msg));
    notifyAll();
  }
  
  public synchronized void waitForMessage(long timeout) throws Exception{
    while(messages.isEmpty()){
      wait(timeout);
    }
  }
  
  public synchronized void waitForMessages(int quantity, long timeout) throws Exception{
    while(messages.size() < quantity){
      wait(timeout);
    }
  }  
  
  public Message get(){
    return messages.remove();
  }
  
  public Collection<Message> getAll(){
    return new ArrayList<Message>(messages);
  }
  

}
