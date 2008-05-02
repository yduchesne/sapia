package org.sapia.beeq;

import org.sapia.beeq.impl.MessageImpl;

/**
 * Provides misc utility functions to manipulate messages.
 * 
 * @author yduchesne
 *
 */
public class Messages {
  
  /**
   * @param other some {@link Message}
   * @return a {@link Message} holding the state of the given
   * {@link Message}, therefore acting as an exact copy.
   */
  public static Message copy(Message other){
    MessageImpl msg = new MessageImpl();
    msg.setClientGeneratedId(other.getClientGeneratedId());
    msg.setContentType(other.getContentType());
    msg.setCreationDate(other.getCreationDate());
    msg.setDestination(other.getDestination());
    msg.setID(other.getID());
    msg.setPayload(other.getPayload());
    msg.setStatus(other.getStatus());
    return msg;    
  } 

}
