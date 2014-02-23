package org.sapia.dataset.transform.range;

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
import org.sapia.dataset.impl.DefaultDataset;
import org.sapia.dataset.stat.MeanValue;

public class RangesTest {
  
  private Dataset dataset;
  
  @Before
  public void setUp() {
    ColumnSet columns = ColumnSets.columnSet(
        "col0", Datatype.STRING, 
        "col1", Datatype.NUMERIC, 
        "col2", Datatype.STRING,
        "col3", Datatype.NUMERIC
    );
    
    List<Vector> rows = new ArrayList<>();
        
    for (int i = 0; i < 10; i++) {
      int v1 = i + 1;
      int v2 = (i + 1) * 10; 
      rows.add(Vectors.vector("s" + v1, v1, "s" + v2, v2));
    }
    dataset = new DefaultDataset(columns, rows);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testRange() {
    Dataset result = Ranges.range(dataset, 2, "col1", "col3");
    for (int i = 0; i < result.size(); i += 2) {
      double v1 = new MeanValue().increase(i + 1).increase(i + 2).get();
      double v2 = v1 * 10d; 
      Range<Object> c1 = (Range<Object>) result.getRow(i).get(1);
      Range<Object> c3 = (Range<Object>) result.getRow(i + 1).get(3);
      assertEquals(v1, c1.get(), 0);
      assertEquals(v2, c3.get(), 0);
    }
  }

}
