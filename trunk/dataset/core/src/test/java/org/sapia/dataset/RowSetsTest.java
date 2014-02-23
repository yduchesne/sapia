package org.sapia.dataset;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.sapia.dataset.util.Data;

public class RowSetsTest {
  
  private RowSet rowset;
  
  @Before
  public void setUp() {

    rowset = RowSets.rowSet(new Object[][] {
       Data.array(1d, 2d, 3d),
       Data.array(1d, 2d, 3d) 
    });
  }

  @Test
  public void testSumRowSet() {
    Vector sum = RowSets.sum(rowset);
    assertEquals(2d, ((Double)sum.get(0)).doubleValue(), 0);
    assertEquals(4d, ((Double)sum.get(1)).doubleValue(), 0);
    assertEquals(6d, ((Double)sum.get(2)).doubleValue(), 0);
  }

  @Test
  public void testSumRowSetWithStartVectorIndex() {
    Vector sum = RowSets.sum(1, rowset);
    assertEquals(0d, ((Double)sum.get(0)).doubleValue(), 0);
    assertEquals(4d, ((Double)sum.get(1)).doubleValue(), 0);
    assertEquals(6d, ((Double)sum.get(2)).doubleValue(), 0);
  }

}
