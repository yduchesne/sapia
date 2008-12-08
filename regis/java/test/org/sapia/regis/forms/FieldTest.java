package org.sapia.regis.forms;

import java.util.HashMap;
import java.util.Map;

import org.sapia.regis.impl.NodeImpl;
import org.sapia.regis.type.BuiltinTypes;

import junit.framework.TestCase;

public class FieldTest extends TestCase {
  
  Field f;

  public FieldTest(String arg0) {
    super(arg0);
  }
 
  protected void setUp() throws Exception {
    f = new Field(BuiltinTypes.INT_TYPE, "test");
  }
  
  public void testSetString(){
    NodeImpl node = new NodeImpl();
    f.set(node, "100");
    super.assertEquals(f.get(node), new Integer(100));
    
    try{
      f.set(node, "value");
      fail("String value should not have been set");
    }catch(NumberFormatException e){}
  }
  
  public void testSetInt(){
    NodeImpl node = new NodeImpl();
    f.set(node, new Integer(100));
    super.assertEquals(f.get(node), new Integer(100));
  }
  
  public void testSetFromMap(){
    Map values = new HashMap();
    values.put("test", "100");
    NodeImpl node = new NodeImpl();
    f.set(node, values);
    super.assertEquals(f.get(node), new Integer(100));    
  }
  
 
  
  

}
