package org.sapia.beeq.impl;


public class MessageImpl extends AbstractMessage{
  
  protected Object payload;
  
  public Object getPayload() {
    return payload;
  }
  public void setPayload(Object payload) {
    this.payload = payload;
  }
  
}
