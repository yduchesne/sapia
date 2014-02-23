package org.sapia.dataset.stat;

import org.sapia.dataset.Column;
import org.sapia.dataset.Datatype;
import org.sapia.dataset.RowSet;
import org.sapia.dataset.Vector;
import org.sapia.dataset.computation.Computation;
import org.sapia.dataset.computation.ComputationResult;
import org.sapia.dataset.computation.ComputationResults;
import org.sapia.dataset.func.NoArgFunction;
import org.sapia.dataset.value.MutableValue;
import org.sapia.dataset.value.NullValue;
import org.sapia.dataset.value.Value;
import org.sapia.dataset.value.Values;

/**
 * Computes variance for dataset columns.
 * 
 * @author yduchesne
 *
 */
public class VarianceComputation implements Computation {
  
  @Override
  public void compute(ComputationResults context, RowSet rows) {
    
    ComputationResult meanResults = context.get(Stats.MEAN);
    ComputationResult varResults  = context.get(Stats.VARIANCE);

    for (Vector row : rows) {
      for (Column col : varResults.getColumnSet()) {
        
        MutableValue variance = (MutableValue) varResults.get(col, new NoArgFunction<Value>() {
          @Override
          public Value call() {
            return new MutableValue();
          }
        });

        if (col.getType() == Datatype.NUMERIC) {
          Object value = row.get(col.getIndex());
          if (NullValue.isNotNull(value)) {
            double meanValue = Values.doubleValue(meanResults.get(col));
            double colValue  = Values.doubleValue(row.get(col.getIndex()));
            variance.increase(Math.pow(colValue - meanValue, 2));
          }
        }
      }
    }
    
  }

}
