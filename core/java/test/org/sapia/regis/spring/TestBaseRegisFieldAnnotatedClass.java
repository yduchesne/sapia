package org.sapia.regis.spring;

import org.sapia.regis.Node;
import org.sapia.regis.Registry;

public class TestBaseRegisFieldAnnotatedClass {
  
  private Registry registry;
  
  @Lookup(path="databases/000")
  private Node node;

  
  public Registry getRegistry() {
    return registry;
  }
  
  public Node getNode() {
    return node;
  }
}
