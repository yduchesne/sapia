package org.sapia.corus.taskmanager.v2;

/**
 * Allows registering a callback with a {@link TaskManagerV2} in order to by
 * notified when execution of a given task has completed.
 * 
 * @author yduchesne
 *
 */
public interface TaskListener {
 
  /**
   * Call when execution of a given task completes successfully.
   * @param task the {@link TaskV2} that has completed.
   * @param result the actual result of the execution, or <code>null</code>
   * if the task returned no result.
   * @see TaskV2#execute(TaskExecutionContext)
   */
  public void executionSucceeded(TaskV2 task, Object result);
 
  /**
   * Call when execution of a given task result in an error .
   * @param task the {@link TaskV2} that has completed.
   * @param err the actual error that was thrown.
   * @see TaskV2#execute(TaskExecutionContext)
   */
  public void executionFailed(TaskV2 task, Throwable err);
}
