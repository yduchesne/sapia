package org.sapia.dataset.math;

import org.sapia.dataset.Column;
import org.sapia.dataset.RowSet;
import org.sapia.dataset.Vector;
import org.sapia.dataset.computation.Computation;
import org.sapia.dataset.computation.ComputationResult;
import org.sapia.dataset.computation.ComputationResults;
import org.sapia.dataset.func.NoArgFunction;
import org.sapia.dataset.value.Values;

/**
 * Computes sums over dataset rows.
 * 
 * @author yduchesne
 *
 */
public class SumComputation implements Computation {
  
  @Override
  public void compute(ComputationResults context, RowSet rowSet) {
    ComputationResult sum = context.get("sum");
    
    NoArgFunction<SumValue> func = new NoArgFunction<SumValue>() {
      @Override
      public SumValue call() {
        return new SumValue();
      }
    };
    
    for (Vector row : rowSet) {
      for (Column col : context.getColumnSet()) {
        SumValue sumValue = sum.get(col, func);
        sumValue.add(Values.doubleValue(row.get(col.getIndex())));
      }
    }
  }

}
