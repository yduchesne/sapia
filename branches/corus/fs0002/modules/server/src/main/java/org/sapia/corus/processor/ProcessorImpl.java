package org.sapia.corus.processor;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.sapia.corus.client.annotations.Bind;
import org.sapia.corus.client.common.Arg;
import org.sapia.corus.client.common.ProgressQueue;
import org.sapia.corus.client.common.ProgressQueueImpl;
import org.sapia.corus.client.exceptions.deployer.DistributionNotFoundException;
import org.sapia.corus.client.exceptions.misc.MissingDataException;
import org.sapia.corus.client.exceptions.processor.ProcessNotFoundException;
import org.sapia.corus.client.exceptions.processor.TooManyProcessInstanceException;
import org.sapia.corus.client.services.db.DbModule;
import org.sapia.corus.client.services.deployer.Deployer;
import org.sapia.corus.client.services.deployer.DistributionCriteria;
import org.sapia.corus.client.services.deployer.dist.Distribution;
import org.sapia.corus.client.services.deployer.dist.ProcessConfig;
import org.sapia.corus.client.services.deployer.event.UndeploymentEvent;
import org.sapia.corus.client.services.event.EventDispatcher;
import org.sapia.corus.client.services.http.HttpModule;
import org.sapia.corus.client.services.processor.ExecConfig;
import org.sapia.corus.client.services.processor.ProcStatus;
import org.sapia.corus.client.services.processor.Process;
import org.sapia.corus.client.services.processor.ProcessCriteria;
import org.sapia.corus.client.services.processor.Processor;
import org.sapia.corus.client.services.processor.ProcessorConfiguration;
import org.sapia.corus.client.services.processor.Process.ProcessTerminationRequestor;
import org.sapia.corus.core.ModuleHelper;
import org.sapia.corus.db.CachingDbMap;
import org.sapia.corus.deployer.DistributionDatabase;
import org.sapia.corus.interop.Status;
import org.sapia.corus.processor.task.BootstrapExecConfigStartTask;
import org.sapia.corus.processor.task.EndUserExecConfigStartTask;
import org.sapia.corus.processor.task.KillTask;
import org.sapia.corus.processor.task.MultiExecTask;
import org.sapia.corus.processor.task.ProcessCheckTask;
import org.sapia.corus.processor.task.RestartTask;
import org.sapia.corus.processor.task.ResumeTask;
import org.sapia.corus.processor.task.SuspendTask;
import org.sapia.corus.taskmanager.core.BackgroundTaskConfig;
import org.sapia.corus.taskmanager.core.TaskConfig;
import org.sapia.corus.taskmanager.core.TaskLogProgressQueue;
import org.sapia.corus.taskmanager.core.TaskManager;
import org.sapia.corus.taskmanager.core.TaskParams;
import org.sapia.corus.taskmanager.core.ThrottleFactory;
import org.sapia.corus.taskmanager.core.TimeIntervalThrottle;
import org.sapia.ubik.rmi.Remote;
import org.sapia.ubik.rmi.interceptor.Interceptor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implements the <code>Processor</code> interface.
 *
 * @author Yanick Duchesne
 */
@Bind(moduleInterface=Processor.class)
@Remote(interfaces=Processor.class)
public class ProcessorImpl extends ModuleHelper implements Processor {
    
  @Autowired
  private ProcessorConfiguration _configuration;
  
  @Autowired
  DbModule _db;
  
  @Autowired
  private Deployer _deployer;
  
  @Autowired
  private TaskManager _taskman;
  
  @Autowired
  private EventDispatcher _events;

  @Autowired
  private HttpModule _http;
  
  
  private ProcessRepository      _processes;
  private ExecConfigDatabaseImpl _execConfigs;
  private StartupLock            _startLock;  
  
  public ProcessorConfiguration getConfiguration() {
    return _configuration;
  }

