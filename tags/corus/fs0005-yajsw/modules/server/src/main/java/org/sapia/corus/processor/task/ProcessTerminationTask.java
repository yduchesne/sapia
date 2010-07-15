package org.sapia.corus.processor.task;

import org.sapia.corus.client.exceptions.processor.ProcessLockException;
import org.sapia.corus.client.exceptions.processor.ProcessNotFoundException;
import org.sapia.corus.client.services.port.PortManager;
import org.sapia.corus.client.services.processor.LockOwner;
import org.sapia.corus.client.services.processor.Process;
import org.sapia.corus.client.services.processor.Process.ProcessTerminationRequestor;
import org.sapia.corus.taskmanager.core.Task;
import org.sapia.corus.taskmanager.core.TaskExecutionContext;

/**
 * Abstract class that provides convenient basic behavior for process-terminating tasks.
 * 
 * @author Yanick Duchesne
 *
 */
public abstract class ProcessTerminationTask extends Task {

  private String                      _corusPid;
  private ProcessTerminationRequestor _requestor;
  private LockOwner                   _lockOwner = Process.createLockOwner();

  /**
   * @param maxRetry the maximum number of times this instance will try
   * to terminate a process.
   * @param retryInterval the interval between retries (in seconds).
   */
  public ProcessTerminationTask(
       ProcessTerminationRequestor requestor,
       String corusPid, 
       int maxRetry) {
    setMaxExecution(maxRetry);
    _requestor = requestor;
    _corusPid  = corusPid;
  }
  
  LockOwner lockOwner(){
    return _lockOwner;
  }
  
  @Override
  public Object execute(TaskExecutionContext ctx) throws Throwable {
    Process proc = null;
    try{
      proc = ctx.getServerContext().getServices().getProcesses().getActiveProcesses().getProcess(_corusPid);
      proc.acquireLock(_lockOwner);
      proc.save();
    } catch (ProcessLockException e) {
      ctx.error("Could not acquire lock on process: " + _corusPid);
      ctx.error(e);
      abort(ctx);
      return null;
    } catch (ProcessNotFoundException e) {
      ctx.error("Process not found for: " + _corusPid);
      ctx.error(e);
      abort(ctx);
      return null;
    }
    proc.refresh();
    if (proc.getStatus() == Process.LifeCycleStatus.KILL_CONFIRMED) {
      proc.releasePorts(ctx.getServerContext().getServices().lookup(PortManager.class));
      proc.save();
      onKillConfirmed(ctx);
      abort(ctx);
    } else {
      onExec(ctx);
    }
    return null;
  }
  
  /**
   * @return the Corus process identifier of the process to terminate.
   */
  protected String corusPid() {
    return _corusPid;
  }

  /**
   * @return the logical identifier of the originator of the termination
   * request.
   */
  protected ProcessTerminationRequestor requestor() {
    return _requestor;
  }

  
  @Override
  protected void abort(TaskExecutionContext ctx) {
    try{
      Process proc = ctx.getServerContext().getServices().getProcesses().getActiveProcesses().getProcess(_corusPid);
      proc.releaseLock(_lockOwner);
      proc.save();
    }catch(Throwable err){
      //ctx.error(err);
    }finally{
      super.abort(ctx);
    }
  }
  
  /**
   * Template method that is called when the process corresponding
   * to this task has shut down.
   * @param ctx a {@link TaskExecutionContext}.
   */  
  protected abstract void onKillConfirmed(TaskExecutionContext ctx) throws Throwable;
  
  protected abstract void onExec(TaskExecutionContext ctx) throws Throwable;

}
