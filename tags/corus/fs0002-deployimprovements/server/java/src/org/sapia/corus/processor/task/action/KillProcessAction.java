package org.sapia.corus.processor.task.action;

import org.sapia.corus.admin.services.processor.Process;
import org.sapia.corus.admin.services.processor.ProcessorConfiguration;
import org.sapia.corus.admin.services.processor.Processor;
import org.sapia.corus.admin.services.processor.Process.ProcessTerminationRequestor;
import org.sapia.corus.processor.task.KillTask;
import org.sapia.corus.processor.task.TaskConfig;
import org.sapia.corus.taskmanager.Action;
import org.sapia.taskman.PeriodicTaskDescriptor;
import org.sapia.taskman.TaskContext;

/**
 * @author Yanick Duchesne
 */
public class KillProcessAction implements Action{
  
  private TaskConfig _config;
  private ProcessTerminationRequestor _requestor;
  private Process _proc;
  
  public KillProcessAction(TaskConfig config,
                           ProcessTerminationRequestor requestor, 
                           Process proc){
    _config = config;
    _requestor = requestor;
    _proc = proc;
  }

  public boolean execute(TaskContext ctx) {
    ProcessorConfiguration processorConf = _config.getServices().lookup(Processor.class).getConfiguration();
    KillTask kill = new KillTask(_config, _requestor, _proc.getProcessID(), _proc.getMaxKillRetry());
    PeriodicTaskDescriptor desc = new PeriodicTaskDescriptor("KillProcessTask", processorConf.getKillIntervalMillis(), kill);
    ctx.execTaskFor(desc);
    return true;
  }

}
