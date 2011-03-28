/*
 * MapTypeTest.java
 * JUnit based test
 *
 * Created on June 28, 2005, 2:52 PM
 */

package org.sapia.soto.config.types;

import java.util.Map;

import junit.framework.TestCase;

/**
 *
 * @author yduchesne
 */
public class MapTypeTest extends TestCase {
  
  public MapTypeTest(String testName) {
    super(testName);
  }

  protected void setUp() throws Exception {
  }
  
  /**
   * Test of onCreate method, of class org.sapia.soto.config.types.MapType.
   */
  public void testOnCreate() throws Exception{
    MapType type = new MapType();
    for(int i = 0; i < 5; i++){
      MapType.MapParam param = type.createEntry();
      param.setKey("key"+i);
      param.setValue("value"+i);;
      param.onCreate();
    }
    Map map = (Map)type.onCreate();
    for(int i = 0; i < 5; i++){
      assertEquals("value"+i,  map.get("key"+i));
    }    
  }
  
}
