package org.sapia.corus.taskmanager;

import org.apache.log.Hierarchy;
import org.apache.log.Logger;
import org.sapia.corus.util.ProgressQueue;
import org.sapia.corus.util.ProgressQueueImpl;
import org.sapia.taskman.NullTaskManager;
import org.sapia.taskman.Task;
import org.sapia.taskman.TaskDescriptor;
import org.sapia.taskman.TaskOutput;

public class TestTaskManager implements TaskManager{
  
  private Logger logger = Hierarchy.getDefaultHierarchy().getLoggerFor(this.getClass().getName());
  private NullTaskManager delegate = new NullTaskManager();
  private ProgressQueues _queues = new ProgressQueues();
  
  public void execAsyncTask(String name, Task task) {
    delegate.execAsyncTask(name, task, output());
  }
  
  public void execAsyncTask(String name, Task task, TaskOutput out) {
    delegate.execAsyncTask(name, task, out);
  }
  
  public ProgressQueue execSyncTask(String name, Task task) {
    TaskOutputImpl out = new TaskOutputImpl(_queues, logger);
    delegate.execAsyncTask(name, task, out);
    return out.getProgressQueue();  
  }
  
  public void execSyncTask(String name, Task task, TaskOutput out) {
    delegate.execSyncTask(name, task, out);
  }
  
  public void execTaskFor(TaskDescriptor td) {
    delegate.execTaskFor(td);
  }
  
  public ProgressQueue getProgressQueue(int level) {
    ProgressQueue queue = new ProgressQueueImpl();
    _queues.addProgressQueue(queue, level);
    return queue;
  }
  
  public String getRoleName() {
    return TaskManager.ROLE;
  }
  
  private TaskOutput output(){
    return new TestTaskOutput();
  }

}
