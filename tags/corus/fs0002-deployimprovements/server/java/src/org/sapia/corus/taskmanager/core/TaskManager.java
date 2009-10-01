package org.sapia.corus.taskmanager.core;

public interface TaskManager {

  /**
   * Executes the given task in parallel (execution order is not guaranteed).
   * 
   * @param task the task to execute.
   */
  public void fork(Task task);
  
  /**
   * Executes the given task in parallel (execution order is not guaranteed).
   * 
   * @param task the task to execute.
   * @param conf a {@link ForkedTaskConfig}
   */
  public void fork(Task task, ForkedTaskConfig conf);
  
  /**
   * Executes the given task sequentially (execution order with other
   * sequential tasks is guaranteed).
   * 
   * @param task the {@link Task} to execute.
   */
  public void execute(Task task);
  
  /**
   * @param task the {@link Task} to execute.
   * @param conf a {@link SequentialTaskConfig}
   */
  public void execute(Task task, SequentialTaskConfig conf);
  
  /**
   * Executes the given task sequentially (execution order with other
   * sequential tasks is guaranteed), returning a {@link FutureResult} 
   * which can be blocked upon.
   * 
   * @param task the task to execute
   * @return a {@link FutureResult} on which the calling thread can block.
   */
  public FutureResult executeAndWait(Task task);
  
  /**
   * This method takes a task log that will be used by the given
   * task to log its activity, upon execution.
   * 
   * @param a {@link TaskConfig}
   * @see #executeAndWait(Task)
   */
  public FutureResult executeAndWait(Task task, TaskConfig conf);
 
  /**
   * Executes the given task in the background, indefinitely.
   * @param task the task to execute.
   * @param config a {@link BackgroundTaskConfig}.
   */
  public void executeBackground(Task task, BackgroundTaskConfig config);
}
