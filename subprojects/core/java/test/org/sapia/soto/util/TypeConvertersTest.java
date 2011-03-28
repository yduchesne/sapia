package org.sapia.soto.util;

import junit.framework.TestCase;

public class TypeConvertersTest extends TestCase {

  public TypeConvertersTest(String arg0) {
    super(arg0);
  }

  public void testString(){
    assertEquals("test", ((String)TypeConverters.convert("test", String.class)));
  }  
  
  public void testBoolean(){
    assertTrue(((Boolean)TypeConverters.convert("true", boolean.class)).booleanValue());
    assertTrue(((Boolean)TypeConverters.convert("true", Boolean.class)).booleanValue());    
  }
  
  public void testShort(){
    assertEquals(1, ((Short)TypeConverters.convert("1", short.class)).shortValue());
    assertEquals(1, ((Short)TypeConverters.convert("1", Short.class)).shortValue());    
  }  
  
  public void testInt(){
    assertEquals(1, ((Integer)TypeConverters.convert("1", int.class)).intValue());
    assertEquals(1, ((Integer)TypeConverters.convert("1", Integer.class)).intValue());    
  }  
  
  public void testLong(){
    assertEquals(1, ((Long)TypeConverters.convert("1", long.class)).longValue());
    assertEquals(1, ((Long)TypeConverters.convert("1", Long.class)).longValue());    
  }  
  
  public void testFloat(){
    assertEquals(1, ((Float)TypeConverters.convert("1", float.class)).longValue());
    assertEquals(1, ((Float)TypeConverters.convert("1", Float.class)).longValue());    
  }  
  
  public void testDouble(){
    assertEquals(1, ((Double)TypeConverters.convert("1", double.class)).longValue());
    assertEquals(1, ((Double)TypeConverters.convert("1", Double.class)).longValue());    
  }  

}
