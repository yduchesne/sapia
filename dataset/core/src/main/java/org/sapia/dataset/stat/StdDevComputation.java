package org.sapia.dataset.stat;

import org.sapia.dataset.Column;
import org.sapia.dataset.RowSet;
import org.sapia.dataset.computation.Computation;
import org.sapia.dataset.computation.ComputationResult;
import org.sapia.dataset.computation.ComputationResults;
import org.sapia.dataset.value.DoubleValue;

/**
 * Computes the mean, variance, and standard deviation.
 * 
 * @author yduchesne
 *
 */
public class StdDevComputation implements Computation {
  
  @Override
  public void compute(ComputationResults context, RowSet rows) {
    
    ComputationResult varResults  = context.get(Stats.VARIANCE);
    ComputationResult stdResults  = context.get(Stats.STDDEV);
    
    // ------------------------------------------------------------------------
    // computing stddev
    
    for (Column col : varResults.getColumnSet()) {
      stdResults.set(col, new DoubleValue(Math.sqrt(varResults.get(col).get())));
    }
    
  }

}
