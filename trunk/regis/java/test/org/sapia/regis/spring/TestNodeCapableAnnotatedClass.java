package org.sapia.regis.spring;

public class TestNodeCapableAnnotatedClass {

  @Regis
  private TestNodeCapable nodeCapable;
    
  public TestNodeCapable getNodeCapable() {
    return nodeCapable;
  }

}
