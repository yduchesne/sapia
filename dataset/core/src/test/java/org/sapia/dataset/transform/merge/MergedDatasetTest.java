package org.sapia.dataset.transform.merge;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.sapia.dataset.Column;
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
import org.sapia.dataset.util.Numbers;

public class MergedDatasetTest {
  
  private Dataset d1, d2;
  private Dataset merged;
  
  @Before
  public void testSetup() {
    d1 = new DefaultDataset(
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
    
    d2 = new DefaultDataset(
        Data.list(
            (Column) new DefaultColumn(0, Datatype.STRING, "col0"),
            (Column) new DefaultColumn(1, Datatype.STRING, "col1"),
            (Column) new DefaultColumn(2, Datatype.STRING, "col2")
        ),
        Data.list(
            (Vector) new DefaultVector("30", "31", "32"),
            (Vector) new DefaultVector("40", "41", "42"),
            (Vector) new DefaultVector("50", "51", "52"),
            (Vector) new DefaultVector("60", "61", "62")
        )
    );
    
    merged = new MergedDataset(d1.getColumnSet(), Data.list(d1, d2));
  }

  @Test
  public void testGetColumnSet() {
    assertEquals(d1.getColumnSet(), merged.getColumnSet());
  }

  @Test
  public void testGetColumnInt() {
    for (int i = 0; i < merged.getColumnSet().size(); i++) {
      Vector vec = merged.getColumn(i);
      for (int j : Numbers.range(0, d1.size())) {
        assertEquals(new StringBuilder().append(j).append(i).toString(), vec.get(j));
      }
    }
  }

  @Test
  public void testGetColumnString() {
    for (int i = 0; i < merged.getColumnSet().size(); i++) {
      Vector vec = merged.getColumn(merged.getColumnSet().get(i).getName());
      for (int j : Numbers.range(0, d1.size())) {
        assertEquals(new StringBuilder().append(j).append(i).toString(), vec.get(j));
      }
    }
  }

  @Test
  public void testGetColumnSubsetIntCriteriaOfObject() {
    merged.getColumnSubset(1, new Criteria<Object>() {
      @Override
      public boolean matches(Object v) {
        return v.equals("41") || v.equals("51") || v.equals("61");
      }
    });
  }

  @Test
  public void testGetColumnSubsetStringCriteriaOfObject() {
    merged.getColumnSubset("col1", new Criteria<Object>() {
      @Override
      public boolean matches(Object v) {
        return v.equals("41") || v.equals("51") || v.equals("61");
      }
    });
  }

  @Test
  public void testGetRow() {
    for (int i = 0; i < d1.size(); i++) {
      Vector row = merged.getRow(i);
      for (int j = 0; j < row.size(); j++) {
        assertEquals(new StringBuilder().append(i).append(j).toString(), row.get(j));
      }
    }
  }

  @Test
  public void testGetSubset() {
    Dataset subset = merged.getSubset(new Criteria<RowResult>() {
      @Override
      public boolean matches(RowResult r) {
        String v = (String) r.get("col0");
        return v.equals("40") || v.equals("50") || v.equals("60");
      }
    });
    for (int i : Numbers.range(3)) {
      Vector row = subset.getRow(i);
      for (int j = 0; j < row.size(); j++) {
        assertEquals(new StringBuilder().append(i + 4).append(j).toString(), row.get(j));
      }
    }
    
  }

  @Test
  public void testIndexStringArray() {
    IndexedDataset indexed = merged.index("col0");
    indexed.getIndexedColumnSet().get("col0");
  }

  @Test
  public void testIndexListOfString() {
    IndexedDataset indexed = merged.index(Data.list("col0"));
    indexed.getIndexedColumnSet().get("col0");
  }

  @Test
  public void testIterator() {
    int i =  0;
    for (Vector row : merged) {
      for (int j = 0; j < row.size(); j++) {
        assertEquals(new StringBuilder().append(i).append(j).toString(), row.get(j));
      }
      i++;
    }
    assertEquals(i, d1.size() + d2.size());
  }

  @Test
  public void testHead() {
    int i =  0;
    for (Vector row : merged.head()) {
      for (int j = 0; j < row.size(); j++) {
        assertEquals(new StringBuilder().append(i).append(j).toString(), row.get(j));
      }
      i++;
    }
    assertEquals(i, d1.size() + d2.size());
  }

  @Test
  public void testTail() {
    int i =  0;
    for (Vector row : merged.head()) {
      for (int j = 0; j < row.size(); j++) {
        assertEquals(new StringBuilder().append(i).append(j).toString(), row.get(j));
      }
      i++;
    }
    assertEquals(i, d1.size() + d2.size());
  }

  @Test
  public void testSize() {
    assertEquals(merged.size(), d1.size() + d2.size());
  }

}
