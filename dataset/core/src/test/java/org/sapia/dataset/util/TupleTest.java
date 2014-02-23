package org.sapia.dataset.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TupleTest {

  private Tuple tuple;
  
  @Before
  public void setUp() {
    tuple = new Tuple(0, 1 ,2);
  }
  
  @Test
  public void testLength() {
    assertEquals(3, tuple.length());
  }

  @Test
  public void testGetValue() {
    for (int i : Numbers.range(3)) {
      assertEquals(i, tuple.get(i));
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetNotNull() {
    tuple = new Tuple(new Object[3]);
    tuple.getNotNull(0);
  }
  
  @Test
  public void testGetNotNullOk() {
    for (int i : Numbers.range(3)) {
      assertEquals(i, tuple.getNotNull(i));
    }
  }

  @Test
  public void testGetIntClassOf() {
    for (int i : Numbers.range(3)) {
      assertEquals(new Integer(i), tuple.get(i, Integer.class));
    }
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testGetNotNullClassOf() {
    tuple = new Tuple(new Object[3]);
    tuple.getNotNull(0, Integer.class);
  }

  @Test
  public void testGetNotNullClassOfOk() {
    for (int i : Numbers.range(3)) {
      assertEquals(new Integer(i), tuple.getNotNull(i, Integer.class));
    }
  }
  
  @Test
  public void testGetForType() {
    tuple = new Tuple("foo", 1);
    assertEquals("foo", tuple.get(String.class));
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testGetForTypeAmbiguous() {
    tuple = new Tuple("foo", "bar");
    tuple.get(String.class);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testGetNotNullForType() {
    tuple = new Tuple("foo");
    tuple.getNotNull(Integer.class);
  }

  @Test
  public void testIterator() {
    int count = 0;
    for (Object o : tuple) {
      count++;
    }
    assertEquals(3, count);
  }

}
