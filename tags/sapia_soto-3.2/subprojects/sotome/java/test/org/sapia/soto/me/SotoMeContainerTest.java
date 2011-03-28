/**
 * 
 */
package org.sapia.soto.me;

import org.sapia.soto.me.NotFoundException;

import javolution.util.FastMap;
import junit.framework.TestCase;

/**
 *
 * @author Jean-Cédric Desrochers
 */
public class SotoMeContainerTest extends TestCase {

  // Fixtures
  private static String _RESOURCE_NAME = "/org/sapia/soto/me/SotoMeContainerTest.soto.xml";
  private SotoMeContainer _sotoMe;
  private FastMap _sotoMeProps;
  
  public void setUp() {
    System.setProperty("soto.debug", "true");
    _sotoMe = new SotoMeContainer();
    _sotoMeProps = new FastMap();
    _sotoMeProps.put("foo", "bar");
    _sotoMeProps.put("layout", "general");
    _sotoMeProps.put("profile", "junit");
  }
  
  public void testLifeCycle_completeNoPause() throws Exception {
    assertSotoMeContainer(SotoMeLifeCycle.STATE_CREATED, _sotoMe);
    
    _sotoMe.load(_RESOURCE_NAME);
    assertSotoMeContainer(SotoMeLifeCycle.STATE_LOADED, _sotoMe);
    
    _sotoMe.start();
    assertSotoMeContainer(SotoMeLifeCycle.STATE_STARTED, _sotoMe);
    
    _sotoMe.dispose();
    assertSotoMeContainer(SotoMeLifeCycle.STATE_DISPOSED, _sotoMe);
  }
  
  public void testLifeCycle_completeWithPause() throws Exception {
    assertSotoMeContainer(SotoMeLifeCycle.STATE_CREATED, _sotoMe);
    
    _sotoMe.load(_RESOURCE_NAME);
    assertSotoMeContainer(SotoMeLifeCycle.STATE_LOADED, _sotoMe);
    
    _sotoMe.start();
    assertSotoMeContainer(SotoMeLifeCycle.STATE_STARTED, _sotoMe);
    
    _sotoMe.pause();
    assertSotoMeContainer(SotoMeLifeCycle.STATE_PAUSED, _sotoMe);

    _sotoMe.start();
    assertSotoMeContainer(SotoMeLifeCycle.STATE_STARTED, _sotoMe);
    
    _sotoMe.dispose();
    assertSotoMeContainer(SotoMeLifeCycle.STATE_DISPOSED, _sotoMe);
  }
  
  public void testLifeCycle_loadWithProperties() throws Exception {
    assertSotoMeContainer(SotoMeLifeCycle.STATE_CREATED, _sotoMe);
    
    _sotoMe.load(_RESOURCE_NAME, _sotoMeProps);
    assertSotoMeContainer(SotoMeLifeCycle.STATE_LOADED, _sotoMe);
    
    _sotoMe.start();
    assertSotoMeContainer(SotoMeLifeCycle.STATE_STARTED, _sotoMe);
    
    _sotoMe.dispose();
    assertSotoMeContainer(SotoMeLifeCycle.STATE_DISPOSED, _sotoMe);
  }
  
  public void testLifeCycle_startWithoutLoad() throws Exception {
    assertSotoMeContainer(SotoMeLifeCycle.STATE_CREATED, _sotoMe);
    
    try {
      _sotoMe.start();
      fail("Should not start SotoMe container without loading a resource");
    } catch (IllegalStateException expected) {
    }
  }
  
  public void testLifeCycle_pauseWithoutStart() throws Exception {
    assertSotoMeContainer(SotoMeLifeCycle.STATE_CREATED, _sotoMe);
    
    _sotoMe.load(_RESOURCE_NAME);
    assertSotoMeContainer(SotoMeLifeCycle.STATE_LOADED, _sotoMe);
    
    try {
      _sotoMe.pause();
      fail("Should not pause SotoMe container without starting it");
    } catch (IllegalStateException expected) {
    }
  }
  
  public void testLookupService_unknown() throws Exception {
    try {
      _sotoMe.lookupService("invalidName");
      fail("Should not be able to lookup a non-existing service");
    } catch (NotFoundException expected) {
    }
  }
  
  public void testBindObject() throws Exception {
    Object o = new String("OBJECT");
    _sotoMe.bindObject("test/foo", o);
    
    assertEquals("", o, _sotoMe.lookupObject("test/foo"));
  }
  
  // Assertion
  /**
   * 
   * @param eState
   * @param actual
   */
  public static void assertSotoMeContainer(int eState, SotoMeContainer actual) {
    assertNotNull("The SotoMe container passed in should not be null", actual);
    assertEquals("The state of the SotoMe container is invalid", eState, actual.getLifeCycle().getState());
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
