package org.sapia.dataset.computation;

import java.util.concurrent.ExecutorService;

import org.sapia.dataset.util.Time;

/**
 * Holds computation-related methods.
 * 
 * @author yduchesne
 *
 */
public class Computations {
  
  private Computations() {
  }

  /**
   * Returns a task that executes its registered computations in parallel.
   * 
   * @param executor the {@link ExecutorService} to be used by the returned task.
   * @param timeout the timeout value corresponding to the maximum amount of the 
   * time given to perform all the task's computations.
   * 
   * @return a new {@link ComputationTask}.
   */
  public static ComputationTask parallel(ExecutorService executor, Time timeout) {
    return new ConcurrentComputationTask(executor, timeout);
  }
  
  /**
   * @return a {@link ComputationTask} that performs its computations in parallel.
   */
  public static ComputationTask sequential() {
    return new SequentialComputationTask();
  }
}
