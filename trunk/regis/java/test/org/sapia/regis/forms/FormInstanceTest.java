package org.sapia.regis.forms;

import org.sapia.regis.impl.NodeImpl;
import org.sapia.regis.type.BuiltinTypes;

import junit.framework.TestCase;

public class FormInstanceTest extends TestCase {
  
  FormInstance f;
  NodeImpl n;

  public FormInstanceTest(String arg0) {
    super(arg0);
  }

  protected void setUp() throws Exception {
    Form form = new Form("test");
    form.createField(BuiltinTypes.INT_TYPE, "field1");
    form.createField(BuiltinTypes.BOOLEAN_TYPE, "field2");
    n = new NodeImpl();
    f = form.getInstance(n);
  }
  
  public void testSetProperty(){
    f.setProperty("field1", "100");
    f.setProperty("field2", "true");
    f.assign();
    assertTrue(n.getProperty("field1").asInt() == 100);
    assertTrue(n.getProperty("field2").asBoolean());    
  }

}
