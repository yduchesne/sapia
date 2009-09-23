package org.sapia.corus.processor;

import org.sapia.corus.CorusException;
import org.sapia.corus.CorusRuntime;
import org.sapia.corus.LogicException;
import org.sapia.corus.ModuleHelper;
import org.sapia.corus.admin.CommandArg;
import org.sapia.corus.admin.CommandArgParser;
import org.sapia.corus.admin.StringCommandArg;
import org.sapia.corus.db.DbModule;
import org.sapia.corus.deployer.Deployer;
import org.sapia.corus.deployer.DeployerImpl;
import org.sapia.corus.deployer.DistributionStore;
import org.sapia.corus.deployer.config.Distribution;
import org.sapia.corus.deployer.config.ProcessConfig;
import org.sapia.corus.http.HttpModule;
import org.sapia.corus.interop.Status;
import org.sapia.corus.port.PortManager;
import org.sapia.corus.processor.task.*;
import org.sapia.corus.taskmanager.TaskManager;
import org.sapia.corus.util.ProgressQueue;
import org.sapia.corus.util.ProgressQueueImpl;

import org.sapia.taskman.PeriodicTaskDescriptor;
import org.sapia.taskman.TaskDescriptor;

import org.sapia.ubik.net.TCPAddress;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Implements the <code>Processor</code> interface.
 *
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class ProcessorImpl extends ModuleHelper implements Processor {
  /**
   * This constant specifies the default "process timeout" - delay (in seconds)
   * after which a process is considered idled and must be restarted.
   */
  public static final int DEFAULT_PROCESS_TIMEOUT = 25;

  /**
   * This constant specifies the default interval (in seconds) at which
   * process status is checked.
   */
  public static final int DEFAULT_CHECK_INTERVAL = 15;

  /**
   * This constant specifies the default interval (in seconds) at which
   * kill attempts occur.
   */
  public static final int DEFAULT_KILL_INTERVAL = 15;
  
  /**
   * This constant specifies the amount of time (in seconds) to wait
   * between process startups.
   */
  public static final int DEFAULT_START_INTERVAL = 15;  

  /**
   * This constant specifies the minimum amount of time (in
   * seconds) required between two startups for the second one
   * to be authorized; value is 120 (seconds).
   */
  public static final int DEFAULT_RESTART_INTERVAL = 120;
  
  
  public static final long EXEC_TASK_INTERVAL = 10000;
  
  
  static ProcessorImpl    instance;
  
  private ProcessDB       _db;
  private PortManager     _ports;
  private ExecConfigStore _execConfigs;
  private int             _procTimeout     = DEFAULT_PROCESS_TIMEOUT;
  private int             _checkInterval   = DEFAULT_CHECK_INTERVAL;
  private int             _killInterval    = DEFAULT_KILL_INTERVAL;
  private int             _restartInterval = DEFAULT_RESTART_INTERVAL;
  private int             _httpPort;
  private StartupLock     _startLock       = new StartupLock(DEFAULT_START_INTERVAL);  

  /**
   * @param seconds the delay after which processes are considered in timeout.
   */
  public void setProcessTimeout(int seconds) {
    _procTimeout = seconds;
  }

  /**
   * @param interval the interval (in seconds) at which processes are checked for timeout.
   */
  public void setCheckInterval(int interval) {
    _checkInterval = interval;
  }

  /**
   * @param interval the interval (in seconds) at which kill attempts are performed.
   */
  public void setKillInterval(int interval) {
    _killInterval = interval;
  }

  /**
   * @param interval the interval (in seconds) to wait for between process startups.
   */
  public void setStartInterval(int interval) {
    _startLock.setInterval(interval*1000);
  }  

  /**
   * @param interval the interval (in seconds) within which the last restart attempt must have
   * occurred for the next one to be triggered.
   */
  public void setRestartInterval(int interval) {
    _restartInterval = interval;
  }
  
  public void init() throws Exception {
    instance   = this;

    _httpPort = ((TCPAddress) CorusRuntime.getTransport().getServerAddress()).getPort();
    
    DbModule db = (DbModule) CorusRuntime.getCorus().lookup(DbModule.ROLE);
    _execConfigs = new ExecConfigStore(db.getDbMap("processor.execConfigs"));

    ProcessStore suspended = new ProcessStore(db.getDbMap("processor.suspended"));
    ProcessStore active = new ProcessStore(db.getDbMap("processor.active"));
    ProcessStore toRestart = new ProcessStore(db.getDbMap("processor.toRestart"));

    _ports = (PortManager)CorusRuntime.getCorus().lookup(PortManager.ROLE);

    // here we "touch" the process objects to prevent their automatic 
    // termination (the Corus server might have been down for a period
    // of time that is longer then some process' tolerated idle delay).                                                                    
    List<Process>    processes = (List<Process>) active.getProcesses();
    Process proc;

    for (int i = 0; i < processes.size(); i++) {
      proc = (Process) processes.get(i);
      proc.touch();
    }
 
    _db = new ProcessDB(suspended, active, toRestart);

    TaskManager      tm       = (TaskManager) env().lookup(TaskManager.ROLE);
    Deployer         deployer = (Deployer) env().lookup(Deployer.ROLE);
    TCPAddress       address = (TCPAddress) CorusRuntime.getTransport().getServerAddress();
    
    StartBootConfigsTask boot = new StartBootConfigsTask(
        address,
        _httpPort,
        _ports,
        tm,
        deployer,
        this,
        _execConfigs,
        _db,
        _startLock
    );
    PeriodicTaskDescriptor ptd = new PeriodicTaskDescriptor("StartBootConfigsTask",
        35000, boot);
    tm.execTaskFor(ptd);
    
    ProcessCheckTask check = new ProcessCheckTask(
        address,
        _httpPort, 
        _db, 
        _killInterval * 1000, _procTimeout * 1000,
        _restartInterval * 1000, 
        _ports);
    ptd = new PeriodicTaskDescriptor("ProcessCheckTask",
        _checkInterval * 1000, check);
    tm.execTaskFor(ptd);
    
  }
  
  public void start() throws Exception {
    HttpModule module = (HttpModule)CorusRuntime.getCorus().lookup(HttpModule.ROLE);
    ProcessorExtension ext = new ProcessorExtension(this);
    module.addHttpExtension(ext);
  }

  /**
   * @see org.sapia.soto.Service#dispose()
   */
  public void dispose() {
  }

  /*////////////////////////////////////////////////////////////////////
                         Module INTERFACE METHODS
  ////////////////////////////////////////////////////////////////////*/

  /**
   * @see org.sapia.corus.Module#getRoleName()
   */
  public String getRoleName() {
    return Processor.ROLE;
  }

  /*////////////////////////////////////////////////////////////////////
                       Processor INTERFACE METHODS
  ////////////////////////////////////////////////////////////////////*/
  
  public ProgressQueue exec(String execConfigName) {
    ProgressQueue progress = new ProgressQueueImpl();
    ProcessDependencyFilter filter = new ProcessDependencyFilter(progress);
    Deployer     deployer = null;
    TaskManager  taskman = null;
    try{
      deployer = (Deployer) CorusRuntime.getCorus().lookup(Deployer.ROLE);
      taskman  = ((TaskManager) CorusRuntime.getCorus().lookup(TaskManager.ROLE)); 
    }catch(CorusException  e){
      progress.error(e);
      progress.close();
      return progress;
    }
    ExecConfig conf = this._execConfigs.getConfigFor(execConfigName);
    if(conf == null){
      progress.error("No execution configuration for: " + execConfigName);
      progress.close();
      return progress;
    }
    else{
      progress.error("Executing process configuration: " + execConfigName);
      for(ProcessDef def:conf.getProcesses()){
        List<Distribution> dists = deployer.getDistributions(
            new StringCommandArg(def.getDist()), 
            new StringCommandArg(def.getVersion()));
        if (dists.size() == 0) {
          progress.warning("No distribution for " + def.getDist() + ", " +
            def.getVersion());
        }
        for(Distribution dist:dists){
          List<ProcessConfig> configs;
          if(def.getProcess() == null){
            configs = dist.getProcesses();
          }
          else{
            configs = dist.getProcesses(new StringCommandArg(def.getProcess()));
          }
          if(configs.size() == 0){
            progress.warning("Could not find any process to start for: " + 
                def.getDist() + ", " + 
                def.getVersion() + ", " + def.getProcess());
          }
          else{
            for(ProcessConfig config:configs){
              filter.addRootProcess(dist, config, def.getProfile());
            }
          }
        }
        
      }
      filter.filterDependencies(deployer, this);
      for(ProcessRef processRef:filter.getFilteredProcesses()){
        progress.info("Scheduling process execution for distribution: " + processRef.getDist().getName() + ", " + processRef.getDist().getVersion());
      }
      TCPAddress    addr = (TCPAddress) CorusRuntime.getTransport().getServerAddress(); 
      MultiExecTask  exec = new MultiExecTask(addr, _httpPort, _db, _startLock, filter.getFilteredProcesses(), _ports, taskman, 1);
      PeriodicTaskDescriptor ptd = new PeriodicTaskDescriptor("MultiExecProcessTask", EXEC_TASK_INTERVAL, exec);            
      taskman.execTaskFor(ptd);
    }
    progress.close();
    return progress;
    
  }

  public ProgressQueue exec(CommandArg distName, CommandArg version, String profile,
    CommandArg processName, int instances) {
    try {
      Deployer     deployer = (Deployer) CorusRuntime.getCorus().lookup(Deployer.ROLE);
      List<Distribution> dists = deployer.getDistributions(distName, version);
      if (dists.size() == 0) {
        throw new LogicException("No distribution for " + distName + ", " +
          version);
      }
      ProgressQueueImpl q = new ProgressQueueImpl();      
      ProcessDependencyFilter filter = new ProcessDependencyFilter(q);
      for(int i = 0; i < dists.size(); i++){
        Distribution dist = dists.get(i);
        List<ProcessConfig> configs;
        if(processName == null){
          configs = dist.getProcesses();
        }
        else{
          configs = dist.getProcesses(processName);
        }
        if(configs.size() == 0){
          q.warning("Could not find any process to start for: " + distName + ", " + version
              + ", " + processName);
        }
        else{
          for(ProcessConfig config:configs){
            filter.addRootProcess(dist, config, profile);
          }
        }
      }
      filter.filterDependencies(deployer, this);
      for(ProcessRef processRef:filter.getFilteredProcesses()){
        q.info("Scheduling process execution for distribution: " + processRef.getDist().getName() + ", " + processRef.getDist().getVersion());
      }
      TCPAddress    addr = (TCPAddress) CorusRuntime.getTransport().getServerAddress(); 
      TaskManager taskman = ((TaskManager) env().lookup(TaskManager.ROLE)); 
      MultiExecTask  exec = new MultiExecTask(addr, _httpPort, _db, _startLock, filter.getFilteredProcesses(), _ports, taskman, instances);
      PeriodicTaskDescriptor ptd = new PeriodicTaskDescriptor("MultiExecProcessTask", EXEC_TASK_INTERVAL, exec);            
      taskman.execTaskFor(ptd);
      
      q.close();
      return q;
    } catch (Exception e) {
      ProgressQueue q = new ProgressQueueImpl();
      q.error(e);
      q.close();

      return q;
    }
  }

  public ProgressQueue exec(CommandArg distName, CommandArg version, String profile,
    int instances) {
    return exec(distName, version, profile, null, instances);
  }
  
  public void addExecConfig(ExecConfig conf) throws LogicException{
    if(conf.getName() == null) {
      throw new LogicException("Execution configuration must have a name");
    }
    this._execConfigs.addConfig(conf);
  }
  
  public List<ExecConfig> getExecConfigs() {
    return _execConfigs.getConfigs();
  }
  
  public void removeExecConfig(CommandArg name) {
    _execConfigs.removeConfigsFor(name);
  }

  public void kill(CommandArg distName, CommandArg version, String profile,
      CommandArg processName, boolean suspend) throws CorusException {
    List<Process> procs = _db.getActiveProcesses().getProcesses(distName, version,
        profile, processName);
    TaskManager tm;
    KillTask kill;
    PeriodicTaskDescriptor ptd;
    Process proc;
    TCPAddress addr = (TCPAddress) CorusRuntime.getTransport().getServerAddress();

    try {
      tm = (TaskManager) env().lookup(TaskManager.ROLE);
    } catch (Exception e) {
      throw new CorusException(e);
    }

    for (int i = 0; i < procs.size(); i++) {
      proc = (Process) procs.get(i);

      if (suspend) {
        SuspendTask susp = new SuspendTask(addr, _httpPort,
            Process.KILL_REQUESTOR_ADMIN, proc.getProcessID(), _db,
            proc.getMaxKillRetry(), _ports);
        ptd = new PeriodicTaskDescriptor("SuspendProcessTask",
            _killInterval * 1000, susp);
        tm.execTaskFor(ptd);
      } else {
        kill   = new KillTask(addr, _httpPort, Process.KILL_REQUESTOR_ADMIN,
            proc.getProcessID(), _db, proc.getMaxKillRetry(),
            _restartInterval * 1000, _ports);
        ptd = new PeriodicTaskDescriptor("KillProcessTask",
            _killInterval * 1000, kill);
        tm.execTaskFor(ptd);
      }
    }
  }

  public void kill(CommandArg distName, CommandArg version, 
      boolean suspend) throws CorusException {
    kill(distName, version, null, suspend);
  }
  public void kill(CommandArg distName, CommandArg version, String profile,
    boolean suspend) throws CorusException {
    List<Process>          procs = _db.getActiveProcesses().getProcesses(distName,
        version, profile);
    TaskManager            tm;
    KillTask               kill;
    PeriodicTaskDescriptor ptd;
    Process                proc;
    TCPAddress             addr = (TCPAddress) CorusRuntime.getTransport()
                                                           .getServerAddress();

    try {
      tm = (TaskManager) env().lookup(TaskManager.ROLE);
    } catch (Exception e) {
      throw new CorusException(e);
    }

    for (int i = 0; i < procs.size(); i++) {
      proc = (Process) procs.get(i);

      if (suspend) {
        SuspendTask susp = new SuspendTask(addr, _httpPort,
            Process.KILL_REQUESTOR_ADMIN, proc.getProcessID(), _db,
            proc.getMaxKillRetry(), _ports);
        ptd = new PeriodicTaskDescriptor("SuspendProcessTask",
            _killInterval * 1000, susp);
        tm.execTaskFor(ptd);
      } else {
        kill   = new KillTask(addr, _httpPort, Process.KILL_REQUESTOR_ADMIN,
            proc.getProcessID(), _db, proc.getMaxKillRetry(),
            _restartInterval * 1000, _ports);
        ptd    = new PeriodicTaskDescriptor("KillProcessTask",
            _killInterval * 1000, kill);
        tm.execTaskFor(ptd);
      }
    }
  }

  public void kill(String corusPid, boolean suspend) throws CorusException {
    try {
      TCPAddress             addr = (TCPAddress) CorusRuntime.getTransport()
                                                             .getServerAddress();

      KillTask               kill;
      PeriodicTaskDescriptor ptd;
      Process                proc = _db.getActiveProcesses().getProcess(corusPid);

      if (suspend) {
        SuspendTask susp = new SuspendTask(addr, _httpPort,
            Process.KILL_REQUESTOR_ADMIN, corusPid, _db, proc.getMaxKillRetry(), _ports);
        ptd = new PeriodicTaskDescriptor("SuspendProcessTask",
            _killInterval * 1000, susp);
        ((TaskManager) env().lookup(TaskManager.ROLE)).execTaskFor(ptd);
      } else {
        kill   = new KillTask(addr, _httpPort, Process.KILL_REQUESTOR_ADMIN,
            corusPid, _db, proc.getMaxKillRetry(), _restartInterval * 1000, _ports);
        ptd    = new PeriodicTaskDescriptor("KillProcessTask",
            _killInterval * 1000, kill);
        ((TaskManager) env().lookup(TaskManager.ROLE)).execTaskFor(ptd);
      }
    } catch (Exception e) {
      throw new CorusException(e);
    }
  }

  public void restartByAdmin(String pid) throws CorusException {
    doRestart(pid, Process.KILL_REQUESTOR_ADMIN);
  }

  public void restart(String pid) throws CorusException {
    doRestart(pid, Process.KILL_REQUESTOR_PROCESS);
  }
  
  private void doRestart(String dynId, String origin) throws CorusException {
    try {
      Process     proc    = _db.getActiveProcesses().getProcess(dynId);
      RestartTask restart = new RestartTask((TCPAddress) CorusRuntime.getTransport()
                                                                     .getServerAddress(),
          _httpPort, origin, _db,
          ((DeployerImpl) CorusRuntime.getCorus().lookup(Deployer.ROLE)).getDistributionStore(),
          dynId, proc.getMaxKillRetry(), _ports);

      PeriodicTaskDescriptor ptd = new PeriodicTaskDescriptor("RestartProcessTask",
          _killInterval * 1000, restart);
      ((TaskManager) env().lookup(TaskManager.ROLE)).execTaskFor(ptd);
      
    } catch (CorusException e) {
      throw e;
    } catch (Exception e) {
      throw new CorusException(e);
    }
  }

  public ProgressQueue resume() {
    Iterator<Process>    procs  = _db.getSuspendedProcesses().getProcesses().iterator();
    TaskManager tm;
    ResumeTask  resume;
    Process     proc;

    try {
      tm = (TaskManager) env().lookup(TaskManager.ROLE);
    } catch (Exception e) {
      ProgressQueue q = new ProgressQueueImpl();
      q.error(e);
      q.close();

      return q;
    }

    DistributionStore store = null;

    try {
      store = ((DeployerImpl) CorusRuntime.getCorus().lookup(Deployer.ROLE)).getDistributionStore();
    } catch (CorusException e) {
      ProgressQueue q = new ProgressQueueImpl();
      q.error(e);
      q.close();

      return q;
    }

    Distribution  dist;
    ProcessConfig conf;
    TCPAddress    addr         = (TCPAddress) CorusRuntime.getTransport()
                                                          .getServerAddress();
    int           restartCount = 0;

    while (procs.hasNext()) {
      proc = (Process) procs.next();
      CommandArg nameArg = CommandArgParser.exact(proc.getDistributionInfo().getName());
      CommandArg versionArg = CommandArgParser.exact(proc.getDistributionInfo().getVersion());      
      try {
 
        dist = store.getDistribution(nameArg, versionArg);
      } catch (LogicException e) {
        store.removeDistribution(nameArg, versionArg);
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

      try {
        resume = new ResumeTask(addr, _httpPort, _db, proc, dist, conf, _ports);
      } catch (LockException e) {
        ProgressQueue q = new ProgressQueueImpl();
        q.error(e);
        q.close();

        return q;
      }

      tm.execAsyncTask("ResumeProcessTask", resume);
      restartCount++;
    }

    ProgressQueue q = new ProgressQueueImpl();

    if (restartCount == 0) {
      q.warning("No suspended VMs to restart.");
      q.close();
    } else {
      q.warning("Restarted " + restartCount + " suspended VMs.");
      q.close();
    }

    return q;
  }

  public Process getProcess(String corusPid) throws LogicException {
    return _db.getActiveProcesses().getProcess(corusPid);
  }

  public List<Process> getProcesses() {
    return _db.getProcesses();
  }

  public List<Process> getProcesses(CommandArg distName, CommandArg version, String profile,
    CommandArg processName) {
    return _db.getProcesses(distName, version, profile, processName);
  }

  public List<Process> getProcesses(CommandArg distName, CommandArg version, String profile) {
    return _db.getProcesses(distName, version, profile);
  }
  
  public List<Process> getProcesses(CommandArg distName, CommandArg version) {
    return _db.getProcesses(distName, version);
  }

  public List<Process> getProcesses(CommandArg distName) {
    return _db.getProcesses(distName);
  }
  
  public List<Process> getProcessesWithPorts() {
    List<Process> toReturn = new ArrayList<Process>();
    List<Process> processes = _db.getProcesses();
    for(int i = 0; i < processes.size(); i++){
      Process p = processes.get(i);
      if(p.getActivePorts().size() > 0){
        toReturn.add(p);
      }
    }
    return toReturn;
  }  

  public List<Status> getStatus() {
    return copyStatus(getProcesses());
  }

  public List<Status> getStatus(CommandArg distName, CommandArg version, String profile,
    CommandArg processName) {
    return copyStatus(getProcesses(distName, version, profile, processName));
  }
  
  public List<Status> getStatus(CommandArg distName, CommandArg version, String profile) {
    return copyStatus(getProcesses(distName, version, profile));
  }

  public List<Status> getStatus(CommandArg distName, CommandArg version) {
    return copyStatus(getProcesses(distName, version));
  }

  public List<Status> getStatus(CommandArg distName) {
    return copyStatus(getProcesses(distName));
  }

  public ProcStatus getStatusFor(String corusPid) throws LogicException {
    Process proc = getProcess(corusPid);

    return copyStatus(proc);
  }

  private List<Status> copyStatus(List<Process> processes) {
    List<Status>   stat   = new ArrayList<Status>(processes.size());
    for (Process p:processes) {
      stat.add(copyStatus(p));
    }

    return stat;
  }
  
  private ProcStatus copyStatus(Process p) {
    return new ProcStatus(p);
  }
}
