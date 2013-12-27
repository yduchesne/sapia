/*
 * VariablesTest.java
 * JUnit based test
 *
 * Created on November 8, 2005, 3:47 PM
 */

package org.sapia.soto.configuration.jconfig;

import java.io.File;
import java.io.InputStream;

import junit.framework.TestCase;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.sapia.soto.SotoContainer;
import org.sapia.soto.configuration.ConfigurationService;
import org.sapia.soto.configuration.example.ConfigExample;

/**
 *
 * @author yduchesne
 */
public class VariablesTest extends TestCase {
  
  public VariablesTest(String testName) {
    super(testName);
  }
  
  public void testConfigure() throws Exception{
    SotoContainer cont = new SotoContainer();
    cont.load(new File("etc/jconfig/appVars.xml"));
    cont.start();
    ConfigurationService conf = (ConfigurationService)cont.lookup(ConfigurationService.class);
    super.assertEquals("foo", conf.getProperty("test", "account", "username"));
    super.assertEquals("bar", conf.getProperty("test", "account", "password"));
    super.assertEquals("foo", conf.getProperty("test", "account", "alias"));
    super.assertEquals("manager", conf.getProperty("test", "account", "role"));
    
    ConfigExample eg = (ConfigExample)cont.lookup("configured");
    super.assertEquals(10, eg.intValue);
    super.assertEquals("someText", eg.textValue);
  }
  
  public void testConfigureXML() throws Exception{
    SotoContainer cont = new SotoContainer();
    cont.load(new File("etc/jconfig/appVars.xml"));
    cont.start();
    InputStream is; //cont.toEnv().resolveStream("test/account/config.xml");
    /*SAXReader reader = new SAXReader();
    try{
      Document doc = reader.read(is);
      super.assertEquals("foo", doc.getRootElement().element("username").attribute("value").getText());
      super.assertEquals("bar", doc.getRootElement().element("password").attribute("value").getText());
      
    }finally{
      is.close();
    }*/
    
    is = cont.toEnv().resolveStream("jconfig:/test/account/config.xml");
    SAXReader reader = new SAXReader();
    try{
      Document doc = reader.read(is);
      super.assertEquals("foo", doc.getRootElement().element("username").attribute("value").getText());
      super.assertEquals("bar", doc.getRootElement().element("password").attribute("value").getText());
      
    }finally{
      is.close();
    }    
    
  }  
  
}
