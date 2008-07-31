package org.sapia.corus.taskmanager.core;

import org.apache.tools.ant.taskdefs.SQLExec.Transaction;

/**
 * Encapsulates various objects pertaining to the execution of 
 * a given task.
 * @author yduchesne
 *
 */
public interface CorusTaskContext {
  
  /**
   * @return the name of the corresponding {@link CorusTask}
   */
  public String getTaskName();
  
  /**
   * @return the {@link Transaction} to which the {@link CorusTask} corresponding to
   * this instance is associated.
   */
  public CorusTransaction getTransaction();
  
  /**
   * Executes the given task sequentially (meaning that it will be executed after the
   * previous task - if any - has completed successfully).
   * 
   * @param name the name to assign to the task.
   * @param sched an execution {@link Schedule}.
   * @param task a {@link CorusTask}
   * @return this instance.
   */
  public CorusTaskContext execSequential(String name, Schedule sched, CorusTask task);

  /**
   * Executes the given task in parallel (meaning that it will be executed in parallel to the
   * previous task - if any).
   * 
   * @param name the name to assign to the task.
   * @param sched an execution {@link Schedule}.
   * @param task a {@link CorusTask}
   * @return this instance.
   */  
  public CorusTaskContext execParallel(String name, Schedule sched, CorusTask task);

}
