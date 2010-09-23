package org.sapia.regis.gui.event;

import org.sapia.regis.Node;

public class NodeUpdatedEvent {

  private Node node;
  
  public NodeUpdatedEvent(Node node){
    this.node = node;
  }
  
  public Node getNode(){
    return node;
  }
}
