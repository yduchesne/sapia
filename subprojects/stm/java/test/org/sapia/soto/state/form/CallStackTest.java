/*
 * CallStackTest.java
 * JUnit based test
 *
 * Created on September 3, 2005, 8:05 AM
 */

package org.sapia.soto.state.form;

import junit.framework.*;
import java.util.Stack;

/**
 *
 * @author yduchesne
 */
public class CallStackTest extends TestCase {
  
  public CallStackTest(String testName) {
    super(testName);
  }

  /**
   * Test of peek method, of class org.sapia.soto.state.form.CallStack.
   */
  public void testPeek() {
    CallStack st = new CallStack(new IdFactory());
    super.assertTrue(st.peek() == null);
    Call c = st.create();
    super.assertEquals(c, st.peek());
  }

  /**
   * Test of pop method, of class org.sapia.soto.state.form.CallStack.
   */
  public void testPop() {
    CallStack st = new CallStack(new IdFactory());
    super.assertTrue(st.peek() == null);
    Call c = st.create();
    super.assertEquals(c, st.peek());
    st.pop();
    super.assertTrue(st.peek() == null);
  }

}
