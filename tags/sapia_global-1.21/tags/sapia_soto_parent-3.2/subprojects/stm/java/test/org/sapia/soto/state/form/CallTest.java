/*
 * CallTest.java
 * JUnit based test
 *
 * Created on September 3, 2005, 8:03 AM
 */

package org.sapia.soto.state.form;

import junit.framework.*;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.StateExecException;
import org.sapia.soto.state.StatePath;

/**
 *
 * @author yduchesne
 */
public class CallTest extends TestCase {
  
  public CallTest(String testName) {
    super(testName);
  }

  /**
   * Test of getId method, of class org.sapia.soto.state.form.Call.
   */
  public void testGetId() {
    Call c = new Call(new IdFactory());
    super.assertTrue(c.getId() != 0);
  }

  /**
   * Test of performReturn method, of class org.sapia.soto.state.form.Call.
   */
  public void testPerformReturn() {
  }

  /**
   * Test of getForms method, of class org.sapia.soto.state.form.Call.
   */
  public void testGetForms() {
    Call c = new Call(new IdFactory());
    super.assertTrue(c.getForms() != null);
  }
  
}
