package org.sapia.soto;

import org.sapia.soto.util.ObjectFactoryDelegate;

public class TestObjectFactoryDelegate implements ObjectFactoryDelegate{
  
  boolean called;
  
  public Object newInstance(String className) throws Exception {
    called = true;
    return Class.forName(className).newInstance();
  }

}
