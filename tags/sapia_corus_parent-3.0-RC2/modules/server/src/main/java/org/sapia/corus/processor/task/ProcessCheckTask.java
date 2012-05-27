package org.sapia.corus.processor.task;

import java.util.List;

import org.sapia.corus.client.services.processor.Process;
import org.sapia.corus.client.services.processor.ProcessCriteria;
import org.sapia.corus.client.services.processor.ProcessorConfiguration;
import org.sapia.corus.client.services.processor.Process.ProcessTerminationRequestor;
import org.sapia.corus.taskmanager.core.BackgroundTaskConfig;
import org.sapia.corus.taskmanager.core.Task;
import org.sapia.corus.taskmanager.core.TaskExecutionContext;
import org.sapia.corus.taskmanager.core.TaskParams;


/**
 * This task ensures that all external processes are up and running. It determines
 * so by checking the time at which each process last polled its Corus
 * server. This task is ran continuously at a predefined interval.
 * <p>
 * At runtime, the task goes through currently active processes to determine the ones
 * that are 'down'. For these, it orders a kill/restart.
 * <p>
 * Calls:
 * <ol>
 *  <li> {@link KillTask}: when down processes are detected.
 * </ol>
 * 
 *
 * @author Yanick Duchesne
 */
public class ProcessCheckTask extends Task<Void,Void>{
  
  @Override
  public Void execute(TaskExecutionContext ctx, Void param) throws Throwable {
    ctx.debug("Checking for stale processes...");

    ProcessorConfiguration processorConf = ctx.getServerContext().getServices().getProcessor().getConfiguration();
    List<Process>          processes     = ctx.getServerContext()
      .getServices()
      .getProcesses()
      .getActiveProcesses()
      .getProcesses(ProcessCriteria.builder().all());
    
    Process proc;

    for (int i = 0; i < processes.size(); i++) {
      proc = processes.get(i);
      if ((proc.getStatus() == Process.LifeCycleStatus.ACTIVE) &&
            proc.isTimedOut(processorConf.getProcessTimeoutMillis())) {
        if (proc.getLock().isLocked()) {
          ctx.warn(String.format("Process timed out but locked, probably terminating or restarting: %s", proc));
        } else {
          proc.setStatus(Process.LifeCycleStatus.KILL_REQUESTED);
          proc.save();

          ctx.warn(String.format("Process timed out - ordering kill: %s. Will retry %s time(s)", proc, proc.getMaxKillRetry()));
          ctx.getTaskManager().executeBackground(
              new KillTask(proc.getMaxKillRetry()), 
              TaskParams.createFor(proc, ProcessTerminationRequestor.KILL_REQUESTOR_SERVER),
              BackgroundTaskConfig.create()
                .setExecDelay(processorConf.getKillIntervalMillis())
                .setExecInterval(processorConf.getKillIntervalMillis())
          );
        }
      }
    }

    ctx.debug("Stale process check finished");
    
    return null;
  }
}
