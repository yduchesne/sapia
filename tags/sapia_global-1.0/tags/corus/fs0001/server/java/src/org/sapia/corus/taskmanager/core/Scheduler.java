package org.sapia.corus.taskmanager.core;

/**
 * This interface specifies the task scheduling behavior.
 *  
 * @author yduchesne
 *
 */
public interface Scheduler {

  /**
   * 
   * @return a new {@link CorusTransaction}
   */
  public CorusTransaction begin();
  
  /**
   * @param name the name to assign to the given {@link CorusTask}
   * @param tx the {@link CorusTransaction} to associate to the task.
   * @param sched an execution {@link Schedule}
   * @param task a {@link CorusTask} to execute.
   */
  public void schedule(String name, CorusTransaction tx, Schedule sched, CorusTask task);
  
}
