package org.sapia.corus.processor.task;

import java.util.List;

import org.sapia.corus.LogicException;
import org.sapia.corus.admin.CommandArg;
import org.sapia.corus.admin.StringCommandArg;
import org.sapia.corus.configurator.Configurator;
import org.sapia.corus.deployer.Deployer;
import org.sapia.corus.deployer.config.Distribution;
import org.sapia.corus.deployer.config.ProcessConfig;
import org.sapia.corus.port.PortManager;
import org.sapia.corus.processor.ExecConfig;
import org.sapia.corus.processor.ExecConfigStore;
import org.sapia.corus.processor.Process;
import org.sapia.corus.processor.ProcessDB;
import org.sapia.corus.processor.ProcessDef;
import org.sapia.corus.processor.ProcessDependencyFilter;
import org.sapia.corus.processor.ProcessRef;
import org.sapia.corus.processor.Processor;
import org.sapia.corus.processor.ProcessorImpl;
import org.sapia.corus.processor.StartupLock;
import org.sapia.corus.taskmanager.TaskManager;
import org.sapia.corus.taskmanager.TaskProgressQueue;
import org.sapia.taskman.Abortable;
import org.sapia.taskman.PeriodicTaskDescriptor;
import org.sapia.taskman.Task;
import org.sapia.taskman.TaskContext;
import org.sapia.ubik.net.TCPAddress;

public class StartBootConfigsTask implements Task, Abortable{

  
  public static final long START_DELAY = 30000;
  TCPAddress address;
  int httpPort;
  PortManager ports;
  TaskManager taskman;
  Deployer deployer;
  Processor processor;
  Configurator configurator;
  ExecConfigStore execConfigs;
  ProcessDB processes;
  StartupLock lock;
  private boolean aborted = false;
  private long startTime  = System.currentTimeMillis();
  private long startDelay = START_DELAY;
  
  public StartBootConfigsTask(
    TCPAddress address,
    int httpPort,
    PortManager ports,
    TaskManager taskman,
    Deployer deployer,
    Processor processor,
    Configurator configurator,
    ExecConfigStore execConfigs,
    ProcessDB processes,
    StartupLock lock
    ) {
    this.address = address;
    this.httpPort = httpPort;
    this.ports = ports;
    this.taskman = taskman;
    this.deployer = deployer;
    this.processor = processor;
    this.configurator = configurator;
    this.execConfigs = execConfigs;
    this.processes = processes;
    this.lock = lock;
  }
  
  public boolean isAborted() {
    return aborted;
  }
  
  public void exec(TaskContext ctx) {
    if(System.currentTimeMillis() - startTime >= startDelay){
      ctx.getTaskOutput().warning("Starting up bootstrap processes...");
      try{
        doExec(ctx);
      }finally{
        aborted = true;
      }
    }
    else{
      ctx.getTaskOutput().warning("Waiting a while for Corus startup completion...");
    }
  }
  
  private void doExec(TaskContext ctx){
    List<ExecConfig> configsToStart = execConfigs.getBootstrapConfigs();
    
    ProcessDependencyFilter filter = new ProcessDependencyFilter(new TaskProgressQueue(ctx.getTaskOutput()));
    
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
            ctx.getTaskOutput().warning("No distribution found for " + pd);
          }
          if(dist != null){
            for(ProcessConfig conf: dist.getProcesses(processName)){
              String profile = pd.getProfile() == null ? ec.getProfile() : pd.getProfile();
              if(conf.containsProfile(profile)){
                filter.addRootProcess(dist, conf, pd.getProfile());
              }
              else{
                ctx.getTaskOutput().warning("No profile " + profile + " found for " + pd.getProfile());
                ctx.getTaskOutput().warning("Got profiles " + conf.getProfiles());
              }
            }
          }
          else{
            ctx.getTaskOutput().warning("No distribution found for " + pd);
          }
        }
        else{
          ctx.getTaskOutput().warning("Process already started for: " + pd);
        }
      }
    }
    
    filter.filterDependencies(deployer, processor);

    List<ProcessRef> filteredProcesses = filter.getFilteredProcesses();
    MultiExecTask exec = new MultiExecTask( 
        address, httpPort, 
        processes, 
        lock,
        filteredProcesses, 
        ports,
        taskman,
        configurator,
        1);
    PeriodicTaskDescriptor ptd = new PeriodicTaskDescriptor("MultiExecTask", ProcessorImpl.EXEC_TASK_INTERVAL, exec);                
    ctx.execTaskFor(ptd);
    
  }
}
