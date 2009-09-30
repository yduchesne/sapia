package org.sapia.corus.taskmanager.v2;

/**
 * An instance of this interface allows task to output logging information.
 * <p/>
 * Each method corresponds to a specific log level. 
 */
public interface TaskLog {
  
  public void debug(TaskV2 task, String msg);
  
  public void info(TaskV2 task, String msg);
  
  public void warn(TaskV2 task, String msg);

  public void warn(TaskV2 task, String msg, Throwable err);

  public void error(TaskV2 task, String msg);
  
  public void error(TaskV2 task, String msg, Throwable err);

}
