package org.sapia.dataset.transform.view;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.sapia.dataset.Vector;
import org.sapia.dataset.impl.DefaultVector;
import org.sapia.dataset.util.Numbers;

public class ViewVectorTest {
  
  private ViewVector vector;
  
  @Before
  public void setUp() {
    Vector delegate = new DefaultVector(0, 1, 2, 3, 4);
    vector = new ViewVector(new int[] {0, 2, 4}, delegate);
  }

  @Test
  public void testGet() {
    int[] expected = new int[] {0, 2, 4};
    for (int i : Numbers.range(2)) {
      assertEquals(expected[i], vector.get(i));
    }
  }

  @Test
  public void testSize() {
    assertEquals(3, vector.size());
  }

  @Test
  public void testIterator() {
    int i = 0;
    int[] expected = new int[] {0, 2, 4};
    for (Object o : vector) {
      assertEquals(new Integer(expected[i++]), (Integer) o);
    }
  }

  @Test
  public void testSubset() {
    Vector subset = vector.subset(1);
    assertEquals(new Integer(2), (Integer) subset.get(0));
  }

  @Test
  public void testToArray() {
    Object[] values = vector.toArray();
    int[] expected = new int[] {0, 2, 4};
    for (int i : Numbers.range(vector.size())) {
      assertEquals(new Integer(expected[i]), (Integer) values[i]);
    }
  }

}
