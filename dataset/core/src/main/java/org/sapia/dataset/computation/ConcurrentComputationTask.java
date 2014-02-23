package org.sapia.dataset.computation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import org.sapia.dataset.ColumnSet;
import org.sapia.dataset.RowSet;
import org.sapia.dataset.concurrent.ConcurrencyException;
import org.sapia.dataset.util.Time;

/**
 * A {@link ComputationTask} that executes its registered {@link Computation}s 
 * in parallel.
 * 
 * @author yduchesne
 *
 */
public class ConcurrentComputationTask implements ComputationTask {
  
  private ExecutorService   executor;
  private List<Computation> computations = new ArrayList<>();
  private Time              timeout;
  
  /**
   * @param executor the {@link ExecutorService} to use to perform the computations.
   * @param timeout the {@link Time} to wait until task completion - a {@link ConcurrencyException}
   * is thrown if a computation could not complete within the allowed time.
   */
  public ConcurrentComputationTask(ExecutorService executor, Time timeout) {
    this.executor = executor;
    this.timeout  = timeout;
  }
  
  @Override
  public void add(Computation computation) {
    computations.add(computation);
    
  }
  
  @Override
  public ComputationResults compute(ColumnSet columns, RowSet rows) throws InterruptedException {
    List<Future<ComputationResults>> resultsList = new ArrayList<>(computations.size());
    for (Computation c : computations) {
      resultsList.add(submit(columns, rows, c));
    }
    ComputationResults aggregated = ComputationResults.newInstance(columns);
    for (Future<ComputationResults> futureResults : resultsList) {
      try {
        aggregated.mergeWith(futureResults.get(timeout.getValue(), timeout.getUnit()));
      } catch (ExecutionException e) {
        throw new ConcurrencyException("Error occured awaiting computation result", e);
      } catch (TimeoutException e) {
        throw new ConcurrencyException("Timeout awaiting computation result", e);
      }
    }    
    return aggregated;
  }

  private Future<ComputationResults> submit(final ColumnSet columns, final RowSet rows, final Computation computation) {  
    Future<ComputationResults> future = executor.submit(new Callable<ComputationResults>() {
      @Override
      public ComputationResults call() throws Exception {
        ComputationResults results = ComputationResults.newInstance(columns);
        computation.compute(results, rows);
        return results;
      }
    });
    return future;
  }
}
