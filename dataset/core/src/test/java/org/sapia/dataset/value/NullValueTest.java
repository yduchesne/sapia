package org.sapia.dataset.value;

import static org.junit.Assert.*;

import org.junit.Test;

public class NullValueTest {
  
  private NullValue value = NullValue.getInstance();

  @Test
  public void testGet() {
    assertEquals(0d, value.get(), 0);
  }

  @Test
  public void testEqualsNull() {
    assertTrue(value.equals(null));
  }
  
  @Test
  public void testEqualsNullValue() {
    assertTrue(value.equals(NullValue.getInstance()));
  }

  @Test
  public void testIsNull() {
    assertTrue(NullValue.isNull(null));
  }
  
  @Test
  public void testIsNullValue() {
    assertTrue(NullValue.isNull(NullValue.getInstance()));
  }

  @Test
  public void testIsNotNull() {
    assertTrue(NullValue.isNotNull(new Object()));
  }

}
