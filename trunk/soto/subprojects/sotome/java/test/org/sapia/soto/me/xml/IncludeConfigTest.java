package org.sapia.soto.me.xml;

import junit.framework.TestCase;

import org.sapia.soto.me.SotoMeContainer;

/**
 *
 * @author Jean-Cédric Desrochers
 */
public class IncludeConfigTest extends TestCase {

  private static String _RESOURCE_NAME = "/org/sapia/soto/me/xml/IncludeConfig.soto.xml";
  private SotoMeContainer _sotoMe;
  
  public void setUp() {
    System.setProperty("soto.debug", "true");
    System.setProperty("bof_message", "Greetings");
    System.setProperty("eof_message", "Ciao");
    System.setProperty("param.timezone", "UNSET");
    _sotoMe = new SotoMeContainer();
  }
  
  public void testProcessing() throws Exception {
    _sotoMe.setProperty("param.timezone", "GMT");
    _sotoMe.load(_RESOURCE_NAME);
    _sotoMe.start();
    Thread.sleep(100);
    _sotoMe.pause();
    Thread.sleep(100);
    _sotoMe.start();
    Thread.sleep(100);
    _sotoMe.dispose();
  }
}
