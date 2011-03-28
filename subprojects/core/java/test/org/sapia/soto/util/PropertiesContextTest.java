package org.sapia.soto.util;

import java.util.Properties;

import org.sapia.util.text.SystemContext;

import junit.framework.TestCase;

public class PropertiesContextTest extends TestCase {

  public PropertiesContextTest(String arg0) {
    super(arg0);
  }
  
  public void testGetValue(){
    PropertiesContext ctx = new PropertiesContext(new SystemContext(), false);
    super.assertTrue(ctx.getValue("user.dir") != null);
    Properties props = new Properties();
    props.setProperty("user.dir", "some-test-value");
    ctx.addProperties(props);
    super.assertEquals("some-test-value", ctx.getValue("user.dir"));
    props = new Properties();
    props.setProperty("user.dir", "some-test-value2");
    ctx.addProperties(props);
    super.assertEquals("some-test-value2", ctx.getValue("user.dir"));
    
    ctx = new PropertiesContext(new SystemContext(), true);
    props = new Properties();
    props.setProperty("user.dir", "some-test-value");    
    ctx.addProperties(props);
    super.assertTrue(!ctx.getValue("user.dir").equals("some-test-value"));
  }
  
}
