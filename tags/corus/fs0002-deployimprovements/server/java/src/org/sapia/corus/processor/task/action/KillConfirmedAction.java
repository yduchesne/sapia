package org.sapia.corus.processor.task.action;

import org.sapia.corus.admin.services.port.PortManager;
import org.sapia.corus.admin.services.processor.Process;
import org.sapia.corus.processor.task.TaskConfig;
import org.sapia.corus.taskmanager.Action;
import org.sapia.taskman.TaskContext;

/**
 * @author Yanick Duchesne
 */
public class KillConfirmedAction implements Action{
  
  private TaskConfig _config;
  private Process   _proc;
  
  public KillConfirmedAction(TaskConfig config, Process proc){
    _config = config;
    _proc = proc;
  }
 
  public boolean execute(TaskContext ctx) {
    PortManager ports = _config.getServices().lookup(PortManager.class);
    ctx.getTaskOutput().info("Process kill confirmed: " + _proc.getProcessID());
    _proc.releasePorts(ports);
    if(!new CleanupProcessAction(_config, _proc).execute(ctx)){
      return false;
    }
    ctx.getTaskOutput().warning("Process " + _proc.getProcessID() + " terminated");
    return true;
  }
}
