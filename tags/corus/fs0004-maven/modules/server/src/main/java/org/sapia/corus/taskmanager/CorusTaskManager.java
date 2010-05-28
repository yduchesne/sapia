package org.sapia.corus.taskmanager;

import org.sapia.corus.annotations.Bind;
import org.sapia.corus.taskmanager.core.TaskManager;
import org.sapia.corus.util.progress.ProgressMsg;
import org.sapia.corus.util.progress.ProgressQueue;

/**
 * Extends the {@link TaskManager} interface by adding the 
 * {@link #getProgressQueue(int)} method.
 * 
 * @author yduchesne
 *
 */
@Bind(moduleInterface=CorusTaskManager.class)
public interface CorusTaskManager extends TaskManager{

  public static final String ROLE = CorusTaskManager.class.getName();
  
  /**
   * @param level a progress level.
   * @return a {@link ProgressQueue}
   * @see ProgressMsg
   */
  public ProgressQueue getProgressQueue(int level);

}
