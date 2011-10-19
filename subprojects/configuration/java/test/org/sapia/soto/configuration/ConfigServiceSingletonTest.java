package org.sapia.soto.configuration;

import java.io.File;

import junit.framework.TestCase;

import org.sapia.soto.SotoContainer;

/**
 * 
 *
 * @author Jean-Cedric Desrochers
 */
public class ConfigServiceSingletonTest extends TestCase {

  // Fixture
  private SotoContainer _soto;
  private ConfigurationService _service;
  

  public void setUp() throws Exception {
    _soto = new SotoContainer();
    _soto.load(new File("etc/jconfig/multipleConfig.soto.xml"));
    _soto.start();
    
    _service = (ConfigurationService) _soto.lookup(ConfigurationService.class);
  }
  
  public void tearDown() {
    _soto.dispose();
  }
  
  
  public void testRetrieveValidCategory() {
    ConfigCategory catA = _service.getCategory("one", "catA");
    assertNotNull("The retrieved category should not be null", catA);
    assertEquals("The name of the category is invalid", "catA", catA.getName());
    assertEquals("The value of the 'value_1' property is invalid", "10", catA.getProperty("value_1"));

    ConfigCategory catA_1 = _service.getCategory("one", "catA/catA_1");
    assertNotNull("The retrieved category should not be null", catA_1);
    assertEquals("The name of the category is invalid", "catA_1", catA_1.getName());
    assertEquals("The value of the 'value_1' property is invalid", "20", catA_1.getProperty("value_1"));

    catA_1 = catA.getCategory("catA_1");
    assertNotNull("The retrieved category should not be null", catA_1);
    assertEquals("The name of the category is invalid", "catA_1", catA_1.getName());
    assertEquals("The value of the 'value_1' property is invalid", "20", catA_1.getProperty("value_1"));

    ConfigCategory catA_3 = _service.getCategory("two", "catA/catA_3");
    assertNotNull("The retrieved category should not be null", catA_3);
    assertEquals("The name of the category is invalid", "catA_3", catA_3.getName());
    assertEquals("The value of the 'value_2' property is invalid", "50", catA_3.getProperty("value_2"));
  }
}
