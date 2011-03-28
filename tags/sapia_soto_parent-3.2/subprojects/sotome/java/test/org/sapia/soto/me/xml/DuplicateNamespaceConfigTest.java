package org.sapia.soto.me.xml;

import org.sapia.soto.me.SotoMeContainer;

import junit.framework.TestCase;

/**
 *
 * @author Jean-Cédric Desrochers
 */
public class DuplicateNamespaceConfigTest extends TestCase {

  private SotoMeContainer _sotoMe;
  
  public void setUp() {
    System.setProperty("soto.debug", "true");
    System.setProperty("bof_message", "Greetings");
    System.setProperty("eof_message", "Ciao");
    _sotoMe = new SotoMeContainer();
  }
  
  public void testProcessing() throws Exception {
    try {
      _sotoMe.load("/org/sapia/soto/me/xml/DuplicateNamespaceConfig.soto.xml");
      fail();
    } catch (Exception expected) {
    }
  }
}
