package org.sapia.soto.log;

import java.io.File;

import junit.framework.TestCase;

import org.sapia.soto.SotoContainer;

/**
 * 
 *
 * @author Jean-Cedric Desrochers
 */
public class Log4jServiceTest extends TestCase {

  public Log4jServiceTest(String name){ super(name);}

  // Fixture
  private SotoContainer _soto;
  private Log4jService _service;
  
  public void setUp() throws Exception {
    _soto = new SotoContainer();
    _soto.load(new File("etc/log/main.soto.xml"));
    _soto.start();
    
    _service = (Log4jService) _soto.lookup(LogService.class);
  }

  public void tearDown() {
    _soto.dispose();
  }
  
  public void testDefinition() {
    assertNotNull("The logger def 'com' should not be null", _service.getLoggerDef("com"));
    assertNotNull("The logger def 'org' should not be null", _service.getLoggerDef("org"));
    assertNull("The logger def 'foo' should be null - it was not defined", _service.getLoggerDef("foo"));
  }
}
