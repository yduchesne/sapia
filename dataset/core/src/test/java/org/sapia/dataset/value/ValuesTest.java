package org.sapia.dataset.value;

import static org.junit.Assert.*;

import org.junit.Test;

public class ValuesTest {

  @Test
  public void testDoubleValueNull() {
    assertEquals(0d, Values.doubleValue(null), 0);
  }
  
  @Test
  public void testDoubleValueNullValue() {
    assertEquals(0d, Values.doubleValue(NullValue.getInstance()), 0);
  }
  
  @Test
  public void testDoubleValueNumber() {
    assertEquals(1d, Values.doubleValue(new Double(1)), 0);
  }
  
  @Test
  public void testDoubleValueObject() {
    assertEquals(0d, Values.doubleValue(new Object()), 0);
  }

}
