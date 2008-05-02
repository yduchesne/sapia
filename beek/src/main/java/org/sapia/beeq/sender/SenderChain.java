package org.sapia.beeq.sender;

import java.util.ArrayList;
import java.util.List;

import org.sapia.beeq.Message;

/**
 * This class implements a chain of responsibility. An instance
 * of this class nests other {@link Sender} instances. All the ones
 * that accept (see {@link Sender#accepts(Message)} a given message
 * have their {@link Sender#send(Message)} method called.
 * 
 * @author yduchesne
 *
 */
public class SenderChain implements Sender{
  
  private List<Sender> senders = new ArrayList<Sender>();

  public void addSender(Sender sender){
    senders.add(sender);
  }
  
  /**
   * This method returns <code>true</code> if at least one of
   * the {@link Sender} that this instance encapsulates accepts
   * the given message.
   */
  public boolean accepts(Message msg) {
    for(Sender s:senders){
      if(s.accepts(msg)){
        return true;
      }
    }
    return false;
  }
  
  /**
   * This method delegates sending of the message to all
   * the {@link Sender}s that accept the given message.
   */
  public void send(Message message) throws Exception {
    for(Sender s:senders){
      if(s.accepts(message)){
        s.send(message);
      }
    }
  }
  
}
