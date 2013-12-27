/*
 * ConfigPropertyTest.java
 * JUnit based test
 *
 * Created on August 10, 2005, 1:45 PM
 */

package org.sapia.soto.configuration;

import java.io.File;

import junit.framework.TestCase;

import org.sapia.soto.SotoContainer;
import org.sapia.soto.configuration.example.ConfigExample;
import org.sapia.soto.util.Utils;

/**
 *
 * @author yduchesne
 */
public class ConfigPropertyTest extends TestCase {
  
  public ConfigPropertyTest(String testName) {
    super(testName);
  }
  
  public void testConfigure() throws Exception{
    SotoContainer cont = new SotoContainer();
    cont.load(new File("etc/jconfig/app.xml"));
    cont.start();
    ConfigExample ex = (ConfigExample)cont.lookup("configured");
    super.assertEquals(10, ex.intValue);
    super.assertEquals("<foo>bar</foo>", ex.textValue.trim());    
    String txt = Utils.textStreamToString(ex.resValue.getInputStream());
    super.assertEquals("<foo>bar</foo>", txt.trim());    
  }
  
  public void testNestConfigure() throws Exception{
    SotoContainer cont = new SotoContainer();
    cont.load(new File("etc/jconfig/nestedApp.xml"));
    cont.start();
    ConfigExample ex = (ConfigExample)cont.lookup("configured");
    super.assertEquals(20, ex.intValue);
  }  
}
