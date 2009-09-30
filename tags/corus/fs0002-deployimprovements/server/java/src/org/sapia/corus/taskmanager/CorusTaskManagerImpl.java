package org.sapia.corus.taskmanager;

import org.apache.log.Logger;
import org.sapia.corus.ModuleHelper;
import org.sapia.corus.ServerContext;
import org.sapia.corus.taskmanager.core.FutureResult;
import org.sapia.corus.taskmanager.core.TaskListener;
import org.sapia.corus.taskmanager.core.TaskLog;
import org.sapia.corus.taskmanager.core.TaskManager;
import org.sapia.corus.taskmanager.core.TaskManagerImpl;
import org.sapia.corus.taskmanager.core.Task;
import org.sapia.corus.util.ProgressQueue;
import org.sapia.corus.util.ProgressQueueImpl;


/**
 * This module implements the <code>TaskManager</code> interface.
 * 
 * @author Yanick Duchesne
 */
public class CorusTaskManagerImpl extends ModuleHelper implements CorusTaskManager{
  
  private TaskManagerImpl _delegate; 
  private ProgressQueues _queues = new ProgressQueues();

  /**
   * @see org.sapia.soto.Service#init()
   */
  public void init() throws Exception {
    _delegate = new TaskManagerImpl(logger(), serverContext());
    serverContext().getServices().bind(TaskManager.class, this);
    serverContext().getServices().bind(CorusTaskManager.class, this);
  }

  /**
   * @see org.sapia.soto.Service#dispose()
   */
  public void dispose() {
  }

  /*////////////////////////////////////////////////////////////////////
                         Module INTERFACE METHODS
  ////////////////////////////////////////////////////////////////////*/

  /**
   * @see org.sapia.corus.Module#getRoleName()
   */
  public String getRoleName() {
    return CorusTaskManager.ROLE;
  }

  /*////////////////////////////////////////////////////////////////////
                       TaskManager INTERFACE METHODS
  ////////////////////////////////////////////////////////////////////*/
  
  public void execute(Task task) {
    _delegate.execute(task);
  }
  
  public FutureResult executeAndWait(Task task) {
    return _delegate.executeAndWait(task);
  }
  
  public FutureResult executeAndWait(Task task, TaskLog parent) {
    return _delegate.executeAndWait(task, parent);
  }
  
  public void executeBackground(long startDelay, long execInterval, Task task) {
    _delegate.executeBackground(startDelay, execInterval, task);
  }
  
  public void fork(Task task) {
    _delegate.fork(task);
  }
  
  public void fork(Task task, TaskListener listener) {
    _delegate.fork(task, listener);
  }

  public ProgressQueue getProgressQueue(int level){
  	ProgressQueue queue = new ProgressQueueImpl();
  	_queues.addProgressQueue(queue, level);
  	return queue;
  }
  
  class InternalTaskManager extends TaskManagerImpl{
    
    public InternalTaskManager(Logger logger, ServerContext ctx) {
      super(logger, ctx);
    }
    
    @Override
    protected TaskLog createLogFor(Task task) {
      return new ServerTaskLog(_queues, super.createLogFor(task));
    }
    
    @Override
    protected TaskLog createLogFor(Task task, TaskLog parent) {
      return new ServerTaskLog(_queues, super.createLogFor(task, parent));
    }
    
  }
}
