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
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class ActionFactory {
  static ActionProvider _provider;

  public static CleanupProcessAction newCleanupProcessAction(TaskConfig config, Process proc){
    return provider().newCleanupProcessAction(config, proc);
  }
  
  public static KillConfirmedAction newKillConfirmedAction(TaskConfig config, Process proc){
    return provider().newKillConfirmedAction(config, proc);
  }
  
  public static MakeProcessDirAction newMakeProcessDirAction(ProcessInfo info){
    return provider().newMakeProcessDirAction(info);
  }
  
  public static RestartVmAction newRestartVmAction(TaskConfig config, Process proc){
    return provider().newRestartVmAction(config, proc);
  }
  
  public static ExecCmdLineAction newExecCmdLineAction(File procDir, CmdLine cmdLine, Process proc){
    return provider().newExecCmdLineAction(procDir, cmdLine, proc);
  }
  
  public static ExecProcessAction newExecProcessAction(TaskConfig config, ProcessInfo info) throws IOException{
    return provider().newExecProcessAction(config, info);
  } 
  
  public static KillProcessAction newKillProcessAction(
      TaskConfig config,
      ProcessTerminationRequestor requestor,
      Process proc) {
    return provider().newKillProcessAction(config, requestor, proc);
  }
  
  public static ForcefulKillAction newForcefulKillAction(TaskConfig config, ProcessTerminationRequestor requestor, String corusPid){
    return provider().newForcefulKillAction(config, requestor, corusPid);
  }
  
  public static AttemptKillAction newAttemptKillAction(ProcessTerminationRequestor requestor, Process proc, int currentRetryCount){
    return provider().newAttemptKillAction(requestor, proc, currentRetryCount);
  }  
  
  
  
  public static void setActionProvider(ActionProvider provider){
    if(_provider != null){
      throw new IllegalStateException("Provider already set");
    }
    _provider = provider;
  }
  
  public static boolean hasProvider(){
    return _provider != null;
  }
  
  private static ActionProvider provider(){
    if(_provider == null){
      _provider = new DefaultActionProvider();
    }
    return _provider;
  }
  
}
