/*
 * FormTest.java
 * JUnit based test
 *
 * Created on September 3, 2005, 7:50 AM
 */

package org.sapia.soto.state.form;

import junit.framework.*;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author yduchesne
 */
public class FormTest extends TestCase {
  
  public FormTest(String testName) {
    super(testName);
  }

  /**
   * Test of getId method, of class org.sapia.soto.state.form.Form.
   */
  public void testGetId() {
    Form f = new Form(1);
    super.assertEquals(1, f.getId());
  }

  /**
   * Test of setObject method, of class org.sapia.soto.state.form.Form.
   */
  public void testSetGetObject() {
    Form f = new Form(0);
    f.setObject("test");
    super.assertEquals("test", f.getObject());    
  }

  /**
   * Test of put method, of class org.sapia.soto.state.form.Form.
   */
  public void testPutGet() {
    Form f = new Form(0);
    f.put("key", "value");
    super.assertEquals("value", f.get("key"));      
  }

}
