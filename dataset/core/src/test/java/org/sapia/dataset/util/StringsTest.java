package org.sapia.dataset.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class StringsTest {

  @Test
  public void testCenter() {
    String toCenter = "1234";
    assertEquals("  1234  ", Strings.center(toCenter, 8));
  }
  
  @Test
  public void testCenterTooLong() {
    String toCenter = "1234";
    assertEquals("1234", Strings.center(toCenter, 4));
  }

  @Test
  public void testRepeat() {
    assertEquals("====", Strings.repeat("=", 4));
  }

  @Test
  public void testRpad() {
    assertEquals("1234  ", Strings.rpad("1234", " ", 6));
  }

  @Test
  public void testLpad() {
    assertEquals("  1234", Strings.lpad("1234", " ", 6));
  }

  @Test
  public void testIsNullOrEmpty() {
    assertTrue(Strings.isNullOrEmpty(""));
    assertTrue(Strings.isNullOrEmpty(null));
    assertFalse(Strings.isNullOrEmpty(" "));
  }

  @Test
  public void testCapitalizeFirst() {
    assertEquals("C", Strings.capitalizeFirst("c"));
    assertEquals("Cc", Strings.capitalizeFirst("cc"));
  }
  
  @Test
  public void testConcat() {
    assertEquals("123", Strings.concat("1", "2", "3"));
  }
}
