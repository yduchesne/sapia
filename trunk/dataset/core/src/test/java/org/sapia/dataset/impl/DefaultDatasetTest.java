package org.sapia.dataset.impl;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.sapia.dataset.Column;
import org.sapia.dataset.ColumnSet;
import org.sapia.dataset.Dataset;
import org.sapia.dataset.Datatype;
import org.sapia.dataset.RowResult;
import org.sapia.dataset.Vector;
import org.sapia.dataset.algo.Criteria;
import org.sapia.dataset.util.Data;
import org.sapia.dataset.util.Numbers;

public class DefaultDatasetTest {
  
  private DefaultDataset dataset;
  
  @Before
  public void setUp() {
    dataset = new DefaultDataset(
        Data.list(
            (Column) new DefaultColumn(0, Datatype.STRING, "col0"),
            (Column) new DefaultColumn(1, Datatype.STRING, "col1"),
            (Column) new DefaultColumn(2, Datatype.STRING, "col2")
        ),
        Data.list(
            (Vector) new DefaultVector("00", "01", "02"),
            (Vector) new DefaultVector("10", "11", "12"),
            (Vector) new DefaultVector("20", "21", "22")
        )
    );
    
  }

  @Test
  public void testGetColumnSet() {
    ColumnSet cols = dataset.getColumnSet();
    assertEquals(3, cols.size());
    for (int i : Numbers.range(3)) {
      assertEquals(i, cols.get(i).getIndex());
      assertEquals("col" + i, cols.get(i).getName());
    }
  }

  @Test
  public void testGetRow() {
    for (int i : Numbers.range(3)) {
      Vector row = dataset.getRow(i);
      for (int j : Numbers.range(2)) {
        String expected = new StringBuilder().append(i).append(j).toString();
        assertEquals(expected, row.get(j));
      }
    }
  }

  @Test
  public void testSize() {
    assertEquals(dataset.size(), 3);
  }

  @Test
  public void testGetColumnForName() {
    Vector column = dataset.getColumn("col1");
    assertEquals(3, column.size());
    for (int i : Numbers.range(3)) {
      String expected = new StringBuilder().append(i).append("1").toString();
      assertEquals(expected, column.get(i));
    }
  }

  @Test
  public void testGetColumnForIndex() {
    Vector column = dataset.getColumn(1);
    assertEquals(3, column.size());
    for (int i : Numbers.range(3)) {
      String expected = new StringBuilder().append(i).append("1").toString();
      assertEquals(expected, column.get(i));
    }
  }

  @Test
  public void testGetColumnSubsetForIndex() {
    Dataset subset = dataset.getColumnSubset(1, new Criteria<Object>() {
      @Override
      public boolean matches(Object v) {
        return v.equals("11") || v.equals("21");
      }
    });
  
    assertEquals("col1", subset.getColumnSet().get(0).getName());
    assertEquals(2, subset.size());
    
    for (int i : Numbers.range(0, 2)) {
      String expected = new StringBuilder().append(i + 1).append("1").toString();
      assertEquals(expected, subset.getRow(i).get(0));
    }
  }
  
  @Test
  public void testGetColumnSubsetForName() {
    Dataset subset = dataset.getColumnSubset("col1", new Criteria<Object>() {
      @Override
      public boolean matches(Object v) {
        return v.equals("11") || v.equals("21");
      }
    });
  
    assertEquals("col1", subset.getColumnSet().get(0).getName());
    assertEquals(2, subset.size());
    
    for (int i : Numbers.range(0, 2)) {
      String expected = new StringBuilder().append(i + 1).append("1").toString();
      assertEquals(expected, subset.getRow(i).get(0));
    }
  }

  @Test
  public void testGetSubset() {
    Dataset subset = dataset.getSubset(new Criteria<RowResult>() {
      @Override
      public boolean matches(RowResult v) {
        return v.get(1).equals("11") || v.get(1).equals("21");
      }
    });
    
    assertEquals(2, subset.size());
    
    for (int i : Numbers.range(0, 2)) {
      String expected = new StringBuilder().append(i + 1).append("1").toString();
      assertEquals(expected, subset.getRow(i).get(1));
    }
  }

  @Test
  public void testIterator() {
    List<Vector> rows = Data.list(dataset.iterator());
    assertEquals(3, rows.size());
    for (int i : Numbers.range(3)) {
      assertEquals("" + i + "0", rows.get(i).get(0));
    }
  }

  @Test
  public void testIndex() {
  }

}
