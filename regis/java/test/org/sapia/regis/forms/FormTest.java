package org.sapia.regis.forms;

import java.util.HashMap;
import java.util.Map;

import org.sapia.regis.impl.NodeImpl;
import org.sapia.regis.type.BuiltinTypes;

import junit.framework.TestCase;

public class FormTest extends TestCase {
  
  Form f;

  public FormTest(String arg0) {
    super(arg0);
  }

  protected void setUp() throws Exception {
    f = new Form("form");
  }
  
  public void testCreateField(){
    f.createField(BuiltinTypes.INT_TYPE, "test");
    try{
      f.createField(BuiltinTypes.INT_TYPE, "test");
      fail("Field already exists");
    }catch(IllegalArgumentException e){}
  }
  
  public void testGetContainsField(){
    f.createField(BuiltinTypes.INT_TYPE, "test");
    super.assertTrue(f.getField("test") != null);
    super.assertTrue(f.containsField("test"));    

  }  
  
  public void testGetFields(){
    Field f1 = f.createField(BuiltinTypes.INT_TYPE, "field1");
    Field f2 = f.createField(BuiltinTypes.INT_TYPE, "field2");
    assertEquals(f.getFields().get(0), f1);
    assertEquals(f.getFields().get(1), f2);    
  }
  
  public void testAssign(){
    NodeImpl node = new NodeImpl();
    f.createField(BuiltinTypes.INT_TYPE, "intField");
    f.createField(BuiltinTypes.BOOLEAN_TYPE, "booleanField");
    Map values = new HashMap();
    values.put("intField", "100");
    values.put("booleanField", "true");
    f.assign(node, values);
    assertEquals(new Integer(node.getProperty("intField").asInt()), new Integer(100));
    assertEquals(new Boolean(node.getProperty("booleanField").asBoolean()), new Boolean(true));    
  }
 
}
