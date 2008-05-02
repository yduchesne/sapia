package org.sapia.beeq.hibernate;

import org.sapia.beeq.MessageID;
import org.sapia.beeq.impl.AbstractMessage;

public class HibernateMessage extends AbstractMessage{
  
  private long uniqueId, version;
  private String nodeId;
  private Payload payloadData;

  public long getUniqueId() {
    return uniqueId;
  }

  public void setUniqueId(long uniqueId) {
    this.uniqueId = uniqueId;
  }
  
  public MessageID getID() {
    if(getNodeId() == null){
      return null;
    }
    else if(super.getID() == null){
      setID(new MessageID(this.getNodeId(), this.getUniqueId()));
    }
    return super.getID();
  }

  public String getNodeId() {
    return nodeId;
  }
  
  public void setNodeId(String nodeId) {
    this.nodeId = nodeId;
  }

  public long getVersion() {
    return version;
  }

  public void setVersion(long version) {
    this.version = version;
  } 
  
  public void setPayload(Object payload) {
    setPayloadData(Payload.create(payload));    
  }
  
  public Object getPayload() {
    if(getPayloadData() != null){
      return getPayloadData().getData();
    }
    else{
      return null;
    }
  }

  public Payload getPayloadData() {
    return payloadData;
  }

  public void setPayloadData(Payload payloadData) {
    this.payloadData = payloadData;
  }
}
