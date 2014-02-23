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
 * A {@link Computation} that computes the minimum for dataset columns.
 * 
 * @author yduchesne
 *
 */
public class MinComputation implements Computation {
  
  @Override
  public void compute(ComputationResults context, RowSet rows) {
    
    NoArgFunction<MinValue> minFunc = new NoArgFunction<MinValue>() {
      @Override
      public MinValue call() {
        return new MinValue();
      }
    };
    
    ComputationResult minResult = context.get(Stats.MIN);
    
    for (Vector row : rows) {
      for (Column col : context.getColumnSet()) {
        if (col.getType() == Datatype.NUMERIC) {
          MinValue minVal = minResult.get(col, minFunc);
          minVal.set(Values.doubleValue(row.get(col.getIndex())));
        }
      }
    }
  }
}
