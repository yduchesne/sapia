package org.sapia.dataset.impl;

import static org.junit.Assert.*;

import java.util.Set;




import org.junit.Before;
import org.junit.Test;
import org.sapia.dataset.Column;
import org.sapia.dataset.ColumnSet;
import org.sapia.dataset.Dataset;
import org.sapia.dataset.Datatype;
import org.sapia.dataset.Index;
import org.sapia.dataset.RowResult;
import org.sapia.dataset.RowSet;
import org.sapia.dataset.Vector;
import org.sapia.dataset.algo.Criteria;
import org.sapia.dataset.util.Data;
import org.sapia.dataset.util.Numbers;

public class IndexDatasetAdapterTest {
  
  private RowSet              rowset;
  private IndexDatasetAdapter adapter;
  
  @Before 
  public void setUp() {
    rowset = new DefaultRowSet(
        Data.list(
            (Vector) new DefaultVector("00", "00", "00", "00"),
            (Vector) new DefaultVector("00", "00", "00", "01"),
            (Vector) new DefaultVector("00", "00", "00", "02"),
            
            (Vector) new DefaultVector("00", "00", "01", "00"),
            (Vector) new DefaultVector("00", "00", "01", "01"),
            (Vector) new DefaultVector("00", "00", "01", "02"),
            
            (Vector) new DefaultVector("00", "01", "01", "00"),
            (Vector) new DefaultVector("00", "01", "01", "01"),
            (Vector) new DefaultVector("00", "01", "01", "02"),
            
            (Vector) new DefaultVector("01", "01", "01", "00"),
            (Vector) new DefaultVector("01", "01", "01", "01"),
            (Vector) new DefaultVector("01", "01", "01", "02")
        )
      );
    
    ColumnSet columns = new DefaultColumnSet(
        Data.list(
            (Column) new DefaultColumn(0, Datatype.STRING, "col0"),
            (Column) new DefaultColumn(1, Datatype.STRING, "col1"),
            (Column) new DefaultColumn(2, Datatype.STRING, "col2"),
            (Column) new DefaultColumn(2, Datatype.STRING, "col3")
        )
    );
    
    Index index = new DefaultIndex(
        rowset, 
        columns, 
        columns.includes("col1", "col2")
    ); 
    
    adapter = new IndexDatasetAdapter(index);
  }

  @Test
  public void testGetColumnByIndex() {
    Vector vec = adapter.getColumn(0);
    for (int i : Numbers.range(vec.size())) {
      assertEquals(rowset.get(i).get(0), vec.get(i));
    }
  }

  @Test
  public void testGetColumnByName() {
    Vector vec = adapter.getColumn("col0");
    for (int i : Numbers.range(vec.size())) {
      assertEquals(rowset.get(i).get(0), vec.get(i));
    }
  }

  @Test
  public void testGetColumnSet() {
    ColumnSet cols = adapter.getColumnSet();
    assertEquals(0, cols.get("col0").getIndex());
    assertEquals(1, cols.get("col1").getIndex());
    assertEquals(2, cols.get("col2").getIndex());
  }

  @Test
  public void testGetColumnSubsetForIndex() {
    Dataset subset = adapter.getColumnSubset(0, new Criteria<Object>() {
      @Override
      public boolean matches(Object v) {
        return v.equals("01");
      }
    });
    
    

    Set<Vector> expected = Data.set(
        (Vector) new DefaultVector("01")
    );

    Set<Vector> actual = Data.set(subset.iterator());
    assertEquals(3, subset.size());
    assertTrue(actual.containsAll(expected));
  }

  @Test
  public void testGetColumnSubsetForName() {
    Dataset subset = adapter.getColumnSubset("col0", new Criteria<Object>() {
      @Override
      public boolean matches(Object v) {
        return v.equals("01");
      }
    });

    Set<Vector> expected = Data.set(
        (Vector) new DefaultVector("01")
    );

    Set<Vector> actual = Data.set(subset.iterator());
    assertEquals(3, subset.size());
    assertTrue(actual.containsAll(expected));

  }

  @Test
  public void testGetRow() {
    for (int i : Numbers.range(rowset.size())) {
      assertEquals(rowset.get(i), adapter.getRow(i));
    }
  }

  @Test
  public void testGetSubset() {
    Dataset subset = adapter.getSubset(new Criteria<RowResult>() {
      @Override
      public boolean matches(RowResult v) {
        return v.get(0).equals("01");
      }
    });
    
    Set<Vector> expected = Data.set(
        (Vector) new DefaultVector("01", "01", "01", "00"),
        (Vector) new DefaultVector("01", "01", "01", "01"),
        (Vector) new DefaultVector("01", "01", "01", "02")
    );

    assertEquals(3, subset.size());
    assertTrue(Data.set(subset.iterator()).containsAll(expected));
  }

  @Test
  public void testIndex() {
    Dataset subset = adapter.index("col0");
    
    for (int i : Numbers.range(rowset.size())) {
      assertEquals(rowset.get(i), subset.getRow(i));
    }
    
    ColumnSet columnSet = adapter.getColumnSet();
    for (int i : adapter.getColumnSet().getColumnIndices()) {
      assertEquals(adapter.getColumnSet().get(i), columnSet.get(i));
    }
   }

  @Test
  public void testIterator() {
    Set<Vector> expected = Data.set(rowset.iterator());
    Set<Vector> actual   = Data.set(adapter.iterator());
    assertTrue(expected.containsAll(actual));
  }

  @Test
  public void testSize() {
    assertEquals(rowset.size(), adapter.size());
  }

}
