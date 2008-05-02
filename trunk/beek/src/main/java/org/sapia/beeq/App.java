package org.sapia.beeq;

import java.util.UUID;

import org.sapia.beeq.impl.MessageNodeImpl;
import org.sapia.beeq.queue.Queue;
import org.sapia.beeq.queue.QueueListener;
import org.sapia.beeq.queue.mem.MemoryQueue;

/**
 * Hello world!
 * 
 */
public class App {
  
  Queue send      = new MemoryQueue(new OutgoingListener());
  Queue receive   = new MemoryQueue(new IncomingListener());
    
  public static void main( String[] args ) throws Exception{
    App app = new App();
    app.run();
  }
  
  public void run() throws Exception{
    MessageNode node = new MessageNodeImpl(UUID.randomUUID().toString(), send);
    Message msg = node.create(null);
    msg.setContentType("none");
    msg.setPayload("Helloworld");
    msg.setDestination("http://acme.com");
    node.put(msg);
  }
  
  ///// QueueListener interface
  
  class OutgoingListener implements QueueListener{
  
    public void onMessage(Message msg) throws Exception {
      receive.add(msg);
    }
  }
  
  class IncomingListener implements QueueListener{
    
    public void onMessage(Message msg) {
      System.out.println("Received: " + msg.getPayload());
    }
  }
  
  
}
