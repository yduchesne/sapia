package org.sapia.dataset.transform.formula;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.sapia.dataset.Column;
import org.sapia.dataset.ColumnSet;
import org.sapia.dataset.ColumnSets;
import org.sapia.dataset.Dataset;
import org.sapia.dataset.Datatype;
import org.sapia.dataset.IndexedDataset;
import org.sapia.dataset.RowResult;
import org.sapia.dataset.Vector;
import org.sapia.dataset.algo.Criteria;
import org.sapia.dataset.func.ArgFunction;
import org.sapia.dataset.impl.DefaultColumn;
import org.sapia.dataset.impl.DefaultColumnSet;
import org.sapia.dataset.impl.DefaultDataset;
import org.sapia.dataset.impl.DefaultVector;
import org.sapia.dataset.util.Data;
import org.sapia.dataset.util.Numbers;

public class FormulaDatasetTest {
  
  private Dataset dataset;
  private Dataset delegate;
  
  @Before
  public void setUp() {
    ColumnSet columns = ColumnSets.columnSet(Datatype.NUMERIC, "col0");
    List<Object> rows = new ArrayList<>();
    rows.addAll(Data.listOfInts(Numbers.range(10)));
    delegate = new DefaultDataset(columns, Data.transform(rows, new ArgFunction<Object, Vector>() {
      @Override
      public Vector call(Object arg) {
        return new DefaultVector(arg);
      }
    }));
    
    List<FormulaInfo> infos = new ArrayList<>();
    infos.add(new FormulaInfo(1, new Formula<RowResult>() {
      public Object call(RowResult arg) {
        return ((Integer) arg.get(0)) * 2;
      }
    }));
    
    List<Column> formulaColumns = new ArrayList<>();
    formulaColumns.addAll(columns.getColumns());
    formulaColumns.add(new DefaultColumn(1, Datatype.NUMERIC, "col1"));
    
    dataset = new FormulaDataset(
        delegate, 
        new DefaultColumnSet(formulaColumns), 
        infos
    );
  }

  @Test
  public void testGetColumnString() {
    Vector col = dataset.getColumn("col1");
    for (int i : Numbers.range(dataset.size())) {
      assertEquals(i * 2, col.get(i));
    }
  }

  @Test
  public void testGetColumnInt() {
    Vector col = dataset.getColumn(1);
    for (int i : Numbers.range(dataset.size())) {
      assertEquals(i * 2, col.get(i));
    }
  }

  @Test
  public void testGetColumnSet() {
    assertEquals("col0", dataset.getColumnSet().get(0).getName());
    assertEquals("col1", dataset.getColumnSet().get(1).getName());
  }

  @Test
  public void testGetColumnSubsetIntCriteriaOfObject() {
    Dataset subset = dataset.getColumnSubset(1, new Criteria<Object>() {
      @Override
      public boolean matches(Object v) {
        return ((Integer) v).intValue() >= 5;
      }
    });
    
    for (int i : Numbers.range(5)) {
      assertEquals(new Integer((i + 3) * 2), subset.getRow(i).get(0));
    }
  }

  @Test
  public void testGetColumnSubsetStringCriteriaOfObject() {
    Dataset subset = dataset.getColumnSubset("col1", new Criteria<Object>() {
      @Override
      public boolean matches(Object v) {
        return ((Integer) v).intValue() >= 5;
      }
    });
    
    for (int i : Numbers.range(5)) {
      assertEquals(new Integer((i + 3) * 2), subset.getRow(i).get(0));
    }
  }

  @Test
  public void testGetRow() {
    for (int i = 0; i < dataset.size(); i++) {
      Vector row = dataset.getRow(i);
      assertEquals(i * 2, row.get(1));
    }
  }

  @Test
  public void testGetSubset() {
    Dataset subset = dataset.getSubset(new Criteria<RowResult>() {
      @Override
      public boolean matches(RowResult v) {
        return ((Integer) v.get("col1")).intValue() >= 5;
      }
    });
    
    for (int i : Numbers.range(5)) {
      assertEquals(new Integer((i + 3) * 2), subset.getRow(i).get(1));
    }
  }

  @Test
  public void testHead() {    
  }

  @Test
  public void testTail() {
  }

  @Test
  public void testIndexStringArray() {
    IndexedDataset ds = dataset.index("col1");
    ds.getIndexedColumnSet().get("col1");
  }

  @Test
  public void testIndexListOfString() {
    IndexedDataset ds = dataset.index(Data.list("col1"));
    ds.getIndexedColumnSet().get("col1");
  }

  @Test
  public void testIterator() {
    int i = 0;
    for (Vector r : dataset) {
      i++;
    }
    assertEquals(i, dataset.size());
  }

  @Test
  public void testSize() {
    assertEquals(delegate.size(), dataset.size());
  }

}
