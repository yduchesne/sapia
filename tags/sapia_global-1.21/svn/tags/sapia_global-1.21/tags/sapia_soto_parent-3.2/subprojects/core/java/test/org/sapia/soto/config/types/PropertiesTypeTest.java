/*
 * MapTypeTest.java
 * JUnit based test
 *
 * Created on June 28, 2005, 2:52 PM
 */

package org.sapia.soto.config.types;

import java.util.Properties;

import junit.framework.TestCase;

/**
 *
 * @author yduchesne
 */
public class PropertiesTypeTest extends TestCase {
  
  public PropertiesTypeTest(String testName) {
    super(testName);
  }

  protected void setUp() throws Exception {
  }
  
  /**
   * Test of onCreate method, of class org.sapia.soto.config.types.MapType.
   */
  public void testOnCreate() throws Exception{
    PropertiesType type = new PropertiesType();
    for(int i = 0; i < 5; i++){
      PropertiesType.PropertyParam param = type.createProperty();
      param.setName("key"+i);
      param.setValue("value"+i);
      param.onCreate();
    }
    Properties props = (Properties)type.onCreate();
    for(int i = 0; i < 5; i++){
      assertEquals("value"+i,  props.getProperty("key"+i));
    }    
  }
  
}
