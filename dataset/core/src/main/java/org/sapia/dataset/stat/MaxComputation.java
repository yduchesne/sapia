package org.sapia.dataset.stat;

import org.sapia.dataset.Column;
import org.sapia.dataset.Datatype;
import org.sapia.dataset.RowSet;
import org.sapia.dataset.Vector;
import org.sapia.dataset.computation.Computation;
import org.sapia.dataset.computation.ComputationResult;
import org.sapia.dataset.computation.ComputationResults;
import org.sapia.dataset.func.NoArgFunction;
import org.sapia.dataset.value.Values;

/**
 * A {@link Computation} that computes the maximum for dataset columns.
 * 
 * @author yduchesne
 *
 */
public class MaxComputation implements Computation {
  
  @Override
  public void compute(ComputationResults context, RowSet rows) {
    
    NoArgFunction<MaxValue> maxFunc = new NoArgFunction<MaxValue>() {
      @Override
      public MaxValue call() {
        return new MaxValue();
      }
    };
    
    ComputationResult maxResult = context.get(Stats.MAX);
    
    for (Vector row : rows) {
      for (Column col : context.getColumnSet()) {
        if (col.getType() == Datatype.NUMERIC) {
          MaxValue maxVal = maxResult.get(col, maxFunc);
          maxVal.set(Values.doubleValue(row.get(col.getIndex())));
        }
      }
    }
  }
}
