package org.sapia.corus.taskmanager;

import org.sapia.corus.client.annotations.Bind;
import org.sapia.corus.client.common.ProgressQueue;
import org.sapia.corus.client.common.ProgressQueueImpl;
import org.sapia.corus.core.ModuleHelper;
import org.sapia.corus.taskmanager.core.BackgroundTaskConfig;
import org.sapia.corus.taskmanager.core.FutureResult;
import org.sapia.corus.taskmanager.core.SequentialTaskConfig;
import org.sapia.corus.taskmanager.core.Task;
import org.sapia.corus.taskmanager.core.TaskConfig;
import org.sapia.corus.taskmanager.core.TaskManager;
import org.sapia.corus.taskmanager.core.TaskManagerImpl;
import org.sapia.corus.taskmanager.core.Throttle;
import org.sapia.corus.taskmanager.core.ThrottleKey;
import org.sapia.corus.taskmanager.core.log.LoggerTaskLog;
import org.sapia.ubik.rmi.Remote;


/**
 * This module implements the <code>TaskManager</code> interface.
 * 
 * @author Yanick Duchesne
 */
@Bind(moduleInterface={TaskManager.class, CorusTaskManager.class})
@Remote(interfaces=CorusTaskManager.class)
public class CorusTaskManagerImpl extends ModuleHelper implements CorusTaskManager{
  
  private TaskManager 	 delegate; 
  private ProgressQueues queues = new ProgressQueues();

  /*////////////////////////////////////////////////////////////////////
                        Service INTERFACE METHODS
  ////////////////////////////////////////////////////////////////////*/

  public void init() throws Exception {
    delegate = new TaskManagerImpl(
        new ServerTaskLog(queues, new LoggerTaskLog(log)), serverContext());
  }

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
  
  @Override
  public void registerThrottle(ThrottleKey key, Throttle throttle) {
  	delegate.registerThrottle(key, throttle);
  }
  
  public <R,P> void execute(Task<R,P> task, P param) {
    delegate.execute(task, param);
  }
  
  public <R,P> void execute(Task<R,P> task, P param, SequentialTaskConfig conf) {
    delegate.execute(task, param, conf);
  }
  
  public <R,P> FutureResult<R> executeAndWait(Task<R,P> task, P param) {
    return delegate.executeAndWait(task, param);
  }
  
  public <R,P> FutureResult<R> executeAndWait(Task<R,P> task, P param, TaskConfig cfg) {
    return delegate.executeAndWait(task, param, cfg);
  }
  
  public <R,P> void executeBackground(Task<R,P> task, P param, BackgroundTaskConfig cfg) {
    delegate.executeBackground(task, param, cfg);
  }

  public ProgressQueue getProgressQueue(int level){
  	ProgressQueue queue = new ProgressQueueImpl();
  	queues.addProgressQueue(queue, level);
  	return queue;
  }

}