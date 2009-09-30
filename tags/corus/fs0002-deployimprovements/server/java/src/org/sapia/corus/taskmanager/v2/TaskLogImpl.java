package org.sapia.corus.taskmanager.v2;

import org.apache.log.Logger;

public class TaskLogImpl implements TaskLog{
  
  private TaskLog parent;
  private Logger logger;
  
  
  public TaskLogImpl(Logger logger, TaskLog parent) {
    this.logger = logger;
    this.parent = parent;
  }
  
  public void debug(TaskV2 task, String msg) {
    logger.debug(task.getName() + " >> " + msg);
    if(parent != null) parent.debug(task, msg);
  }
  
  public void info(TaskV2 task, String msg) {
    logger.info(task.getName() + " >> " + msg);
    if(parent != null) parent.info(task, msg);
  }
  
  public void warn(TaskV2 task, String msg) {
    logger.warn(task.getName() + " >> " + msg);
    if(parent != null) parent.warn(task, msg);
  }
  
  public void warn(TaskV2 task, String msg, Throwable err) {
    logger.warn(task.getName() + " >> " + msg);
    if(parent != null) parent.warn(task, msg, err);
  }
  
  public void error(TaskV2 task, String msg) {
    logger.error(task.getName() + " >> " + msg);
    if(parent != null) parent.error(task, msg);
  }
  
  public void error(TaskV2 task, String msg, Throwable err) {
    logger.error(task.getName() + " >> " + msg, err);
    if(parent != null) parent.error(task, msg, err);
  }
  
}
