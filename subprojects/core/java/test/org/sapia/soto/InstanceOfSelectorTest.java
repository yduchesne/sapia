/*
 * InstanceOfSelectorTest.java
 * JUnit based test
 *
 * Created on August 19, 2005, 9:27 AM
 */

package org.sapia.soto;

import junit.framework.TestCase;

/**
 *
 * @author yduchesne
 */
public class InstanceOfSelectorTest extends TestCase {
  
  public InstanceOfSelectorTest(String testName) {
    super(testName);
  }

  /**
   * Test of accepts method, of class org.sapia.soto.InstanceOfSelector.
   */
  public void testAccepts() {
    InstanceOfSelector ios = new InstanceOfSelector(TestService.class);
    ServiceMetaData smd = new ServiceMetaData(new SotoContainer(), null, new TestServiceImpl());
    super.assertTrue(ios.accepts(smd));
    smd = new ServiceMetaData(new SotoContainer(), null, new TestServiceImplEx());
    super.assertTrue(ios.accepts(smd));
  }
  
  public static interface TestService{
  }
  
  static class TestServiceImpl implements TestService{
  }  
  
  static class TestServiceImplEx extends TestServiceImpl{
  }    
  
}
