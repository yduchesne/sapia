package org.sapia.regis.gui.event;

import org.sapia.regis.Node;

public class NodeCreatedEvent {

  private Node node;
  
  public NodeCreatedEvent(Node node){
    this.node = node;
  }
  
  public Node getNode(){
    return node;
  }
}
