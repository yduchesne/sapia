package org.sapia.dataset.transform.slice;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.sapia.dataset.ColumnSet;
import org.sapia.dataset.ColumnSets;
import org.sapia.dataset.Dataset;
import org.sapia.dataset.Datasets;
import org.sapia.dataset.Datatype;
import org.sapia.dataset.Vector;
import org.sapia.dataset.Vectors;
import org.sapia.dataset.conf.Conf;
import org.sapia.dataset.impl.DefaultDataset;
import org.sapia.dataset.util.Numbers;

public class SlicesTest {
  
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
  public void testTop() {
    Dataset top = Slices.top(dataset, 0.1);
    assertEquals(5, top.size());
    for (int i : Numbers.range(0, 5)) {
      assertEquals(new Integer(i), top.getRow(i).get(0));
    }
  }

  @Test
  public void testBottom() {
    Dataset bottom = Slices.bottom(dataset, 0.1);
    assertEquals(5, bottom.size());
    for (int i : Numbers.range(45, 49)) {
      assertEquals(new Integer(i), bottom.getRow(i - 45).get(0));
    }
  }

  @Test
  public void testHead() {
    Dataset head = Slices.head(dataset);
    for (int i : Numbers.range(0, Conf.getHeadLength())) {
      assertEquals(new Integer(i), head.getRow(i).get(0));
    }
  }

  @Test
  public void testTail() {
    Dataset tail = Slices.tail(dataset);
    for (int i : Numbers.range(dataset.size() - Conf.getTailLength(), 50)) {
      assertEquals(new Integer(i), tail.getRow(i - 25).get(0));
    }
  }

  @Test
  public void testSlice() {
    Dataset slice = Slices.slice(dataset, 10, 20);
    for (int i : Numbers.range(10, 20)) {
      assertEquals(new Integer(i), slice.getRow(i - 10).get(0));
    }
  }

  @Test
  public void testQuartile() {
    Dataset q1 = Slices.quartile(dataset, 1);
    assertEquals(new Integer(0), q1.getRow(0).get(0));

    Dataset q2 = Slices.quartile(dataset, 2);
    assertEquals(new Integer(12), q2.getRow(0).get(0));
    
    Dataset q3 = Slices.quartile(dataset, 3);
    assertEquals(new Integer(24), q3.getRow(0).get(0));
    
    Dataset q4 = Slices.quartile(dataset, 4);
    assertEquals(new Integer(36), q4.getRow(0).get(0));
    assertEquals(new Integer(49), Datasets.lastRow(q4).get(0));
  }

  @Test
  public void testQuintile() {
    for (int n : Numbers.range(1, 6)) {
      Dataset quintile = Slices.quintile(dataset, n);
      assertEquals(new Integer((n - 1) * 10), quintile.getRow(0).get(0));
    }
  }

}
