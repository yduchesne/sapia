package org.sapia.dataset.impl;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.sapia.dataset.Vector;
import org.sapia.dataset.util.Data;

public class DefaultVectorTest {

  private DefaultVector vector;
  
  @Before
  public void setUp() {
    vector = new DefaultVector(0, 1, 2, 3);
  }
  
  @Test
  public void testGet() {
    assertEquals(new Integer(0), vector.get(0));
    assertEquals(new Integer(1), vector.get(1));
    assertEquals(new Integer(2), vector.get(2));
    assertEquals(new Integer(3), vector.get(3));
  }

  @Test
  public void testSubset() {
    Vector subset = vector.subset(1, 2);
    assertEquals(new Integer(1), subset.get(0));
    assertEquals(new Integer(2), subset.get(1));
  }

  @Test
  public void testSize() {
    assertEquals(4, vector.size());
  }

  @Test
  public void testIterator() {
    List<Object> values = Data.list(vector.iterator());
    assertEquals(new Integer(0), values.get(0));
    assertEquals(new Integer(1), values.get(1));
    assertEquals(new Integer(2), values.get(2));
    assertEquals(new Integer(3), values.get(3));
  }

}
