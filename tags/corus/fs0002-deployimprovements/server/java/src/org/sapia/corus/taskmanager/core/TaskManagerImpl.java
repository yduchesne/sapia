package org.sapia.corus.taskmanager.core;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log.Logger;
import org.sapia.corus.ServerContext;

public class TaskManagerImpl implements TaskManager{
  
  private Timer           background;
  private ExecutorService sequential;
  private ExecutorService parallel;
  private ServerContext   serverContext;
  private Logger          logger;
  
  public TaskManagerImpl(Logger logger, ServerContext serverContext) {
    this(logger, serverContext, 5);
  }
  
  public TaskManagerImpl(Logger logger, ServerContext serverContext, int parallelThreads) {
    this.logger = logger;
    this.serverContext = serverContext;
    this.background = new Timer("TaskManagerDaemon", true);
    this.parallel = Executors.newFixedThreadPool(parallelThreads);
    this.sequential = Executors.newFixedThreadPool(1);
  }
  
  public void execute(final Task task) {
    if(task.isMaxExecutionReached()){
      TaskExecutionContext ctx = new TaskExecutionContext(
          task,
          createLogFor(task), 
          serverContext, 
          self());
      try{
        task.onMaxExecutionReached(ctx);
      }catch(Throwable err){
        ctx.error(err);
      }
    }
    else{
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
          }finally{
            task.incrementExecutionCount();
          }
        }
      });
    }
  }
  
  public FutureResult executeAndWait(final Task task) {
    final FutureResult result = new FutureResult();
    if(task.isMaxExecutionReached()){
      TaskExecutionContext ctx = new TaskExecutionContext(
          task,
          createLogFor(task), 
          serverContext, 
          self());
      try{
        task.onMaxExecutionReached(ctx);
        result.completed(null);
      }catch(Throwable err){
        result.completed(err);
      }
    }
    else{
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
          }finally{
            task.incrementExecutionCount();
          }
        }
      });
    }
    return result;
  }
  
  public FutureResult executeAndWait(final Task task, final TaskLog parentLog) {
    final FutureResult result = new FutureResult();
    if(task.isMaxExecutionReached()){
      TaskExecutionContext ctx = new TaskExecutionContext(
          task,
          createLogFor(task), 
          serverContext, 
          self());
      try{
        task.onMaxExecutionReached(ctx);
        result.completed(null);
      }catch(Throwable err){
        result.completed(err);
      }finally{
        parentLog.close();
      }
    }
    else{
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
          }finally{
            parentLog.close();
            task.incrementExecutionCount();
          }
        }
      });
    }
    return result;
  }
  
  
  public void executeBackground(long startDelay, long execInterval, final Task task, final BackgroundTaskListener listener) {
    background.schedule(new TimerTask(){  
      @Override
      public void run() {
        if(task.isAborted()){
          super.cancel();
          background.purge();
          if(listener != null){
            listener.executionAborted(task);
          }
        }
        else if(task.isMaxExecutionReached()){
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
          if(listener != null){
            listener.executionAborted(task);
          }
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
  
  public void executeBackground(long startDelay, long execInterval, Task task) {
    this.executeBackground(startDelay, execInterval, task, null);
  }
  
  public void fork(final Task task) {
    if(task.isMaxExecutionReached()){
      TaskExecutionContext ctx = new TaskExecutionContext(
          task,
          createLogFor(task), 
          serverContext, 
          self());
      try{
        task.onMaxExecutionReached(ctx);
      }catch(Throwable err){
        ctx.error(err);
      }
    }
    else{
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
          }finally{
            task.incrementExecutionCount();
          }
        }
      });
    }
  }
  
  public void fork(final Task task, final TaskListener listener) {
    if(task.isMaxExecutionReached()){
      TaskExecutionContext ctx = new TaskExecutionContext(
          task,
          createLogFor(task), 
          serverContext, 
          self());
      try{
        task.onMaxExecutionReached(ctx);
      }catch(Throwable err){
        ctx.error(err);
      }
    }
    else{
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
          }finally{
            task.incrementExecutionCount();
          }
        }
      });
    }
  }
  
  public void shutdown(){
    sequential.shutdown();
    parallel.shutdown();
  }
  
  protected TaskLog createLogFor(Task task){
    return new TaskLogImpl(logger, null);
  }
  
  protected TaskLog createLogFor(Task task, TaskLog parent){
    return new TaskLogImpl(logger, parent);
  }
  
  private TaskManager self(){
    return this;
  }

}
