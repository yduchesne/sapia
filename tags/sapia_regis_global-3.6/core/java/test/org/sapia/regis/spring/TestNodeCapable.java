package org.sapia.regis.spring;

import org.sapia.regis.Node;
import org.sapia.regis.codegen.NodeCapable;

public class TestNodeCapable implements NodeCapable{
  
  public static final String NODE_PATH = "databases/000";
  
  private Node node;
  
  public TestNodeCapable(Node node) {
    this.node = node;
  }
  
  @Override
  public Node getNode() {
    return node;
  }
  
  @Override
  public <T extends NodeCapable> T getInstanceFor(Class<T> nodeClass) {
    return null;
  }

}
