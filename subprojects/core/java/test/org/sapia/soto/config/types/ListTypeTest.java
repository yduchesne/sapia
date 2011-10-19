/*
 * ListTypeTest.java
 * JUnit based test
 *
 * Created on June 28, 2005, 2:45 PM
 */

package org.sapia.soto.config.types;

import java.util.List;

import junit.framework.TestCase;

/**
 *
 * @author yduchesne
 */
public class ListTypeTest extends TestCase {
  
  public ListTypeTest(String testName) {
    super(testName);
  }

  protected void setUp() throws Exception {
  }


  /**
   * Test of onCreate method, of class org.sapia.soto.config.types.ListType.
   */
  public void testOnCreate() throws Exception{
    ListType type = new ListType();
    for(int i = 0; i < 5; i++){
      type.handleObject("", ""+i);
    }
    List list = (List)type.onCreate();
    for(int i = 0; i < 5; i++){
      assertEquals(""+i, list.get(i));
    }    
  }
}
