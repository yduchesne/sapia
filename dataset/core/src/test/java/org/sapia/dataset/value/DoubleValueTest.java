package org.sapia.dataset.value;

import static org.junit.Assert.*;

import org.junit.Test;

public class DoubleValueTest {

  @Test
  public void testDoubleValueNumber() {
    DoubleValue value = new DoubleValue(new Integer(1));
    assertEquals(1d, value.get(), 0);
  }
  
  @Test
  public void testDoubleValueNumberNull() {
    DoubleValue value = new DoubleValue(null);
    assertEquals(0d, value.get(), 0);
  }

  @Test
  public void testDoubleValueDouble() {
    DoubleValue value =  new DoubleValue(1);
    assertEquals(1d, value.get(), 0);
  }


  @Test
  public void testEqualsObject() {
    DoubleValue expected = new DoubleValue(1);
    DoubleValue actual   = new DoubleValue(1);
    assertEquals(expected, actual);
  }

}
