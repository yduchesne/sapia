package org.sapia.dataset.stat;

import org.sapia.dataset.computation.CompositeComputation;
import org.sapia.dataset.computation.Computation;

/**
 * A composite {@link Computation} encapsulating a {@link MinComputation} and a {@link MaxComputation}.
 * 
 * @author yduchesne
 *
 */
public class MinMaxComputation extends CompositeComputation {

  public MinMaxComputation() {
    add(new MinComputation(), new MaxComputation());
  }
}
