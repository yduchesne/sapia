package org.sapia.dataset.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class SettingValuesTest {

  private SettingValues values;
  
  @Before
  public void setUp() {
    values = SettingValues.obj();
  }
  
  @Test
  public void testSet() {
    values.set("test1", "value1").set("test2", "value2");
    assertEquals("value1", values.get("test1"));
    assertEquals("value2", values.get("test2"));
  }

  @Test
  public void testValueOfListOfObject() {
    values = SettingValues.valueOf(Data.list("test1", "value1", "test2", "value2"));
    assertEquals("value1", values.get("test1"));
    assertEquals("value2", values.get("test2"));
  }

  @Test
  public void testValueOfObjectArray() {
    values = SettingValues.valueOf("test1", "value1", "test2", "value2");
    assertEquals("value1", values.get("test1"));
    assertEquals("value2", values.get("test2"));
  }

}
