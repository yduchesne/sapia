package org.sapia.beeq;

/**
 * An instance of this class uniquely identifies a message. A {@link Message}
 * is created and queued by a given {@link MessageNode}. The message thus encapsulates,
 * as part of its ID, the identifier of the node, as well as the unique identifier
 * of the message in the scope of that node (otherwise named "local ID"). To be globally unique, the {@link MessageID}
 * thus consists of the combination of the node and local identifiers. 
 * 
 * @author yduchesne
 *
 */
public class MessageID implements java.io.Serializable{
  
  static final long serialVersionUID = 1L;
  
  private Object localID;
  private String nodeID;
  
  public MessageID(String nodeID, Object localID){
    this.nodeID = nodeID;
    this.localID = localID;
  }
  
  /**
   * @return unique ID of the message in the context of its {@link MessageNode}.
   */
  public Object getLocalID() {
    return localID;
  }
  public void setLocalID(Object localID) {
    this.localID = localID;
  }
  /**
   * @return the unique ID of the {@link MessageNode}
   */
  public String getNodeID() {
    return nodeID;
  }
  public void setNodeID(String nodeID) {
    this.nodeID = nodeID;
  }
  /**
   * @return a string representation of this ID.
   */
  public String toString(){
    StringBuffer buf = new StringBuffer();
    return buf.append(nodeID).append("-").append(localID).toString();
  }
  public boolean equals(Object other){
    if(other instanceof MessageID){
      MessageID otherID = (MessageID)other;
      return otherID.getNodeID().equals(nodeID) && otherID.getLocalID().equals(localID);
    }
    else{
      return false;
    }
  }
  public int hashCode(){
    return this.nodeID.hashCode() ^ this.localID.hashCode();
  }
  
}
