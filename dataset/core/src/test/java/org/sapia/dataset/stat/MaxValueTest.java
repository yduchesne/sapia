package org.sapia.dataset.stat;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class MaxValueTest {

  private MaxValue max;
  
  @Before
  public void setUp() {
    max = new MaxValue();
  }
  
  @Test
  public void testSet() {
    max.set(10);
    assertEquals(10, max.get(), 0);
    max.set(5);
    assertEquals(10, max.get(), 0);
    max.set(20);
    assertEquals(20, max.get(), 0);
  }

}