  public void init() throws Exception {

    _startLock = new StartupLock(_configuration.getStartIntervalMillis());
    
    services().getTaskManager().registerThrottle(
        ProcessorThrottleKeys.PROCESS_EXEC, 
        ThrottleFactory.createTimeIntervalThrottle(
            TimeUnit.MILLISECONDS, 
            _configuration.getExecIntervalMillis()
        )
    );    
        
    //// intializing process repository
    _execConfigs = new ExecConfigDatabaseImpl(_db.getDbMap(String.class, ExecConfig.class, "processor.execConfigs"));
    services().bind(ExecConfigDatabase.class, _execConfigs);
    
    ProcessDatabase suspended = new ProcessDatabaseImpl(
        new CachingDbMap<String, Process>(_db.getDbMap(String.class, Process.class, "processor.suspended"))
    );
    
    ProcessDatabase active = new ProcessDatabaseImpl(
        new CachingDbMap<String, Process>(_db.getDbMap(String.class, Process.class, "processor.active"))
    );
    
    ProcessDatabase toRestart = new ProcessDatabaseImpl(
        new CachingDbMap<String, Process>(_db.getDbMap(String.class, Process.class, "processor.toRestart"))
    );

    _processes = new ProcessRepositoryImpl(suspended, active, toRestart);
    services().bind(ProcessRepository.class, _processes);

    // here we "touch" the process objects to prevent their automatic 
    // termination (the Corus server might have been down for a period
    // of time that is longer then some process' tolerated idle delay).                                                                    
    List<Process>    processes = (List<Process>) active.getProcesses(ProcessCriteria.builder().all());
    Process proc;

    for (int i = 0; i < processes.size(); i++) {
      proc = (Process) processes.get(i);
      proc.touch();
    }
  }
  
  public void start() throws Exception {
    ProcessorExtension ext = new ProcessorExtension(this, serverContext());
    _http.addHttpExtension(ext);
    
    BootstrapExecConfigStartTask boot = new BootstrapExecConfigStartTask(_startLock);
    
    _taskman.executeBackground(
        boot,
        null,
        BackgroundTaskConfig.create()
          .setExecDelay(_configuration.getBootExecDelayMillis())
          .setExecInterval(_configuration.getExecIntervalMillis()));
    
    ProcessCheckTask check = new ProcessCheckTask();
    _taskman.executeBackground(
        check, 
        null,
        BackgroundTaskConfig.create()
          .setExecDelay(0)
          .setExecInterval(_configuration.getProcessCheckIntervalMillis()));

    _events.addInterceptor(UndeploymentEvent.class, new ProcessorInterceptor());
  }

  public void dispose() {
  }

  /*////////////////////////////////////////////////////////////////////
                         Module INTERFACE METHODS
  ////////////////////////////////////////////////////////////////////*/

  /**
   * @see org.sapia.corus.client.Module#getRoleName()
   */
  public String getRoleName() {
    return Processor.ROLE;
  }

  /*////////////////////////////////////////////////////////////////////
                       Processor INTERFACE METHODS
  ////////////////////////////////////////////////////////////////////*/
  
  public ProgressQueue exec(String execConfigName) {
    ProgressQueue progress = new ProgressQueueImpl();
    EndUserExecConfigStartTask start = new EndUserExecConfigStartTask(execConfigName, _startLock);
    try{
      _taskman.executeAndWait(
          start, 
          null,
          TaskConfig.create(new TaskLogProgressQueue(progress))
      ).get();
    }catch(InvocationTargetException e){
      // noop
    }catch(InterruptedException e){
      progress.error(e);
      progress.close();
    }

    return progress;
    
  }
  
