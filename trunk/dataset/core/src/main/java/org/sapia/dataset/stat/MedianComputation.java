package org.sapia.dataset.stat;

import org.sapia.dataset.Column;
import org.sapia.dataset.RowSet;
import org.sapia.dataset.Vector;
import org.sapia.dataset.computation.Computation;
import org.sapia.dataset.computation.ComputationResult;
import org.sapia.dataset.computation.ComputationResults;
import org.sapia.dataset.value.DoubleValue;
import org.sapia.dataset.value.Value;
import org.sapia.dataset.value.Values;

/**
 * Computes the median for dataset columns.
 * 
 * @author yduchesne
 *
 */
public class MedianComputation implements Computation {
  
  @Override
  public void compute(ComputationResults context, RowSet rows) {
    if (rows.size() > 0) {
      ComputationResult median = context.get("median");
      if (rows.size() % 2 == 0) {
        for (Column col : context.getColumnSet()) {
          double lower  = Values.doubleValue(rows.get(rows.size() / 2 - 1).get(col.getIndex()));
          double higher = Values.doubleValue(rows.get(rows.size() / 2).get(col.getIndex()));
          median.set(col, new MeanValue().increase(lower).increase(higher));
        }
      } else {
        for (Column col : context.getColumnSet()) {
          Vector medianRow   = rows.get(rows.size() / 2);
          Value  medianValue = new DoubleValue(Values.doubleValue(medianRow.get(col.getIndex())));
          median.set(col, medianValue);
        }
      }
    }
  }

}
