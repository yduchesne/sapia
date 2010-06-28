package org.sapia.corus.taskmanager;

import java.rmi.Remote;

import org.apache.log.Logger;
import org.sapia.corus.client.annotations.Bind;
import org.sapia.corus.client.common.ProgressQueue;
import org.sapia.corus.client.common.ProgressQueueImpl;
import org.sapia.corus.core.ModuleHelper;
import org.sapia.corus.core.ServerContext;
import org.sapia.corus.taskmanager.core.BackgroundTaskConfig;
import org.sapia.corus.taskmanager.core.ForkedTaskConfig;
import org.sapia.corus.taskmanager.core.FutureResult;
import org.sapia.corus.taskmanager.core.SequentialTaskConfig;
import org.sapia.corus.taskmanager.core.Task;
import org.sapia.corus.taskmanager.core.TaskConfig;
import org.sapia.corus.taskmanager.core.TaskLog;
import org.sapia.corus.taskmanager.core.TaskManager;
import org.sapia.corus.taskmanager.core.TaskManagerImpl;


/**
 * This module implements the <code>TaskManager</code> interface.
 * 
 * @author Yanick Duchesne
 */
@Bind(moduleInterface=CorusTaskManager.class)
public class CorusTaskManagerImpl extends ModuleHelper implements CorusTaskManager, Remote{
  
  private InternalTaskManager _delegate; 
  private ProgressQueues _queues = new ProgressQueues();

  /**
   * @see org.sapia.corus.core.soto.Service#init()
   */
  public void init() throws Exception {
    _delegate = new InternalTaskManager(logger(), serverContext());
  }

  /**
   * @see org.sapia.corus.core.soto.Service#dispose()
   */
  public void dispose() {
  }

  /*////////////////////////////////////////////////////////////////////
                         Module INTERFACE METHODS
  ////////////////////////////////////////////////////////////////////*/

  /**
   * @see org.sapia.corus.client.Module#getRoleName()
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
  
  public void execute(Task task, SequentialTaskConfig conf) {
    _delegate.execute(task, conf);
  }
  
  public FutureResult executeAndWait(Task task) {
    return _delegate.executeAndWait(task);
  }
  
  public FutureResult executeAndWait(Task task, TaskConfig cfg) {
    return _delegate.executeAndWait(task, cfg);
  }
  
  public void executeBackground(Task task, BackgroundTaskConfig cfg) {
    _delegate.executeBackground(task, cfg);
  }
  
  public void fork(Task task) {
    _delegate.fork(task);
  }
  
  public void fork(Task task, ForkedTaskConfig cfg) {
    _delegate.fork(task, cfg);
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
    protected TaskLog createLogFor(Task task, TaskLog delegate) {
      if(task.isRoot()){
        return new ServerTaskLog(_queues, super.createLogFor(task, delegate));
      }
      else{
        return super.createLogFor(task, delegate);
      }
    }
    
  }
}
