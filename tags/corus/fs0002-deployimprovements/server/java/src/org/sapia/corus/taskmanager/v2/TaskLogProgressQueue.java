package org.sapia.corus.taskmanager.v2;

import org.sapia.corus.util.ProgressQueue;

public class TaskLogProgressQueue implements TaskLog{
  
  private ProgressQueue progress;
  
  public TaskLogProgressQueue(ProgressQueue progress) {
    this.progress = progress;
  }
  
  public void debug(TaskV2 task, String msg) {
    progress.debug(format(task, msg));
  }
  
  public void info(TaskV2 task, String msg) {
    progress.info(format(task, msg));
  }
  
  public void warn(TaskV2 task, String msg) {
    progress.warning(format(task, msg));
  }
  
  public void warn(TaskV2 task, String msg, Throwable err) {
    progress.warning(err);
  }
  
  public void error(TaskV2 task, String msg) {
    progress.error(format(task, msg));    
  }
  
  public void error(TaskV2 task, String msg, Throwable err) {
    progress.error(err);
  }

  private String format(TaskV2 task, String msg){
    return task.getName() + " >> " + msg;
  }
}
