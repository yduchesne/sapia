package org.sapia.dataset.computation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Encapsulates other computations. Invokes them in the order in which
 * they were added.
 * 
 * @author yduchesne
 *
 */
public class CompositeComputation implements Computation {
  
  private List<Computation> computations = new ArrayList<>();
  
  /**
   * @param toAdd one or more {@link Computation}s to add.
   */
  public void add(Computation...toAdd) {
    computations.addAll(Arrays.asList(toAdd));
  }
  
  @Override
  public void compute(ComputationResults context, org.sapia.dataset.RowSet rowSet) {
    for (Computation c : computations) {
      c.compute(context, rowSet);
    }
  }

}
