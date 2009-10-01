package org.sapia.corus.taskmanager.core;

import org.sapia.corus.ServerContext;

/**
 * Encapsulates state pertaining to the execution of a given task.
 * 
 * @author yduchesne
 *
 */
public class TaskExecutionContext {

  private Task task;
  
  private TaskLog log;
  private TaskManager taskManager;
  private ServerContext serverContext;
  
  public TaskExecutionContext(Task t, TaskLog log, ServerContext ctx, TaskManager taskMan) {
    this.task = t;
    this.log = log;
    this.serverContext = ctx;
    this.taskManager = new InnerTaskManager(taskMan);
  }

  /**
   * @return the TaskV2 associated to this instance.
   */
  public Task getTask() {
    return task;
  }
  
  /**
   * @return this instance's {@link ServerContext}
   */
  public ServerContext getServerContext() {
    return serverContext;
  }
  
  /**
   * @return this instance's {@link TaskManager}
   */
  public TaskManager getTaskManager() {
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
  
  static class InnerTaskManager implements TaskManager{
    
    TaskManager taskManager;
    
    public InnerTaskManager(TaskManager delegate) {
      this.taskManager = delegate;
    }
    
    public void execute(Task task) {
      taskManager.execute(task);
    }
    
    public void execute(Task task, SequentialTaskConfig conf) {
      taskManager.execute(task, conf);
    }
    
    public FutureResult executeAndWait(Task task) {
      return taskManager.executeAndWait(task);
    }
    
    public FutureResult executeAndWait(Task task, TaskConfig conf) {
      return taskManager.executeAndWait(task, conf);
    }
    
    
    public void executeBackground(Task task, BackgroundTaskConfig conf) {
      taskManager.executeBackground(task, conf);
    }
    
    public void fork(Task task) {
      taskManager.fork(task);
    }
    
    public void fork(Task task, ForkedTaskConfig conf) {
      taskManager.fork(task, conf);
    }
    
  }
}
