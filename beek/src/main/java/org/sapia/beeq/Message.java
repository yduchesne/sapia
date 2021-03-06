package org.sapia.beeq;

import java.util.Date;

/**
 * An instance of this class holds message data.
 * 
 * @author yduchesne
 *
 */
public interface Message {
  
  /**
   * @return the unique identifier of this message.
   */
  public MessageID getID();
  
  /**
   * @return the identifier generated by the client, meant for 
   * future reference.
   */
  public Object getClientGeneratedId();

  /**
   * @return the creation date of this message.
   */
  public Date getCreationDate();

  /**
   * @return the payload of this message.
   */
  public Object getPayload();
  
  /**
   * @param payload sets the payload of this message.
   */
  public void setPayload(Object payload);
  
  /**
   * @return the content type of this message.
   */
  public String getContentType();
  
  /**
   * @param type the content type to assign to this message
   */
  public void setContentType(String type);

  /**
   * @return the status of this message.
   * @see MessageStatus
   */
  public MessageStatus getStatus();
  
  /**
   * @return the {@link Object} corresponding to this instance's destination.
   */
  public Object getDestination();
  
  /**
   * @param dest this message's destination.
   */
  public void setDestination(Object dest);
  
}
