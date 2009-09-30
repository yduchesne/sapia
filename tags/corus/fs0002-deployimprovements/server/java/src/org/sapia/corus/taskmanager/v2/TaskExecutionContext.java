package org.sapia.corus.taskmanager.v2;

import org.sapia.corus.ServerContext;

/**
 * Encapsulates state pertaining to the execution of a given task.
 * 
 * @author yduchesne
 *
 */
public class TaskExecutionContext {

  private TaskV2 task;
  
  private TaskLog log;
  private TaskManagerV2 taskManager;
  private ServerContext serverContext;
  
  public TaskExecutionContext(TaskV2 t, TaskLog log, ServerContext ctx, TaskManagerV2 taskMan) {
    this.task = t;
    this.log = log;
    this.serverContext = ctx;
    this.taskManager = new InnerTaskManager(taskMan);
  }

  /**
   * @return the TaskV2 associated to this instance.
   */
  public TaskV2 getTask() {
    return task;
  }
  
  /**
   * @return this instance's {@link ServerContext}
   */
  public ServerContext getServerContext() {
    return serverContext;
  }
  
  /**
   * @return this instance's {@link TaskManagerV2}
   */
  public TaskManagerV2 getTaskManager() {
    return taskManager;
  }

  /**
   * @return this instance's {@link TaskLog}
   */
  public TaskLog getLog() {
    return log;
  }
  
  public void debug(String msg){
    log.debug(task, msg);
  }
  
  public void info(String msg){
    log.info(task, msg);
  }
  
  public void warn(String msg){
    log.warn(task, msg);
  }
  
  public void warn(String msg, Throwable err){
    log.warn(task, msg, err);
  }
  
  public void error(String msg){
    log.error(task, msg);
  }
  
  public void error(String msg, Throwable err){
    log.error(task, msg, err);
  }
  
  public void error(Throwable err){
    log.error(task, "Error caught", err);
  }
  
  ///////////// Inner task manager impl
  
  static class InnerTaskManager implements TaskManagerV2{
    
    TaskManagerV2 taskManager;
    
    public InnerTaskManager(TaskManagerV2 delegate) {
      this.taskManager = delegate;
    }
    
    public void execute(TaskV2 task) {
      taskManager.execute(task);
    }
    
    public FutureResult executeAndWait(TaskV2 task) {
      return taskManager.executeAndWait(task);
    }
    
    public FutureResult executeAndWait(TaskV2 task, TaskLog parent) {
      return taskManager.executeAndWait(task, parent);
    }
    
    public void executeBackground(long startDelay, long execInterval,
        TaskV2 task) {
      taskManager.executeBackground(startDelay, execInterval, task);
    }
    
    public void fork(TaskV2 task) {
      taskManager.fork(task);
    }
    
    public void fork(TaskV2 task, TaskListener listener) {
      taskManager.fork(task, listener);
    }
    
  }
}
