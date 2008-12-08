package org.sapia.regis.gui.event;

import org.sapia.regis.Node;

public class NodeChangeEvent {

  private Node node;
  
  public NodeChangeEvent(Node node){
    this.node = node;
  }
  
  public Node getNode(){
    return node;
  }
}
