package org.sapia.dataset.stat;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.sapia.dataset.ColumnSets;
import org.sapia.dataset.Datatype;
import org.sapia.dataset.RowSet;
import org.sapia.dataset.RowSets;
import org.sapia.dataset.computation.ComputationResults;
import org.sapia.dataset.util.Data;
import org.sapia.dataset.value.DoubleValue;

public class MedianComputationTest {
  
  private ComputationResults context;
  private RowSet oddRows, evenRows;
  private MedianComputation comp;
 
  
  @Before
  public void setUp() {
    context = ComputationResults.newInstance(
        ColumnSets.columnSet("col0", Datatype.NUMERIC, "col1", Datatype.STRING)
    );
   
    oddRows = RowSets.rowSet(new Object[][] {
       Data.array(5, "row1"), 
       Data.array(10, "row2"), 
       Data.array(15, "row3") 
    });
    
    evenRows = RowSets.rowSet(new Object[][] {
        Data.array(5, "row1"), 
        Data.array(10, "row2"), 
     });
    
    comp = new MedianComputation();
  }


  @Test
  public void testComputeWithOddRows() {
    comp.compute(context, oddRows);
    assertEquals(new DoubleValue(10), context.get("median").get(context.getColumnSet().get("col0")));
    assertEquals(new DoubleValue(0), context.get("median").get(context.getColumnSet().get("col1")));
  }
  
  @Test
  public void testComputeWithEvenRows() {
    comp.compute(context, evenRows);
    assertEquals(new DoubleValue(7.5), context.get("median").get(context.getColumnSet().get("col0")));
    assertEquals(new DoubleValue(0), context.get("median").get(context.getColumnSet().get("col1")));
  }

}
