package org.sapia.dataset.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.sapia.dataset.util.Settings.Setting;

public class SettingsTest {

  private Settings settings;
  
  @Before
  public void setUp() {
    settings = Settings.obj();
  }
  
  @Test
  public void testDeclare() {
    Setting s = settings.setting();
    s.name("test").description("desc").type(String.class).finish();
    s = settings.get("test");
    assertEquals(String.class, s.getType());
  }
  
  @Test
  public void testDeclareMultipleSettings() {
    Setting s = settings.setting();
    s.name("test").description("desc").type(String.class)
    .setting().name("test2").description("desc2").type(String.class)
    .finish();
    settings.get("test");
    settings.get("test2");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetUndeclaredSetting() {
    settings.get("test");
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testDeclareSettingWithMissingName() {
    settings.setting().description("desc").type(Double.class).finish();
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testDeclareSettingWithMissingType() {
    settings.setting().name("test").description("desc").finish();
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testDeclareSettingWithMissingDescription() {
    settings.setting().name("test").type(Double.class).finish();
  }
}
