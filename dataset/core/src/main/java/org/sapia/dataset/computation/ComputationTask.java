package org.sapia.dataset.computation;

import org.sapia.dataset.ColumnSet;
import org.sapia.dataset.RowSet;

/**
 * Specifies the behavior for running {@link Computation}s.
 * 
 * @author yduchesne
 *
 */
public interface ComputationTask {
  
  /**
   * @param computation a {@link Computation} to register with this task.
   */
  public void add(Computation computation);
  
  /**
   * Executes the {@link Computation}s added to this instance. The provided {@link ColumnSet}
   * indicates over which columns in the {@link RowSet} the computations should be performed.
   * 
   * @param columns a {@link ColumnSet}, describing the columns in the given {@link RowSet}.
   * @param rowset a {@link RowSet}.
   * @return the {@link ComputationResults}.
   * @throws InterruptedException if the current thread is interrupted while waiting for
   * the completion of this task.
   */
  public ComputationResults compute(ColumnSet columns, RowSet rowset) throws InterruptedException;

}
