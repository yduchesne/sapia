package org.sapia.beeq.impl;

import java.util.Date;

import org.sapia.beeq.Message;
import org.sapia.beeq.MessageID;
import org.sapia.beeq.MessageStatus;

public class MessageImpl implements Message{
  
  protected MessageID id; 
  protected Object clientGeneratedId;
  protected Date creationDate = new Date();
  protected Object payload;
  protected Object destination;
  protected String contentType;
  protected MessageStatus status = MessageStatus.CREATED;
  
  public Date getCreationDate() {
    return creationDate;
  }
  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }
  public MessageID getID() {
    return id;
  }
  public void setID(MessageID id) {
    this.id = id;
  }
  public Object getPayload() {
    return payload;
  }
  public void setPayload(Object payload) {
    this.payload = payload;
  }
  public MessageStatus getStatus() {
    return status;
  }
  public void setStatus(MessageStatus status) {
    this.status = status;
  }
  public Object getClientGeneratedId() {
    return clientGeneratedId;
  }
  public void setClientGeneratedId(Object clientGeneratedId) {
    this.clientGeneratedId = clientGeneratedId;
  }
  public Object getDestination() {
    return destination;
  }
  public void setDestination(Object destination) {
    this.destination = destination;
  }
  public String getContentType() {
    return contentType;
  }
  public void setContentType(String contentType) {
    this.contentType = contentType;
  }
  
}
