package org.sapia.dataset.computation;

import org.sapia.dataset.RowSet;

/**
 * Interface specifying the behavior for computations over rows.
 * 
 * @author yduchesne
 *
 */
public interface Computation {
  
  /**
   * @param context a {@link ComputationResults} with which to register to computation results.
   * @param rowSet the {@link RowSet} corresponding to the rows over which computation is to be performed.
   */
  public void compute(ComputationResults context, RowSet rowSet);

}
