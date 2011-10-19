package org.sapia.soto.me.xml;

import org.sapia.soto.me.NotFoundException;
import org.sapia.soto.me.SotoMeContainer;

import junit.framework.TestCase;

/**
 *
 * @author Jean-Cédric Desrochers
 */
public class SwitchConditionalConfigTest extends TestCase {

  private SotoMeContainer _sotoMe;
  
  public void setUp() {
    System.setProperty("soto.debug", "true");
    System.setProperty("bof_message", "Greetings");
    System.setProperty("eof_message", "Ciao");
    _sotoMe = new SotoMeContainer();
  }
  
  public void testProcessing_local() throws Exception {
    System.setProperty("environment", "local");
    _sotoMe.load("/org/sapia/soto/me/xml/SwitchConditionalConfig.soto.xml");
    _sotoMe.start();
    assertNotNull(_sotoMe.lookupService("helloWorld_local"));
    try {
      _sotoMe.lookupService("helloWorld_dev");
      fail();
    } catch (NotFoundException expected) {
    }
    try {
      _sotoMe.lookupService("helloWorld_other");
      fail();
    } catch (NotFoundException expected) {
    }
    assertNotNull(_sotoMe.lookupService("fallback"));
    Thread.sleep(250);
    _sotoMe.pause();
    Thread.sleep(250);
    _sotoMe.start();
    Thread.sleep(250);
    _sotoMe.dispose();
  }
  
  public void testProcessing_dev() throws Exception {
    System.setProperty("environment", "dev");
    _sotoMe.load("/org/sapia/soto/me/xml/SwitchConditionalConfig.soto.xml");
    _sotoMe.start();
    assertNotNull(_sotoMe.lookupService("helloWorld_dev"));
    try {
      _sotoMe.lookupService("helloWorld_local");
      fail();
    } catch (NotFoundException expected) {
    }
    try {
      _sotoMe.lookupService("helloWorld_other");
      fail();
    } catch (NotFoundException expected) {
    }
    assertNotNull(_sotoMe.lookupService("fallback"));
    Thread.sleep(250);
    _sotoMe.pause();
    Thread.sleep(250);
    _sotoMe.start();
    Thread.sleep(250);
    _sotoMe.dispose();
  }
  
  public void testProcessing_prod() throws Exception {
    System.setProperty("environment", "prod");
    _sotoMe.load("/org/sapia/soto/me/xml/SwitchConditionalConfig.soto.xml");
    _sotoMe.start();
    assertNotNull(_sotoMe.lookupService("helloWorld_other"));
    try {
      _sotoMe.lookupService("helloWorld_local");
      fail();
    } catch (NotFoundException expected) {
    }
    try {
      _sotoMe.lookupService("helloWorld_dev");
      fail();
    } catch (NotFoundException expected) {
    }
    assertNotNull(_sotoMe.lookupService("fallback"));
    Thread.sleep(250);
    _sotoMe.pause();
    Thread.sleep(250);
    _sotoMe.start();
    Thread.sleep(250);
    _sotoMe.dispose();
  }
}
