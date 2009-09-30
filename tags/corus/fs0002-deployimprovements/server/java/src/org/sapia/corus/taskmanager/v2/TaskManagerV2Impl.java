package org.sapia.corus.taskmanager.v2;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log.Logger;
import org.sapia.corus.ServerContext;

public class TaskManagerV2Impl implements TaskManagerV2{
  
  private Timer           background;
  private ExecutorService sequential;
  private ExecutorService parallel;
  private ServerContext   serverContext;
  private Logger          logger;
  
  public TaskManagerV2Impl(Logger logger, ServerContext serverContext) {
    this(logger, serverContext, 5);
  }
  
  public TaskManagerV2Impl(Logger logger, ServerContext serverContext, int parallelThreads) {
    this.logger = logger;
    this.serverContext = serverContext;
    this.background = new Timer("TaskManagerDaemon", true);
    this.parallel = Executors.newFixedThreadPool(parallelThreads);
    this.sequential = Executors.newFixedThreadPool(1);
  }
  
  public void execute(final TaskV2 task) {
    sequential.execute(new Runnable(){
      public void run() {
        TaskExecutionContext ctx = new TaskExecutionContext(
            task,
            createLogFor(task), 
            serverContext, 
            self());
        try{
          task.execute(ctx);
        }catch(Throwable t){
          ctx.getLog().error(task, "Problem occurred executing task", t);
        }
      }
    });
  }
  
  public FutureResult executeAndWait(final TaskV2 task) {
    final FutureResult result = new FutureResult();
    sequential.execute(new Runnable(){
      public void run() {
        try{
          Object o = task.execute(new TaskExecutionContext(
              task,
              createLogFor(task), 
              serverContext, 
              self()));
          result.completed(o);
        }catch(Throwable t){
          result.completed(t);
        }
      }
    });
    return result;
  }
  
  public FutureResult executeAndWait(final TaskV2 task, final TaskLog parentLog) {
    final FutureResult result = new FutureResult();
    sequential.execute(new Runnable(){
      public void run() {
        try{
          Object o = task.execute(new TaskExecutionContext(
              task,
              createLogFor(task, parentLog), 
              serverContext, 
              self()));
          result.completed(o);
        }catch(Throwable t){
          result.completed(t);
        }
      }
    });
    return result;
  }
  
  
  public void executeBackground(long startDelay, long execInterval, final TaskV2 task) {
    background.schedule(new TimerTask(){  
      @Override
      public void run() {
        if(task.isAborted()){
          super.cancel();
          background.purge();
        }
        else if(task.getMaxExecution() > 0 && task.getExecutionCount() >= task.getMaxExecution()){
          TaskExecutionContext ctx = new TaskExecutionContext(
              task,
              createLogFor(task), 
              serverContext, 
              self());
          try{
            task.onMaxExecutionReached(ctx);
            
          }catch(Throwable err){
            ctx.getLog().error(task, "Error terminating task");
          }
          super.cancel();
          background.purge();
        }
        else{
          TaskExecutionContext ctx = new TaskExecutionContext(
              task,
              createLogFor(task), 
              serverContext, 
              self());
          try{
            task.execute(ctx);
          }catch(Throwable t){
            ctx.getLog().error(task, "Problem occurred executing task", t);
          }finally{
            task.incrementExecutionCount();
          }
        }
      }
    }, startDelay, execInterval);
  }
  
  public void fork(final TaskV2 task) {
    parallel.execute(new Runnable(){
      public void run() {
        TaskExecutionContext ctx = new TaskExecutionContext(
            task,
            createLogFor(task), 
            serverContext, 
            self());
        try{
          task.execute(ctx);
        }catch(Throwable t){
          ctx.getLog().error(task, "Problem occurred executing task", t);
        }
      }
    });
    
  }
  
  public void fork(final TaskV2 task, final TaskListener listener) {
    parallel.execute(new Runnable(){
      public void run() {
        try{
          Object o = task.execute(new TaskExecutionContext(
              task,
              createLogFor(task),
              serverContext, 
              self()));
          listener.executionSucceeded(task, o);
        }catch(Throwable t){
          listener.executionFailed(task, t);
        }
      }
    });
  }
  
  public void shutdown(){
    sequential.shutdown();
    parallel.shutdown();
  }
  
  protected TaskLog createLogFor(TaskV2 task){
    return new TaskLogImpl(logger, null);
  }
  
  protected TaskLog createLogFor(TaskV2 task, TaskLog parent){
    return new TaskLogImpl(logger, parent);
  }
  
  private TaskManagerV2 self(){
    return this;
  }

}
