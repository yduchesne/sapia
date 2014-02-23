package org.sapia.dataset.stat;

import org.sapia.dataset.Column;
import org.sapia.dataset.Datatype;
import org.sapia.dataset.RowSet;
import org.sapia.dataset.Vector;
import org.sapia.dataset.computation.Computation;
import org.sapia.dataset.computation.ComputationResult;
import org.sapia.dataset.computation.ComputationResults;
import org.sapia.dataset.func.NoArgFunction;
import org.sapia.dataset.value.NullValue;
import org.sapia.dataset.value.Values;

/**
 * Computes the mean for dataset columns.
 * 
 * @author yduchesne
 *
 */
public class MeanComputation implements Computation {
  
  @Override
  public void compute(ComputationResults context, RowSet rows) {
    
    ComputationResult meanResults = context.get(Stats.MEAN);
    
    // computing mean for each given column
    for (Vector row : rows) {
      for (Column col : meanResults.getColumnSet()) {
        MeanValue mean = meanResults.get(col, new NoArgFunction<MeanValue>() {
          @Override
          public MeanValue call() {
            return new MeanValue();
          }
        });
        if (col.getType() == Datatype.NUMERIC) {
          Object value = row.get(col.getIndex());
          if (NullValue.isNotNull(value)) {
            double colValue = Values.doubleValue(row.get(col.getIndex()));
            mean.increase(colValue);
          }
        }
      }
    }
    
  }

}
