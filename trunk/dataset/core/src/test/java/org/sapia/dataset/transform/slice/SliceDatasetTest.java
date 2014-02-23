package org.sapia.dataset.transform.slice;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.sapia.dataset.ColumnSet;
import org.sapia.dataset.ColumnSets;
import org.sapia.dataset.Dataset;
import org.sapia.dataset.Datatype;
import org.sapia.dataset.IndexedDataset;
import org.sapia.dataset.RowResult;
import org.sapia.dataset.Vector;
import org.sapia.dataset.Vectors;
import org.sapia.dataset.algo.Criteria;
import org.sapia.dataset.conf.Conf;
import org.sapia.dataset.impl.DefaultDataset;
import org.sapia.dataset.util.Data;
import org.sapia.dataset.util.Numbers;

public class SliceDatasetTest {
  
  private static int START = 5;
  private static int END   = 35;
  
  private SliceDataset slice;
  
  @Before 
  public void setUp() {
    ColumnSet columns = ColumnSets.columnSet("col0", Datatype.NUMERIC, "col1", Datatype.STRING, "col2", Datatype.STRING);
    List<Vector> rows = new ArrayList<>(50);
    for (int i : Numbers.range(50)) {
      rows.add(Vectors.vector(new Integer(i), "s1", "s2"));
    }
    slice = new SliceDataset(new DefaultDataset(columns, rows), START, END);
  }

  @Test
  public void testGetColumnSet() {
    slice.getColumnSet().contains("col0", "col1", "col2");
  }

  @Test
  public void testGetColumnInt() {
    Vector vector = slice.getColumn(0);
    for (int i : Numbers.range(0, 5)) {
      assertEquals(new Integer(i + 5), (Integer) vector.get(i));
    }
  }

  @Test
  public void testGetColumnString() {
    Vector vector = slice.getColumn("col0");
    for (int i : Numbers.range(0, 5)) {
      assertEquals(new Integer(i + 5), (Integer) vector.get(i));
    }
  }

  @Test
  public void testGetColumnSubsetIntCriteriaOfObject() {
    Dataset subset = slice.getColumnSubset(0, new Criteria<Object>() {
      @Override
      public boolean matches(Object v) {
        Integer value = (Integer) v;
        return value.intValue() >= 8;
      }
    });
    for (int i : Numbers.range(0, 2)) {
      assertEquals(new Integer(i + 8), (Integer) subset.getRow(i).get(0));
    }
  }

  @Test
  public void testGetColumnSubsetStringCriteriaOfObject() {
    Dataset subset = slice.getColumnSubset("col0", new Criteria<Object>() {
      @Override
      public boolean matches(Object v) {
        Integer value = (Integer) v;
        return value.intValue() >= 8;
      }
    });
    for (int i : Numbers.range(0, 2)) {
      assertEquals(new Integer(i + 8), (Integer) subset.getRow(i).get(0));
    }
  }

  @Test
  public void testGetSubset() {
    Dataset subset = slice.getSubset(new Criteria<RowResult>() {
      public boolean matches(RowResult v) {
        Integer value = (Integer) v.get(0);
        return value.intValue() >= 8;
      }
    });
    for (int i : Numbers.range(0, 2)) {
      assertEquals(new Integer(i + 8), (Integer) subset.getRow(i).get(0));
    }
  }

  @Test
  public void testGetRow() {
    for (int i = 0; i < slice.size(); i++) {
      assertEquals(new Integer(START + i), slice.getRow(i).get(0));
    }
  }

  @Test
  public void testHead() {
    Dataset head = slice.head();
    assertEquals(Conf.getHeadLength(), head.size());
  }

  @Test
  public void testTail() {
    Dataset tail = slice.tail();
    assertEquals(Conf.getTailLength(), tail.size());    
  }

  @Test
  public void testIndexStringArray() {
    IndexedDataset indexed = slice.index("col0");
    assertEquals(slice.size(), indexed.size());
  }

  @Test
  public void testIndexListOfString() {
    IndexedDataset indexed = slice.index(Data.list("col0"));
    assertEquals(slice.size(), indexed.size());
  }

  @Test
  public void testSize() {
    assertEquals(END - START, slice.size());
  }

}
