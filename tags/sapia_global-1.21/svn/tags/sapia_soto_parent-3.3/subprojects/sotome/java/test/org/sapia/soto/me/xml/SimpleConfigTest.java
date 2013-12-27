package org.sapia.soto.me.xml;

import org.sapia.soto.me.SotoMeContainer;

import junit.framework.TestCase;

/**
 *
 * @author Jean-Cédric Desrochers
 */
public class SimpleConfigTest extends TestCase {

  private SotoMeContainer _sotoMe;
  
  public void setUp() {
    System.setProperty("soto.debug", "true");
    System.setProperty("bof_message", "Greetings");
    System.setProperty("eof_message", "Ciao");
    _sotoMe = new SotoMeContainer();
  }
  
  public void testProcessing() throws Exception {
    _sotoMe.load("/org/sapia/soto/me/xml/SimpleConfig.soto.xml");
    _sotoMe.start();
    Thread.sleep(300);
    _sotoMe.pause();
    Thread.sleep(300);
    _sotoMe.start();
    Thread.sleep(300);
    _sotoMe.dispose();
  }
}
