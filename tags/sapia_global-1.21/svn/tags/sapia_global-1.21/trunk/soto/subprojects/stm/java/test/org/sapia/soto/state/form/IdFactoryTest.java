/*
 * IdFactoryTest.java
 * JUnit based test
 *
 * Created on September 3, 2005, 8:08 AM
 */

package org.sapia.soto.state.form;

import junit.framework.*;

/**
 *
 * @author yduchesne
 */
public class IdFactoryTest extends TestCase {
  
  public IdFactoryTest(String testName) {
    super(testName);
  }

  /**
   * Test of id method, of class org.sapia.soto.state.form.IdFactory.
   */
  public void testId() {
    IdFactory fac = new IdFactory();
    int id1 = fac.id();
    int id2 = fac.id();
    super.assertTrue(id2 > id1);
  }
  
}
