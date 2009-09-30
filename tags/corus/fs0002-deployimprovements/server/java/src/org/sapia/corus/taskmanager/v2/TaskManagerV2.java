package org.sapia.corus.taskmanager.v2;

import org.sapia.corus.util.ProgressQueue;

public interface TaskManagerV2 {

  /**
   * Executes the given task in parallel (execution order is not guaranteed).
   * 
   * @param task the task to execute.
   */
  public void fork(TaskV2 task);
  
  /**
   * Executes the given task in parallel (execution order is not guaranteed).
   * 
   * @param task the task to execute.
   * @param listener a {@link TaskListener}
   */
  public void fork(TaskV2 task, TaskListener listener);
  
  /**
   * Executes the given task sequentially (execution order with other
   * sequential tasks is guaranteed).
   * 
   * @param task the task to execute.
   */
  public void execute(TaskV2 task);
  
  /**
   * Executes the given task sequentially (execution order with other
   * sequential tasks is guaranteed), returning a {@link FutureResult} 
   * which can be blocked upon.
   * 
   * @param task the task to execute
   * @return a {@link FutureResult} on which the calling thread can block.
   */
  public FutureResult executeAndWait(TaskV2 task);
  
  /**
   * This method takes a task log that will be used by the given
   * task to log its activity, upon execution.
   * 
   * @param progress the {@link ProgressQueue} to log to.
   * @see #executeAndWait(TaskV2)
   */
  public FutureResult executeAndWait(TaskV2 task, TaskLog parent);
 
  /**
   * Executes the given task in the background, indefinitely.
   * 
   * @param startDelay the delay to wait for until actually executing 
   * the task for the first time.
   * @param execInterval the interval at which the given task 
   * is to be executed.
   * @param task the task to execute.
   */
  public void executeBackground(long startDelay, long execInterval, TaskV2 task);
}
