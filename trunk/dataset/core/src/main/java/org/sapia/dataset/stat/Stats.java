package org.sapia.dataset.stat;

import java.util.List;

import org.sapia.dataset.Dataset;
import org.sapia.dataset.computation.ComputationResult;
import org.sapia.dataset.computation.ComputationResults;
import org.sapia.dataset.computation.ComputationTask;
import org.sapia.dataset.computation.Computations;
import org.sapia.dataset.concurrent.Threading;
import org.sapia.dataset.help.Doc;
import org.sapia.dataset.impl.DatasetRowSetAdapter;

/**
 * Holds stats-related methods.
 *  
 * @author yduchesne
 *
 */
public class Stats {

  /**
   * Constant to which the mean {@link ComputationResult} is bound.
   */
  public static final String MEAN     = "mean";

  /**
   * Constant to which the variance {@link ComputationResult} is bound.
   */
  public static final String VARIANCE = "variance";
  
  /**
   * Constant to which the standard deviation {@link ComputationResult} is bound.
   */
  public static final String STDDEV   = "stddev";
  
  /**
   * Constant to which the min {@link ComputationResult} is bound.
   */
  public static final String MIN      = "min";
  
  /**
   * Constant to which the max {@link ComputationResult} is bound.
   */
  public static final String MAX      = "max";
  
  private Stats() {
  }

  /**
   * Adds summary stats computations with the given {@link ComputationTask}.
   * 
   * @param task a {@link ComputationTask}.
   * @see SpreadStatsComputation
   * @see MedianComputation
   * @see MinMaxComputation
   */
  @Doc("Internally registers summary stats computations with the given task")
  public static void summary(ComputationTask task) {
    task.add(new SpreadStatsComputation());
    task.add(new MedianComputation());
    task.add(new MinMaxComputation());
  }

  /**
   * @param dataset the dataset for which to perform summary stats computation.
   * @param columnNames the names of the columns to use.
   * @return the {@link ComputationResults}.
   * @throws IllegalArgumentException if an invalid argument has been passed.
   * @throws InterruptedException if the calling thread is interrupted while waiting
   * for the end of the summary computation.
   */
  @Doc("Computes summary statistics for the given dataset - and for the specified columns")
  public static ComputationResults summary(@Doc("a dataset") Dataset dataset, @Doc("the column names") List<String> columnNames) 
      throws IllegalArgumentException, InterruptedException {
    return summary(dataset, columnNames.toArray(new String[columnNames.size()]));
  }
  
  /**
   * @param dataset the dataset for which to perform summary stats computation.
   * @param columnNames the names of the columns to use.
   * @return the {@link ComputationResults}.
   * @throws IllegalArgumentException if an invalid argument has been passed.
   * @throws InterruptedException if the calling thread is interrupted while waiting
   * for the end of the summary computation.
   */
  @Doc("Computes summary statistics for the given dataset - and for the specified columns")
  public static ComputationResults summary(@Doc("a dataset") Dataset dataset, @Doc("the column names") String...columnNames) 
      throws IllegalArgumentException, InterruptedException {
    ComputationTask task = Computations.parallel(Threading.getThreadPool(), Threading.getTimeout());
    summary(task);
    return task.compute(dataset.getColumnSet().includes(columnNames), new DatasetRowSetAdapter(dataset));
  }

  /**
   * @param dataset the dataset for which to perform summary stats computation.
   * @return the {@link ComputationResults}.
   * @throws IllegalArgumentException if an invalid argument has been passed.
   * @throws InterruptedException if the calling thread is interrupted while waiting
   * for the end of the summary computation.
   */
  @Doc("Computes summary statistics for all columns in the given dataset")
  public static ComputationResults summary(@Doc("a dataset") Dataset dataset) 
      throws IllegalArgumentException, InterruptedException {
    ComputationTask task = Computations.parallel(Threading.getThreadPool(), Threading.getTimeout());
    summary(task);
    return task.compute(dataset.getColumnSet(), new DatasetRowSetAdapter(dataset));
  }
}
