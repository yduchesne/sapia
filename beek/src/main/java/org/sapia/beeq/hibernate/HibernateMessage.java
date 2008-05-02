package org.sapia.beeq.hibernate;

import org.sapia.beeq.MessageID;
import org.sapia.beeq.impl.MessageImpl;

public class HibernateMessage extends MessageImpl{
  
  private long uniqueId, version;
  private String nodeId;

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

}
