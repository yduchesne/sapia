package org.sapia.beeq.sender;

import java.util.ArrayList;
import java.util.List;

import org.sapia.beeq.Message;

public class SenderChain implements Sender{
  
  private List<Sender> senders = new ArrayList<Sender>();

  public void addSender(Sender sender){
    senders.add(sender);
  }
  
  public boolean accepts(Message msg) {
    for(Sender s:senders){
      if(s.accepts(msg)){
        return true;
      }
    }
    return false;
  }
  
  public void send(Message message) throws Exception {
    for(Sender s:senders){
      if(s.accepts(message)){
        s.send(message);
      }
    }
  }
  
}
