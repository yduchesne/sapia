package org.sapia.corus.processor.task;

import java.util.List;

import org.sapia.corus.LogicException;
import org.sapia.corus.admin.CommandArg;
import org.sapia.corus.admin.StringCommandArg;
import org.sapia.corus.admin.services.deployer.Deployer;
import org.sapia.corus.admin.services.deployer.dist.Distribution;
import org.sapia.corus.admin.services.deployer.dist.ProcessConfig;
import org.sapia.corus.admin.services.processor.ExecConfig;
import org.sapia.corus.admin.services.processor.Process;
import org.sapia.corus.admin.services.processor.ProcessDef;
import org.sapia.corus.admin.services.processor.Processor;
import org.sapia.corus.processor.ProcessDependencyFilter;
import org.sapia.corus.processor.ProcessRef;
import org.sapia.corus.processor.StartupLock;
import org.sapia.corus.server.processor.ExecConfigDatabase;
import org.sapia.corus.server.processor.ProcessRepository;
import org.sapia.corus.taskmanager.core.ProgressQueueTaskLog;
import org.sapia.corus.taskmanager.core.TaskExecutionContext;
import org.sapia.corus.taskmanager.core.Task;

public class StartBootConfigsTask extends Task{

  private StartupLock lock;
  
  public StartBootConfigsTask(StartupLock lock) {
    super.setMaxExecution(1);
    this.lock = lock;
  }
  
  @Override
  public Object execute(TaskExecutionContext ctx) throws Throwable {
    ProcessRepository processes = ctx.getServerContext().getServices().getProcesses();
    ExecConfigDatabase execConfigs = ctx.getServerContext().getServices().getExecConfigs();
    Deployer deployer = ctx.getServerContext().getServices().lookup(Deployer.class);
    Processor processor = ctx.getServerContext().getServices().lookup(Processor.class);
    
    List<ExecConfig> configsToStart = execConfigs.getBootstrapConfigs();
    
    ProcessDependencyFilter filter = new ProcessDependencyFilter(new ProgressQueueTaskLog(this, ctx.getLog()));
    
    for(ExecConfig ec:configsToStart){
      for(ProcessDef pd:ec.getProcesses()){
        CommandArg distName = new StringCommandArg(pd.getDist());
        CommandArg version = new StringCommandArg(pd.getVersion());
        CommandArg processName = new StringCommandArg(pd.getProcess());
        
        List<Process> activeProcesses = processes.getActiveProcesses().getProcesses(
            distName, 
            version,
            ec.getProfile(),
            processName);
       
        // if no process exists for the given dist-version-process...
        if(activeProcesses.size() == 0){ 
          Distribution dist = null;
          try{
              dist = deployer.getDistribution(distName, version);
          }catch(LogicException e){
            ctx.warn("No distribution found for " + pd);
          }
          if(dist != null){
            for(ProcessConfig conf: dist.getProcesses(processName)){
              String profile = pd.getProfile() == null ? ec.getProfile() : pd.getProfile();
              if(conf.containsProfile(profile)){
                filter.addRootProcess(dist, conf, pd.getProfile());
              }
              else{
                ctx.warn("No profile " + profile + " found for " + pd.getProfile());
                ctx.warn("Got profiles " + conf.getProfiles());
              }
            }
          }
          else{
            ctx.warn("No distribution found for " + pd);
          }
        }
        else{
          ctx.warn("Process already started for: " + pd);
        }
      }
    }
    
    filter.filterDependencies(deployer, processor);

    List<ProcessRef> filteredProcesses = filter.getFilteredProcesses();
    MultiExecTask exec = new MultiExecTask(lock, filteredProcesses);
    ctx.getTaskManager().executeBackground(
        0, 
        processor.getConfiguration().getExecIntervalMillis(), 
        exec);
    return null;
  }
}