  @Override
  public ProgressQueue exec(ProcessCriteria criteria, int instances)
      throws TooManyProcessInstanceException {
    try {
      List<Distribution> dists = _deployer.getDistributions(criteria.getDistributionCriteria());
      if (dists.size() == 0) {
        throw new DistributionNotFoundException(
            String.format(
                "No distribution for %s, %s", 
                criteria.getDistribution(), 
                criteria.getVersion())
          );
      }
      ProgressQueueImpl q = new ProgressQueueImpl();      
      ProcessDependencyFilter filter = new ProcessDependencyFilter(q);
      for(int i = 0; i < dists.size(); i++){
        Distribution dist = dists.get(i);
        List<ProcessConfig> configs;
        if(criteria.getName() == null){
          configs = new ArrayList<ProcessConfig>();
          List<ProcessConfig> temp = dist.getProcesses();
          for(ProcessConfig pc: temp){
            if(!pc.isInvoke()){
              configs.add(pc);
            }
          }
        }
        else{
          configs = dist.getProcesses(criteria.getName());
        }
        if(configs.size() == 0){
          q.warning(String.format("Could not find any process to start for: %s", criteria));
        }
        else{
          for(ProcessConfig config:configs){
            int activeCount = _processes.getActiveProcessCountFor(criteria);
            int totalCount = activeCount + instances;
            if(config.getMaxInstances() > 0 && (totalCount > config.getMaxInstances())){
              throw new TooManyProcessInstanceException(
                "Too many process instances for : " + config.getName() + "(" + dist.getName() + " " + dist.getVersion() + ")" +
                ", requested: " + instances + ", currently active: " + activeCount + ", maximum permitted: " + config.getMaxInstances());
            }
            filter.addRootProcess(dist, config, criteria.getProfile(), instances);
          }
        }
      }
      List<ProcessRef> toStart = new ArrayList<ProcessRef>();
      filter.filterDependencies(_deployer, this);
      for(ProcessRef fp:filter.getFilteredProcesses()){
        q.info("Scheduling execution of process: " + fp);
        ProcessRef copy = fp.getCopy();
        toStart.add(copy);
      }
      
      MultiExecTask  exec = new MultiExecTask(_startLock, toStart);
      _taskman.executeBackground(
          exec,
          null,
          BackgroundTaskConfig.create()
            .setExecDelay(0)
            .setExecInterval(_configuration.getExecIntervalMillis()));
      
      q.close();
      return q;
    } catch (Exception e) {
      ProgressQueue q = new ProgressQueueImpl();
      q.error(e);
      q.close();

      return q;
    }
  }
  
  
  public void addExecConfig(ExecConfig conf){
    if(conf.getName() == null) {
      throw new MissingDataException("Execution configuration must have a name");
    }
    this._execConfigs.addConfig(conf);
  }
  
  public List<ExecConfig> getExecConfigs() {
    return _execConfigs.getConfigs();
  }
  
  public void removeExecConfig(Arg name) {
    _execConfigs.removeConfigsFor(name);
  }
  
  @Override
  public void kill(ProcessCriteria criteria, boolean suspend) {
    List<Process> procs = _processes.getActiveProcesses().getProcesses(criteria);
    KillTask kill;
    Process proc;


    for (int i = 0; i < procs.size(); i++) {
      proc = (Process) procs.get(i);

      if (suspend) {
        SuspendTask susp = new SuspendTask(proc.getMaxKillRetry());
        
        _taskman.executeBackground(
            susp,
            TaskParams.createFor(proc, ProcessTerminationRequestor.KILL_REQUESTOR_ADMIN),
            BackgroundTaskConfig.create()
              .setExecDelay(0)
              .setExecInterval(_configuration.getKillIntervalMillis())
        );
      } else {
        kill = new KillTask(proc.getMaxKillRetry());
        _taskman.executeBackground(kill,
            TaskParams.createFor(proc, ProcessTerminationRequestor.KILL_REQUESTOR_ADMIN),
            BackgroundTaskConfig.create()
              .setExecDelay(0)
              .setExecInterval(_configuration.getKillIntervalMillis())
        );
      }
    }
  }

  @Override
  public void kill(String corusPid, boolean suspend)  throws ProcessNotFoundException{

      KillTask               kill;
      Process                proc = _processes.getActiveProcesses().getProcess(corusPid);

      if (suspend) {
        SuspendTask susp = new SuspendTask(proc.getMaxKillRetry());
        _taskman.executeBackground(
            susp,
            TaskParams.createFor(proc, ProcessTerminationRequestor.KILL_REQUESTOR_ADMIN),
            BackgroundTaskConfig.create()
              .setExecDelay(0)
              .setExecInterval(_configuration.getKillIntervalMillis())
        );
      } else {
        kill   = new KillTask(proc.getMaxKillRetry());
        _taskman.executeBackground(
            kill,
            TaskParams.createFor(proc, ProcessTerminationRequestor.KILL_REQUESTOR_ADMIN),
            BackgroundTaskConfig.create()
              .setExecDelay(0)
              .setExecInterval(_configuration.getKillIntervalMillis())
        );
      }
  }

  @Override
  public void restartByAdmin(String pid) throws ProcessNotFoundException{
    doRestart(pid, ProcessTerminationRequestor.KILL_REQUESTOR_ADMIN);
  }

  @Override
  public void restart(String pid) throws ProcessNotFoundException{
    doRestart(pid, ProcessTerminationRequestor.KILL_REQUESTOR_PROCESS);
  }
  
