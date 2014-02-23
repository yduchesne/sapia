package org.sapia.dataset.value;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class MutableValueTest {

  private MutableValue value;
  
  @Before
  public void setUp() {
    value = new MutableValue();
    value.set(1);
  }
  
  @Test
  public void testGet() {
    assertEquals(1d, value.get(), 0);
  }

  @Test
  public void testIncrease() {
    value.increase(2);
    assertEquals(3d, value.get(), 0);
  }

  @Test
  public void testSet() {
    value.set(2);
    assertEquals(2d, value.get(), 0);
  }

  @Test
  public void testEqualsObject() {
    MutableValue actual = new MutableValue();
    actual.set(1);
    assertEquals(value, actual);
    actual.increase(2);
    assertNotSame(value, actual);
  }

}
