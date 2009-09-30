package org.sapia.corus.processor.task.action;

import java.io.File;
import java.io.IOException;

import org.sapia.console.CmdLine;
import org.sapia.corus.admin.services.processor.Process;
import org.sapia.corus.admin.services.processor.Process.ProcessTerminationRequestor;
import org.sapia.corus.processor.ProcessInfo;
import org.sapia.corus.processor.task.TaskConfig;

/**
 * @author Yanick Duchesne
 */
public interface ActionProvider {
  
  public CleanupProcessAction newCleanupProcessAction(TaskConfig config, Process proc);
  
  public KillConfirmedAction newKillConfirmedAction(TaskConfig config, Process proc);
  
  public MakeProcessDirAction newMakeProcessDirAction(ProcessInfo info);
  
  public RestartVmAction newRestartVmAction(TaskConfig config, Process proc);
  
  public ExecProcessAction newExecProcessAction(TaskConfig config, ProcessInfo info) throws IOException;
  
  public ExecCmdLineAction newExecCmdLineAction(File processDir, CmdLine cmdLine, Process proc);
  
  public KillProcessAction newKillProcessAction(TaskConfig config, ProcessTerminationRequestor requestor, Process proc);
  
  public AttemptKillAction newAttemptKillAction(ProcessTerminationRequestor requestor, Process proc, int currentRetryCount);
  
  public ForcefulKillAction newForcefulKillAction(TaskConfig config, ProcessTerminationRequestor requestor, String corusPid);
}
