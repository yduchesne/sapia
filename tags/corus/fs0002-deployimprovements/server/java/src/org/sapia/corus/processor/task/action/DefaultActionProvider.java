package org.sapia.corus.processor.task.action;

import java.io.File;
import java.io.IOException;

import org.sapia.console.CmdLine;
import org.sapia.corus.CorusRuntime;
import org.sapia.corus.admin.services.processor.Process;
import org.sapia.corus.admin.services.processor.Process.ProcessTerminationRequestor;
import org.sapia.corus.processor.ProcessInfo;
import org.sapia.corus.processor.task.TaskConfig;

/**
 * @author Yanick Duchesne
 */
public class DefaultActionProvider implements ActionProvider{
  
  public CleanupProcessAction newCleanupProcessAction(
    TaskConfig config,
    Process proc) {
    return new CleanupProcessAction(config, proc);
  }
  
  public KillConfirmedAction newKillConfirmedAction(
    TaskConfig config,
    Process proc) {
    return new KillConfirmedAction(config, proc);
  }
  
  public MakeProcessDirAction newMakeProcessDirAction(ProcessInfo info) {
    return new MakeProcessDirAction(info);
  }
  
  public RestartVmAction newRestartVmAction(TaskConfig config, Process proc) {
    return new RestartVmAction(config, proc);
  }
  
  public ExecCmdLineAction newExecCmdLineAction(File procDir, CmdLine cmdLine, Process proc) {
    return new ExecCmdLineAction(procDir, cmdLine, proc);
  }
  
  public ExecProcessAction newExecProcessAction(TaskConfig config, ProcessInfo info) throws IOException{
    return new ExecProcessAction(config, info, CorusRuntime.getProcessProperties());
  }
  
  public KillProcessAction newKillProcessAction(TaskConfig config, ProcessTerminationRequestor requestor, Process proc) {
    return new KillProcessAction(config, requestor, proc);
  }
  
  public ForcefulKillAction newForcefulKillAction(
    TaskConfig config,
    ProcessTerminationRequestor requestor,
    String corusPid) {
    return new ForcefulKillAction(config, requestor, corusPid);
  }
  
  public AttemptKillAction newAttemptKillAction(ProcessTerminationRequestor requestor, Process proc, int currentRetryCount) {
    return new AttemptKillAction(requestor, proc, currentRetryCount);
  }
  
}
