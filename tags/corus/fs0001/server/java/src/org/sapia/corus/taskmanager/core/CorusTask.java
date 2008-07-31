package org.sapia.corus.taskmanager.core;

/**
 * Specifies task behavior. A {@link CorusTask} is passed a {@link CorusTaskContext}
 * in its {@link #execute(CorusTaskContext)} method. The task must use the passed in 
 * context if it wishes to:
 * <ul>
 *   <li>Abort the current transaction if an error occurs (see {@link CorusTransaction#abort()}.
 *   <li>Execute sequential or parallel tasks
 * </ul>
 * Other than that, a task performs its logic normally in the body of its execute method .
 * 
 * @author yduchesne
 *
 */
public interface CorusTask {
  
  /**
   * @param ctx the {@link CorusTaskContext} that is passed to this instance.
   */
  public void execute(CorusTaskContext ctx);

}
