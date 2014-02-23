package org.sapia.dataset.impl;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.sapia.dataset.Vector;
import org.sapia.dataset.util.Data;
import org.sapia.dataset.util.Numbers;

public class DefaultRowSetTest {
  
  private DefaultRowSet rowset;
  
  @Before
  public void setUp() {
    rowset = new DefaultRowSet(
        Data.list(
          (Vector) new DefaultVector(0),
          (Vector) new DefaultVector(1),
          (Vector) new DefaultVector(2)          
      )
    );
  }

  @Test
  public void testGet() {
    for (int index : Numbers.range(3)) {
      assertEquals(index, rowset.get(index).get(0));
    }
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testGetIndexTooLarge() {
    rowset.get(3); 
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testGetIndexTooSmall() {
    rowset.get(-1); 
  }

  @Test
  public void testSize() {
    assertEquals(3, rowset.size());
  }

  @Test
  public void testIterator() {
    List<Vector> rows = Data.list(rowset.iterator());
    for (int index : Numbers.range(3)) {
      assertEquals(index, rows.get(index).get(0));
    }
  }

}
