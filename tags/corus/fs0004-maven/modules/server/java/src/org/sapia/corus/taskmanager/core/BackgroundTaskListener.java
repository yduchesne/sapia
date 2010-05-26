package org.sapia.corus.taskmanager.core;

/**
 * This interface inherits from the {@link TaskListener} interface by 
 * adding 2 additional methods.
 * 
 * @author yduchesne
 *
 */
public interface BackgroundTaskListener extends TaskListener{
  
  /**
   * @param task the {@link Task} whose max execution has been reached.
   */
  public void maxExecutionReached(Task task);

  /**
   * @param task the {@link Task} whose execution was aborted.
   */
  public void executionAborted(Task task);
}
