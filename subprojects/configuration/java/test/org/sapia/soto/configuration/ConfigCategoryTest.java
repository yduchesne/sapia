package org.sapia.soto.configuration;

import java.io.File;

import org.sapia.soto.SotoContainer;
import org.sapia.soto.configuration.example.ConfigExample;
import org.sapia.soto.util.Utils;

import junit.framework.TestCase;

/**
 * 
 *
 * @author Jean-Cedric Desrochers
 */
public class ConfigCategoryTest extends TestCase {

  // Fixture
  private SotoContainer _soto;
  private ConfigurationService _service;
  

  public void setUp() throws Exception {
    _soto = new SotoContainer();
    _soto.load(new File("etc/jconfig/app.xml"));
    _soto.start();
    
    _service = (ConfigurationService) _soto.lookup(ConfigurationService.class);
  }
  
  public void tearDown() {
    _soto.dispose();
  }
  
  public void testRetrieveInvalidCategory() {
    assertNull("Should not retrieve a category from an invalid config name", _service.getCategory("foo", "bar"));
    assertNull("Should not retrieve a category from an invalid category name", _service.getCategory("test", "bar"));
    assertNull("Should not retrieve a category from an invalid category name", _service.getCategory("test", "catA/bar"));
  }

  
  public void testRetrieveValidCategory() {
    ConfigCategory catA = _service.getCategory("test", "catA");
    assertNotNull("The retrieved category should not be null", catA);
    assertEquals("The name of the category is invalid", "catA", catA.getName());
    assertEquals("The value of the 'value_1' property is invalid", "10", catA.getProperty("value_1"));

    ConfigCategory catA_1 = _service.getCategory("test", "catA/catA_1");
    assertNotNull("The retrieved category should not be null", catA_1);
    assertEquals("The name of the category is invalid", "catA_1", catA_1.getName());
    assertEquals("The value of the 'value_1' property is invalid", "20", catA_1.getProperty("value_1"));

    catA_1 = catA.getCategory("catA_1");
    assertNotNull("The retrieved category should not be null", catA_1);
    assertEquals("The name of the category is invalid", "catA_1", catA_1.getName());
    assertEquals("The value of the 'value_1' property is invalid", "20", catA_1.getProperty("value_1"));

    ConfigCategory catA_2 = _service.getCategory("test", "catA/catA_2");
    assertNotNull("The retrieved category should not be null", catA_2);
    assertEquals("The name of the category is invalid", "catA_2", catA_2.getName());
    assertEquals("The value of the 'value_2' property is invalid", "30", catA_2.getProperty("value_2"));

    catA_2 = catA.getCategory("catA_2");
    assertNotNull("The retrieved category should not be null", catA_2);
    assertEquals("The name of the category is invalid", "catA_2", catA_2.getName());
    assertEquals("The value of the 'value_2' property is invalid", "30", catA_2.getProperty("value_2"));
  }
  
  public void testCategoryTag() throws Exception {
    ConfigExample ex = (ConfigExample) _soto.lookup("configured");
    assertEquals(10, ex.intValue);
    assertEquals("<foo>bar</foo>", ex.textValue.trim());
    String txt = Utils.textStreamToString(ex.resValue.getInputStream());
    assertEquals("<foo>bar</foo>", txt.trim());
    
    assertNotNull("The config category should not be null", ex.category);
    assertEquals("The name of the config category is invalid", "catA_2", ex.category.getName());
    assertEquals("The value of the 'value_2' property is invalid", "30", ex.category.getProperty("value_2"));
  }
  
  public void testLoad() throws Exception{
    
  }

}
