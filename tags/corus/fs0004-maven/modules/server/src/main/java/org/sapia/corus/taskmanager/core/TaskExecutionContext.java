package org.sapia.corus.taskmanager.core;

import org.sapia.corus.core.ServerContextImpl;
import org.sapia.corus.core.ServerContext;

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
    this.taskManager = new InnerTaskManager(t, log, taskMan);
  }

  /**
   * @return the TaskV2 associated to this instance.
   */
  public Task getTask() {
    return task;
  }
  
  /**
   * @return this instance's {@link ServerContextImpl}
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
    
    TaskManager owner;
    Task task;
    TaskLog log;
    public InnerTaskManager(Task task, TaskLog log, TaskManager delegate) {
      this.task = task;
      this.owner = delegate;
      this.log = log;
    }
    
    public void execute(Task child) {
      execute(child, SequentialTaskConfig.create());
    }
    
    public void execute(Task child, SequentialTaskConfig conf) {
      owner.execute(child, conf);
    }
    
    public FutureResult executeAndWait(Task child) {
      return executeAndWait(child, new TaskConfig());
    }
    
    public FutureResult executeAndWait(Task child, TaskConfig conf) {
      task.addChild(child);
      if(conf.getLog() == null){
        conf.setLog(log);
      }
      return owner.executeAndWait(child, conf);
    }
    
    
    public void executeBackground(Task child, BackgroundTaskConfig conf) {
      owner.executeBackground(child, conf);
    }
    
  }
}
