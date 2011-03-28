package org.sapia.soto;

import java.io.File;
import java.util.Properties;

import junit.framework.TestCase;

public class SettingsTest extends TestCase {
  
  private Settings settings;

  public SettingsTest(String arg0) {
    super(arg0);
  }
  
  protected void setUp() throws Exception {
    settings = new Settings();
    settings.addBoolean("testBoolean", true);
    settings.addInt("testInt", 1);
    settings.addLong("testLong", 1);
    settings.addFloat("testFloat", 1);
    settings.addDouble("testDouble", 1);    
  }
  
  public void testBoolean(){
    assertTrue(settings.getBoolean("testBoolean"));
    assertTrue(settings.getBoolean("testBoolean1", true));
  }
  
  public void testInt(){
    assertEquals(1, settings.getInt("testInt"));
    assertEquals(2, settings.getInt("testInt1", 2));    
  }

  public void testLong(){
    assertEquals(1, settings.getLong("testLong"));
    assertEquals(2, settings.getLong("testLong1", 2));    
  }  
  
  public void testFloat(){
    assertTrue(1f ==  settings.getFloat("testFloat"));
    assertTrue(2f == settings.getFloat("testFloat1", 2));    
  }  

  public void testDouble(){
    assertTrue(1 ==  settings.getDouble("testDouble"));
    assertTrue(2 == settings.getDouble("testDouble1", 2));    
  }  
  
  public void testGetProperties(){
    Properties props = settings.getProperties();
    assertEquals("1", props.getProperty("testInt"));
    assertEquals("1", props.getProperty("testLong"));
    assertEquals("1.0", props.getProperty("testFloat"));
    assertEquals("1.0", props.getProperty("testDouble"));
    assertEquals("true", props.getProperty("testBoolean"));    
  }
  
  public void testExists(){
    assertTrue(settings.exists("testBoolean"));
  }
  
  public void testLoad() throws Exception{
    SotoContainer soto = new SotoContainer();
    soto.load(new File("etc/test/types.xml"));
    soto.start();
  }

}
