package org.sapia.regis.spring;

import org.sapia.regis.Node;
import org.sapia.regis.Registry;

public class TestBaseRegisMethodAnnotatedClass {
  
  private Registry registry;
  
  private Node node;
  
  public Registry getRegistry() {
    return registry;
  }
  
  public Node getNode() {
    return node;
  }
  
  @Regis(node="databases/000")
  public void setNode(Node node) {
    this.node = node;
  }
}
