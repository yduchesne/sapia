package org.sapia.dataset.computation;

import org.sapia.dataset.ColumnSet;
import org.sapia.dataset.RowSet;

/**
 * A {@link ComputationTask} implementations whose instance perform their
 * computations sequentially, in the order in which they where added.
 * 
 * @author yduchesne
 *
 */
public class SequentialComputationTask implements ComputationTask {

  private CompositeComputation computation = new CompositeComputation();
  
  @Override
  public void add(Computation computation) {
    this.computation.add(computation);
  }
  
  @Override
  public ComputationResults compute(ColumnSet columns, RowSet rows)
      throws InterruptedException {
    ComputationResults results = ComputationResults.newInstance(columns);
    computation.compute(results, rows);
    return results;
  }
}