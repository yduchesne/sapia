package org.sapia.corus.processor.task;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sapia.corus.admin.Arg;
import org.sapia.corus.admin.ArgFactory;
import org.sapia.corus.admin.StringArg;
import org.sapia.corus.admin.services.processor.ExecConfig;
import org.sapia.corus.admin.services.processor.Process;
import org.sapia.corus.admin.services.processor.ProcessDef;
import org.sapia.corus.admin.services.processor.ProcessorConfiguration;
import org.sapia.corus.admin.services.processor.Process.ProcessTerminationRequestor;
import org.sapia.corus.processor.StartupLock;
import org.sapia.corus.server.processor.ProcessRepository;
import org.sapia.corus.taskmanager.core.BackgroundTaskConfig;
import org.sapia.corus.taskmanager.core.BackgroundTaskListener;
import org.sapia.corus.taskmanager.core.Task;
import org.sapia.corus.taskmanager.core.TaskExecutionContext;
import org.sapia.corus.taskmanager.core.TaskManager;

public abstract class AbstractExecConfigStartTask extends Task{
  
  protected StartupLock lock;
  
  public AbstractExecConfigStartTask(StartupLock lock) {
    this.lock = lock;
  }
  
  @Override
  public Object execute(TaskExecutionContext ctx) throws Throwable {
    ProcessRepository processes = ctx.getServerContext().getServices().getProcesses();
    
    List<ExecConfig> configsToStart = getExecConfigsToStart(ctx);
    
    Set<Process> toStop = new HashSet<Process>();
    Set<ProcessDef> toStart = new HashSet<ProcessDef>();
    
    // finding processes for versions other than the one configured 
    // as part of the exec configuration
    for(ExecConfig ec:configsToStart){
      for(ProcessDef pd:ec.getProcesses()){
        Arg distName = new StringArg(pd.getDist());
        Arg version = ArgFactory.any();
        Arg processName = new StringArg(pd.getProcess());
        if(pd.getProfile() == null){
          pd.setProfile(ec.getProfile());
        }
        List<Process> activeProcesses = processes.getActiveProcesses().getProcesses(
            distName, 
            version,
            pd.getProfile(),
            processName);
        for(Process p:activeProcesses){
          if(!p.getDistributionInfo().getVersion().equals(pd.getVersion())){
            toStop.add(p);
          }
          else{
            toStart.add(pd);
          }
        }
      }
    }
    
    // no existing processes found
    if(toStop.size() == 0){
      execNewProcesses(ctx.getTaskManager(), toStart);
    }
    // existing processes found, killing
    else{
      KillListener listener = new KillListener(ctx.getTaskManager(), toStop.size(), toStart);
      for(Process p:toStop){
        ctx.warn("Found old processes; proceeding to kill");
        ProcessorConfiguration conf = ctx.getServerContext().getServices().getProcessor().getConfiguration();
        KillTask kill = new KillTask(ProcessTerminationRequestor.KILL_REQUESTOR_SERVER, p.getProcessID(), p.getMaxKillRetry());
        ctx.getTaskManager().executeBackground(
            kill, 
            BackgroundTaskConfig.create(listener).setExecDelay(0).setExecInterval(conf.getKillIntervalMillis())
        );      
      }
    }
    return null;
  }
  
  private void execNewProcesses(TaskManager tm, Set<ProcessDef> toStart){
    ExecNewProcessesTask exec = new ExecNewProcessesTask(lock, toStart);
    tm.fork(exec);
  }
  
  //////////////////// BackgroundTaskListener interface /////////////////////
  
  class KillListener implements BackgroundTaskListener{
    
    private TaskManager tasks;
    private volatile int counter;
    private Set<ProcessDef> toStart;
    
    public KillListener(TaskManager tasks, int counter, Set<ProcessDef> toStart) {
      this.tasks   = tasks;
      this.counter = counter;
      this.toStart = toStart;
    }
    public synchronized void executionAborted(Task task) {
      decrement();
    }
    public synchronized void executionFailed(Task task, Throwable err) {
      decrement();
    }
    public synchronized void executionSucceeded(Task task, Object result) {
      decrement();
    }
    public synchronized void maxExecutionReached(Task task) {
      decrement();
    }
    private void decrement(){
      counter--;            
      if(counter <= 0){
        execNewProcesses(tasks, toStart);
      }
    }
  }
  
  protected abstract List<ExecConfig> getExecConfigsToStart(TaskExecutionContext ctx) throws Exception;

}
