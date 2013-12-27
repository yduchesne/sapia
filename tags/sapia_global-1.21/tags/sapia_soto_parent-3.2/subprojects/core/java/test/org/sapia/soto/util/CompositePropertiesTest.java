package org.sapia.soto.util;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import junit.framework.TestCase;

public class CompositePropertiesTest extends TestCase {

  private CompositeProperties props;
  
  public CompositePropertiesTest(String arg0) {
    super(arg0);
  }
  
  protected void setUp() throws Exception {
    props = new CompositeProperties();
    Properties props1 = new Properties();
    props1.setProperty("foo", "bar");
    props.addProperties(props1);
    Properties props2 = new Properties();
    props2.setProperty("sna", "fu");
    props.addProperties(props2);
  }
  
  public void testGetProperty(){
    super.assertEquals("bar", props.getProperty("foo"));
    super.assertEquals("fu", props.getProperty("sna"));
  }
  
  public void testSetProperty(){
    props.setProperty("test", "value");
    super.assertEquals("value", props.getProperty("test"));
  }
  
  public void testPropertyNames(){
    Set names = new HashSet(); 
    Enumeration e = props.propertyNames();
    while(e.hasMoreElements()){
      names.add(e.nextElement());
    }
    super.assertTrue(names.contains("foo"));
    super.assertTrue(names.contains("sna"));
  }

}
