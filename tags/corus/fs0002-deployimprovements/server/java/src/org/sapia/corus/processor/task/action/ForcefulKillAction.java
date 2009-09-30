package org.sapia.corus.processor.task.action;

import java.io.IOException;
import java.util.Date;

import org.sapia.corus.LogicException;
import org.sapia.corus.admin.services.port.PortManager;
import org.sapia.corus.admin.services.processor.Process;
import org.sapia.corus.admin.services.processor.Processor;
import org.sapia.corus.admin.services.processor.Process.ProcessTerminationRequestor;
import org.sapia.corus.processor.NativeProcessFactory;
import org.sapia.corus.processor.task.TaskConfig;
import org.sapia.corus.server.processor.ProcessRepository;
import org.sapia.corus.taskmanager.Action;
import org.sapia.taskman.TaskContext;

/**
 * @author Yanick Duchesne
 */
public class ForcefulKillAction implements Action{
  
  private TaskConfig                     _config;
  private ProcessTerminationRequestor    _requestor;
  private String                         _corusPid;
  
  public ForcefulKillAction(TaskConfig config, ProcessTerminationRequestor requestor, String corusPid){
    _config = config;
    _requestor = requestor;
    _corusPid = corusPid;
  }
  
  /**
   * @see org.sapia.corus.taskmanager.Action#execute(org.sapia.taskman.TaskContext)
   */
  public boolean execute(TaskContext ctx) {
    boolean killSuccessful = false;

    try {

      PortManager ports   = _config.getServices().lookup(PortManager.class);
      Processor processor = _config.getServices().lookup(Processor.class);
      ProcessRepository processes = _config.getServices().getProcesses();
      Process process = processes.getActiveProcesses().getProcess(_corusPid);

      ctx.getTaskOutput().warning("Process " + process.getProcessID() +
                    " did not confirm kill: " + process + "; requestor: " + _requestor);

      // try forcefull kill if OS pid not null...
      if (process.getOsPid() != null) {
        try {
          doNativeKill(ctx, process);
          killSuccessful = true;
        } catch (IOException e) {
          ctx.getTaskOutput().warning("Error performing OS kill on process " +
                        process.getOsPid());
          ctx.getTaskOutput().error(e);
        }
      } else {
        ctx.getTaskOutput().warning("Process " + _corusPid +
                      " is stalled but could not be killed");
      }
      
      process.releasePorts(ports);

      if(!ActionFactory.newCleanupProcessAction(_config, process).execute(ctx)){
        ctx.getTaskOutput().warning("Process " + process.getProcessID() + " will not be restarted");
        return false;
      }

      // if shutdown was initiated by Corus server, restart process
      // automatically (if restarted interval threshold is respected)
      if (_requestor == ProcessTerminationRequestor.KILL_REQUESTOR_SERVER && processor.getConfiguration().getRestartIntervalMillis() > 0) {
        ctx.getTaskOutput().debug("Preparing for restart");
        ctx.getTaskOutput().debug("Process creation time: " + new Date(process.getCreationTime()));
        ctx.getTaskOutput().debug("Current time: " + new Date());
        ctx.getTaskOutput().debug("Restart interval: " + processor.getConfiguration().getRestartInterval() + " seconds");
        // if no OS pid, then process could not be forcefully killed...
        if (process.getOsPid() == null) {
          ctx.getTaskOutput().warning("Not restarting process: " + process.getProcessID() +
                        "; did not confirm shutdown");
          ctx.getTaskOutput().warning("Could not be forcefully killed (because it does not have an OS pid)");
          ctx.getTaskOutput().warning("Might be stalled... Make sure that you do not have a process in limbo");
          onNoOsPid();
        } else if (((System.currentTimeMillis() - process.getCreationTime()) < processor.getConfiguration().getRestartIntervalMillis())) {
          ctx.getTaskOutput().warning("Process will not be restarted; not enough time since last restart");
          onRestartThresholdInvalid();
        } else {
          ctx.getTaskOutput().warning("Restarting Process: " + process);
          killSuccessful = ActionFactory.newRestartVmAction(_config, process).execute(ctx);
          onRestarted();
        }
      } else {
        ctx.getTaskOutput().warning("Process " + process.getProcessID() + " terminated");
      }
    } catch (LogicException e) {
      ctx.getTaskOutput().error(e);
    }

    return killSuccessful;
  }
  
  protected void doNativeKill(TaskContext ctx, Process proc) throws IOException{
    NativeProcessFactory.newNativeProcess().kill(ctx, proc.getOsPid());    
  }
  
  protected void onNoOsPid(){}
  
  protected void onRestartThresholdInvalid(){}
  
  protected void onRestarted(){}

}
