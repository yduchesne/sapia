package org.sapia.corus.processor.task.action;

import org.sapia.corus.CorusException;
import org.sapia.corus.CorusRuntime;
import org.sapia.corus.LogicException;
import org.sapia.corus.admin.CommandArg;
import org.sapia.corus.admin.CommandArgParser;
import org.sapia.corus.admin.services.deployer.Deployer;
import org.sapia.corus.admin.services.deployer.dist.Distribution;
import org.sapia.corus.admin.services.processor.Process;
import org.sapia.corus.processor.task.ExecTask;
import org.sapia.corus.processor.task.TaskConfig;
import org.sapia.corus.taskmanager.Action;
import org.sapia.taskman.TaskContext;

/**
 * @author Yanick Duchesne
 */
public class RestartVmAction implements Action{
  
  private TaskConfig _config;
  private Process     _proc;
  
  public RestartVmAction(TaskConfig config, Process proc){
    _config = config;
    _proc = proc;
  }
  
  public boolean execute(TaskContext ctx){
    Distribution dist; 
    ctx.getTaskOutput().debug("Executing process");    
    try{
      Deployer deployer = _config.getServices().lookup(Deployer.class);
      
      CommandArg nameArg = CommandArgParser.exact(_proc.getDistributionInfo().getName());
      CommandArg versionArg = CommandArgParser.exact(_proc.getDistributionInfo().getVersion());      
      
      dist = deployer.getDistribution(nameArg, versionArg);
    }catch(LogicException e){
      e.printStackTrace();
      ctx.getTaskOutput().error("Could not find corresponding distribution; process " + _proc.getProcessID() + " will not be restarted", e);
      return false;
    }catch(Exception e){
      e.printStackTrace();
      ctx.getTaskOutput().error("Could not look up Deployer module; process " + _proc.getProcessID() + " will not be restarted", e);
      return false;
    }    
    
    ExecTask exec = new ExecTask(_config,
                                 dist,
                                 dist.getProcess(_proc.getDistributionInfo().getProcessName()),
                                 _proc.getDistributionInfo().getProfile());
    exec.exec(ctx);
    return true;
  }

}
