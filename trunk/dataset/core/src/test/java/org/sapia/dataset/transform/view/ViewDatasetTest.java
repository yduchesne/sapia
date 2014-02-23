package org.sapia.dataset.transform.view;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;
import org.sapia.dataset.Column;
import org.sapia.dataset.ColumnSet;
import org.sapia.dataset.Dataset;
import org.sapia.dataset.Datatype;
import org.sapia.dataset.IndexedDataset;
import org.sapia.dataset.RowResult;
import org.sapia.dataset.Vector;
import org.sapia.dataset.algo.Criteria;
import org.sapia.dataset.impl.DefaultColumn;
import org.sapia.dataset.impl.DefaultDataset;
import org.sapia.dataset.impl.DefaultVector;
import org.sapia.dataset.util.Data;

public class ViewDatasetTest {
  
  private Dataset view;
  
  @Before
  public void setUp() {
    List<Column> columns = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      columns.add(new DefaultColumn(i, Datatype.STRING, "col" + i));
    }

    List<Vector> rows = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      rows.add(new DefaultVector("0", "1", "2", "3", "4"));
    }
    
    Dataset delegate = new DefaultDataset(columns, rows);
    
    view = Views.include(delegate, "col0", "col2", "col4");
  }

  @Test
  public void testGetColumnSet() {
    ColumnSet columns = view.getColumnSet();
    assertEquals("col0", columns.get(0).getName());
    assertEquals("col2", columns.get(1).getName());
    assertEquals("col4", columns.get(2).getName());
  }

  @Test
  public void testGetColumnByName() {
    ColumnSet columns = view.getColumnSet();
    assertEquals(0, columns.get("col0").getIndex());
    assertEquals(1, columns.get("col2").getIndex());
    assertEquals(2, columns.get("col4").getIndex());
  }

  @Test
  public void testGetColumnSubsetIntCriteriaOfObject() {
    Dataset subset = view.getColumnSubset(2, new Criteria<Object>() {
      @Override
      public boolean matches(Object v) {
        return true;
      }
    });
    assertEquals(5, subset.size());
    assertEquals("col4", subset.getColumnSet().get(0).getName());
  }

  @Test
  public void testGetColumnSubsetStringCriteriaOfObject() {
    Dataset subset = view.getColumnSubset("col4", new Criteria<Object>() {
      @Override
      public boolean matches(Object v) {
        return true;
      }
    });
    assertEquals(5, subset.size());
    assertEquals("col4", subset.getColumnSet().get("col4").getName());
  }

  @Test
  public void testGetRow() {
    for (int i = 0; i < view.size(); i++) {
      Vector row = view.getRow(i);
      assertEquals("0", (String) row.get(0));
      assertEquals("2", (String) row.get(1));     
      assertEquals("4", (String) row.get(2));
    }
  }

  @Test
  public void testGetSubset() {
    final AtomicInteger count = new AtomicInteger();
    Dataset subset = view.getSubset(new Criteria<RowResult>() {
      @Override
      public boolean matches(RowResult v) {
        count.incrementAndGet();
        return count.get() < 2;
      }
    });
    assertEquals(1, subset.size());
  }

  @Test
  public void testHead() {
    
  }

  @Test
  public void testTail() {
  }

  @Test
  public void testIndexStringArray() {
    IndexedDataset indexed = view.index("col0");
    indexed.getIndexedColumnSet().get("col0");
  }

  @Test
  public void testIndexListOfString() {
    IndexedDataset indexed = view.index(Data.list("col2"));
    indexed.getIndexedColumnSet().get("col2");
  }

  @Test
  public void testSize() {
    assertEquals(5, view.size());
  }

  @Test
  public void testIterator() {
    final List<Vector> rows = new ArrayList<>();
    for (Vector r : view) {
      rows.add(r);
    }
    assertEquals(view.size(), rows.size());
  }

}
