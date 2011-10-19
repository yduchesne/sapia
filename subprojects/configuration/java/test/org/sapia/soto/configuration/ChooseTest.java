/*
 * ConfigPropertyTest.java
 * JUnit based test
 *
 * Created on August 10, 2005, 1:45 PM
 */

package org.sapia.soto.configuration;

import java.io.File;

import junit.framework.TestCase;
import org.sapia.soto.NotFoundException;

import org.sapia.soto.SotoContainer;
import org.sapia.soto.configuration.example.ConfigExample;
import org.sapia.soto.util.Utils;

/**
 *
 * @author yduchesne
 */
public class ChooseTest extends TestCase {
  
  public ChooseTest(String testName) {
    super(testName);
  }
  
  public void testConfigure() throws Exception{
    SotoContainer cont = new SotoContainer();
    cont.load(new File("etc/jconfig/conditionalConfig.soto.xml"));
    cont.start();
    super.assertTrue(cont.lookup("if-test") instanceof TestInstanceA);    
    try{
      cont.lookup("if-test-false");
      fail("Should not have found service for if-test-false");
    }catch(NotFoundException e){}
    
    try{
      cont.lookup("unless-test");
      fail("Should not have found service for unless-test");
    }catch(NotFoundException e){}
    super.assertTrue(cont.lookup("first") instanceof TestInstanceA);
    super.assertTrue(cont.lookup("second") instanceof TestInstanceB);    
  }
  
}
