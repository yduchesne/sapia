package org.sapia.corus.processor.task.action;

import org.sapia.corus.admin.services.processor.Process;
import org.sapia.corus.admin.services.processor.Process.ProcessTerminationRequestor;
import org.sapia.corus.taskmanager.Action;
import org.sapia.taskman.TaskContext;

/**
 * @author Yanick Duchesne
 */
public class AttemptKillAction implements Action{
  
  private ProcessTerminationRequestor _requestor;
  private Process _proc;
  private int     _retryCount;
  
  public AttemptKillAction(ProcessTerminationRequestor requestor, Process proc, int currentRetryCount){
    _requestor = requestor;
    _proc = proc;
    _retryCount = currentRetryCount;
  }
  
  public boolean execute(TaskContext ctx) {
    if (_proc.getStatus() == Process.KILL_CONFIRMED) {
      ctx.getTaskOutput().info("Process " + _proc.getProcessID() + " has confirmed shutdown");
      return true;
    }
    ctx.getTaskOutput().info("Killing process " + _proc + ". Attempt: " + _retryCount + "; requestor: " + _requestor);
    _proc.kill(_requestor);
    return false;
  }

}
