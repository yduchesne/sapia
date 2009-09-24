package org.sapia.corus.processor.task;

import java.util.List;
import java.util.Set;

import org.sapia.corus.configurator.Configurator;
import org.sapia.corus.port.PortManager;
import org.sapia.corus.processor.ProcessDB;
import org.sapia.corus.processor.ProcessRef;
import org.sapia.corus.processor.StartupLock;
import org.sapia.corus.taskmanager.TaskManager;
import org.sapia.taskman.Abortable;
import org.sapia.taskman.Task;
import org.sapia.taskman.TaskContext;
import org.sapia.taskman.TaskOutput;
import org.sapia.ubik.net.TCPAddress;

public class MultiExecTask implements Task, Abortable{
  
  private TCPAddress    _dynSvr;
  private int           _httpPort;
  private List<ProcessRef>  _processRefs;
  private ProcessDB     _db;
  private StartupLock   _lock;
  private PortManager   _ports;
  private TaskManager   _taskman;
  private Configurator  _configurator;
  private int           _startedCount;
  
  public MultiExecTask(
      TCPAddress dynSvrAddress, 
      int httpPort, 
      ProcessDB db, 
      StartupLock lock,
      List<ProcessRef> processRefs,
      PortManager ports,
      TaskManager taskman,
      Configurator conf,
      int instances) {
    _dynSvr    = dynSvrAddress;
    _httpPort  = httpPort;
    _db        = db;
    _processRefs = processRefs;
    _ports     = ports;
    _lock      = lock;
    _taskman   = taskman;
    _configurator = conf;
  }
  
  public void exec(TaskContext ctx) {
    TaskOutput out = ctx.getTaskOutput();
    if(_lock.authorize()){
      Set<String> serverTags = _configurator.getTags();
      out.info("Starting execution of processes: " + _processRefs);
      for(ProcessRef processRef:_processRefs){
        Set<String> processTags = processRef.getDist().getTagSet();
        processTags.addAll(processRef.getProcessConfig().getTagSet());
        out.debug("Got server tags: " + serverTags);
        out.debug("Got process tags: " + processTags);
        if(processTags.size() > 0 && !serverTags.containsAll(processTags)){
          out.warning("Not executing: " + processRef.getProcessConfig().getName() + " - process tags: " 
              + processTags + " do not match server tags: " + serverTags);
          _startedCount++;
        }
        else{
          int instanceCount = _db.getProcessCountFor(processRef);
          if(instanceCount < processRef.getInstanceCount()){
            out.info("Executing process instance #" 
                  + (instanceCount+1) + " of: " + processRef.getProcessConfig().getName() + " of distribution: " 
                  + processRef.getDist().getName() + ", " + processRef.getDist().getVersion() + ", " + processRef.getProfile());
            ExecTask exec = new ExecTask(_dynSvr, _httpPort, _db, processRef.getDist(), processRef.getProcessConfig(), processRef.getProfile(), _ports);
            _taskman.execSyncTask("ExecProcessTask", exec);
            _startedCount++;
          }
        }
      }
    }
    else{
      out.debug("Not executing now; waiting for startup interval exhaustion");
    }
  }
  
  public boolean isAborted() {
    return _startedCount >= _processRefs.size();
  }
  
}
