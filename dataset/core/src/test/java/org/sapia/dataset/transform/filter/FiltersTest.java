package org.sapia.dataset.transform.filter;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.sapia.dataset.ColumnSet;
import org.sapia.dataset.ColumnSets;
import org.sapia.dataset.Dataset;
import org.sapia.dataset.Datatype;
import org.sapia.dataset.Vector;
import org.sapia.dataset.Vectors;
import org.sapia.dataset.func.ArgFunction;
import org.sapia.dataset.impl.DefaultDataset;
import org.sapia.dataset.util.Data;
import org.sapia.dataset.util.Numbers;
import org.sapia.dataset.value.Value;

public class FiltersTest {
  
  private Dataset dataset;
  
  @Before
  public void setUp() {
    ColumnSet columns = ColumnSets.columnSet("col0", Datatype.NUMERIC, "col1", Datatype.STRING, "col2", Datatype.STRING);
    List<Vector> rows = new ArrayList<>(50);
    for (int i : Numbers.range(50)) {
      rows.add(Vectors.vector(new Integer(i), "s1", "s2"));
    }
    dataset = new DefaultDataset(columns, rows);
  }

  @Test
  public void testRemoveNullsDatasetForAllColumns() {
    ColumnSet columns = ColumnSets.columnSet("col0", Datatype.NUMERIC, "col1", Datatype.STRING, "col2", Datatype.STRING);
    List<Vector> rows = Data.list(
      Vectors.vector(null, "s1"),
      Vectors.vector("s0", null),
      Vectors.vector("s0", "s1")
    );
        
    dataset = new DefaultDataset(columns, rows);    
    dataset = Filters.removeNulls(dataset, Data.list("col0", "col1"));
    assertEquals(1, dataset.size());
    assertEquals("s0", dataset.getRow(0).get(0));
    assertEquals("s1", dataset.getRow(0).get(1));
    
    dataset = new DefaultDataset(columns, rows);    
    dataset = Filters.removeNulls(dataset, "col0", "col1");
    assertEquals(1, dataset.size());
    assertEquals("s0", dataset.getRow(0).get(0));
    assertEquals("s1", dataset.getRow(0).get(1));
  }
  
  @Test
  public void testRemoveNullsDatasetForSomeColumns() {
    ColumnSet columns = ColumnSets.columnSet("col0", Datatype.NUMERIC, "col1", Datatype.STRING, "col2", Datatype.STRING);
    List<Vector> rows = Data.list(
      Vectors.vector(null, "s1"),
      Vectors.vector("s0", null),
      Vectors.vector("s0", "s1")
    );
        
    dataset = new DefaultDataset(columns, rows);    
    dataset = Filters.removeNulls(dataset, "col0");
    assertEquals(2, dataset.size());

    assertNull(dataset.getRow(0).get(1));
    assertEquals("s0", dataset.getRow(0).get(0));
    assertEquals("s0", dataset.getRow(1).get(0));
    assertEquals("s1", dataset.getRow(1).get(1));
  }


  @Test
  public void testRemoveAnyNulls() {
    ColumnSet columns = ColumnSets.columnSet("col0", Datatype.NUMERIC, "col1", Datatype.STRING, "col2", Datatype.STRING);
    List<Vector> rows = Data.list(
      Vectors.vector(null, "s1"),
      Vectors.vector("s0", null),
      Vectors.vector("s0", "s1")
    );
        
    dataset = new DefaultDataset(columns, rows);    
    dataset = Filters.removeNulls(dataset, "col0");

    dataset = new DefaultDataset(columns, rows);    
    dataset = Filters.removeNulls(dataset, "col0", "col1");
    assertEquals(1, dataset.size());
    assertEquals("s0", dataset.getRow(0).get(0));
    assertEquals("s1", dataset.getRow(0).get(1));
  }
  
  @Test
  public void testReplace() {
    ColumnSet columns = ColumnSets.columnSet("col0", Datatype.NUMERIC);
    List<Vector> rows = Data.list(
      Vectors.vector(0),
      Vectors.vector(1),
      Vectors.vector(2)
    );    
    
    dataset = new DefaultDataset(columns, rows);
    
    dataset = Filters.replace(dataset, "col0", Datatype.STRING, new ArgFunction<Object, Object>() {
      @Override
      public Object call(Object arg) {
        return ((Number) arg).toString();
      }
    });
    
    for (int i : Numbers.range(3)) {
      assertEquals(""+i, dataset.getRow(i).get(0));
    }
  }

  @Test
  public void testRemoveHead() {
    Dataset subset = Filters.removeHead(dataset, 10);
    for (int i : Numbers.range(10, 50)) {
      assertEquals(new Integer(i), subset.getRow(i - 10).get(0));
    }
  }

  @Test
  public void testRemoveTail() {
    Dataset subset = Filters.removeTail(dataset, 10);
    for (int i : Numbers.range(0, 40)) {
      assertEquals(new Integer(i), subset.getRow(i).get(0));
    }
  }

  @Test
  public void testRemoveTop() {
    Dataset subset = Filters.removeTop(dataset, 0.1);
    for (int i : Numbers.range(5, 50)) {
      assertEquals(new Integer(i), subset.getRow(i - 5).get(0));
    }
  }

  @Test
  public void testRemoveBottom() {
    Dataset subset = Filters.removeBottom(dataset, 0.1);
    for (int i : Numbers.range(0, 45)) {
      assertEquals(new Integer(i), subset.getRow(i).get(0));
    }
  }

  @Test
  public void testSelect() {
    Dataset subset = Filters.select(dataset, "col0 >= 10 && col1 == 's1'");
    assertEquals(new Integer(10), subset.getRow(0).get(0));
  }

}