  @Override
  public void restart(ProcessCriteria criteria) {
    List<Process> procs = _processes.getActiveProcesses().getProcesses(criteria);

    for (int i = 0; i < procs.size(); i++) {
      Process proc = (Process) procs.get(i);
      RestartTask restart = new RestartTask(proc.getMaxKillRetry());
      _taskman.executeBackground(
          restart,
          TaskParams.createFor(proc, ProcessTerminationRequestor.KILL_REQUESTOR_ADMIN),
          BackgroundTaskConfig.create()
            .setExecDelay(0)
            .setExecInterval(_configuration.getKillIntervalMillis())
      );
    }
  }
  
  private void doRestart(String pid, ProcessTerminationRequestor origin) throws ProcessNotFoundException{
    Process     proc    = _processes.getActiveProcesses().getProcess(pid);
    RestartTask restart = new RestartTask(proc.getMaxKillRetry());

    _taskman.executeBackground(
        restart,
        TaskParams.createFor(proc, origin),
        BackgroundTaskConfig.create()
          .setExecDelay(0)
          .setExecInterval(_configuration.getKillIntervalMillis()));
  }

  @Override
  public ProgressQueue resume() {
    Iterator<Process>    procs  = _processes.getSuspendedProcesses()
      .getProcesses(ProcessCriteria.builder().all()).iterator();
    ResumeTask  resume;
    Process     proc;

    DistributionDatabase store = lookup(DistributionDatabase.class);

    Distribution  dist;
    ProcessConfig conf;
    int           restartCount = 0;

    while (procs.hasNext()) {
      proc = (Process) procs.next();
      DistributionCriteria criteria = DistributionCriteria.builder()
        .name(proc.getDistributionInfo().getName())
        .version(proc.getDistributionInfo().getVersion()).build();
      try{
            dist = store.getDistribution(criteria);
      } catch (DistributionNotFoundException e) {
        store.removeDistribution(criteria);
        continue;
      }

      conf = dist.getProcess(proc.getDistributionInfo().getProcessName());

      if (conf == null) {
        ProgressQueue q = new ProgressQueueImpl();
        q.error("No process named '" +
          proc.getDistributionInfo().getProcessName() + "'");
        q.close();

        return q;
      }

      proc.touch();
      proc.save();

      resume = new ResumeTask();
      _taskman.execute(resume, TaskParams.createFor(proc, dist, conf));
      restartCount++;
    }

    ProgressQueue q = new ProgressQueueImpl();

    if (restartCount == 0) {
      q.warning("No suspended processes to restart.");
      q.close();
    } else {
      q.warning("Restarted " + restartCount + " suspended processes.");
      q.close();
    }

    return q;
  }

  @Override
  public Process getProcess(String corusPid) throws ProcessNotFoundException {
    return _processes.getActiveProcesses().getProcess(corusPid);
  }

  @Override
  public List<Process> getProcesses(ProcessCriteria criteria) {
    return _processes.getProcesses(criteria);
  }
  
  @Override
  public List<Process> getProcessesWithPorts() {
    List<Process> toReturn = new ArrayList<Process>();
    List<Process> processes = _processes.getProcesses();
    for(int i = 0; i < processes.size(); i++){
      Process p = processes.get(i);
      if(p.getActivePorts().size() > 0){
        toReturn.add(p);
      }
    }
    return toReturn;
  }  

  @Override
  public List<Status> getStatus(ProcessCriteria criteria) {
    return copyStatus(getProcesses(criteria));
  }

  @Override
  public ProcStatus getStatusFor(String corusPid) throws ProcessNotFoundException {
    Process proc = getProcess(corusPid);

    return copyStatus(proc);
  }

  private List<Status> copyStatus(List<Process> processes) {
    List<Status>   stat   = new ArrayList<Status>(processes.size());
    for (Process p:processes) {
      if(_logger.isDebugEnabled()){
        _logger.debug(String.format("Returning status %s", p.getProcessStatus()));
      }
      stat.add(copyStatus(p));
    }

    return stat;
  }
  
  private ProcStatus copyStatus(Process p) {
    return new ProcStatus(p);
  }
  
  public class ProcessorInterceptor implements Interceptor{
   
    public void onUndeploymentEvent(UndeploymentEvent evt){
      _execConfigs.removeProcessesForDistribution(evt.getDistribution());
    }
  }
}
