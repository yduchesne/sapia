package org.sapia.dataset.stat;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.sapia.dataset.ColumnSets;
import org.sapia.dataset.Datatype;
import org.sapia.dataset.RowSet;
import org.sapia.dataset.RowSets;
import org.sapia.dataset.computation.ComputationResult;
import org.sapia.dataset.computation.ComputationResults;
import org.sapia.dataset.util.Data;
import org.sapia.dataset.value.DoubleValue;

public class MeanVarianceComputationTest {
  
  private ComputationResults context;
  private RowSet rows;
  private SpreadStatsComputation comp;
  
  @Before
  public void setUp() {
    context = ComputationResults.newInstance(
        ColumnSets.columnSet("col0", Datatype.NUMERIC, "col1", Datatype.STRING)
    );
   
    rows = RowSets.rowSet(new Object[][] {
       Data.array(5, "row1"), 
       Data.array(10, "row2"), 
       Data.array(15, "row3") 
    });
    
    comp = new SpreadStatsComputation();
  }

  @Test
  public void testComputeMean() {
    comp.compute(context, rows);
    ComputationResult mean     = context.get("mean");
    assertEquals(new DoubleValue(10), mean.get(context.getColumnSet().get("col0")));
    assertEquals(new DoubleValue(0), mean.get(context.getColumnSet().get("col1")));
  }
  
  @Test
  public void testComputeVariance() {
    comp.compute(context, rows);
    ComputationResult variance = context.get("variance");
    double mean = 10;
    double expected = Math.pow(5 - mean, 2) + Math.pow(10 - mean, 2) + Math.pow(15 - mean, 2) ;
    
    assertEquals(new DoubleValue(expected), variance.get(context.getColumnSet().get("col0")));
    assertEquals(new DoubleValue(0), variance.get(context.getColumnSet().get("col1")));
  }
  
  @Test
  public void testComputeStandardDeviation() {
    comp.compute(context, rows);
    ComputationResult stddev = context.get("stddev");
    double mean = 10;
    double expected = Math.sqrt(Math.pow(5 - mean, 2) + Math.pow(10 - mean, 2) + Math.pow(15 - mean, 2));
    assertEquals(new DoubleValue(expected), stddev.get(context.getColumnSet().get("col0")));
    assertEquals(new DoubleValue(0), stddev.get(context.getColumnSet().get("col1")));
  }

}
