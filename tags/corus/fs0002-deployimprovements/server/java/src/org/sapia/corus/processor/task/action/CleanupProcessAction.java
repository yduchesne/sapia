package org.sapia.corus.processor.task.action;

import java.io.File;

import org.sapia.corus.admin.services.processor.Process;
import org.sapia.corus.processor.task.TaskConfig;
import org.sapia.corus.taskmanager.Action;
import org.sapia.corus.taskmanager.tasks.TaskFactory;
import org.sapia.taskman.TaskContext;

/**
 * @author Yanick Duchesne
 */
public class CleanupProcessAction implements Action{
  
  private TaskConfig _config;
  private Process   _proc;
  
  public CleanupProcessAction(TaskConfig config, Process proc){
    _config = config;
    _proc = proc;
  }
  
  public boolean execute(TaskContext ctx) {
    if (_proc.getProcessDir() != null) {
      if(_proc.isDeleteOnKill()){
        File f = new File(_proc.getProcessDir());
        //ctx.execSyncNestedTask( "DeleteFileTask", TaskFactory.newDeleteDirTask(f));
        TaskFactory.newDeleteDirTask(f).exec(ctx);

        if (f.exists()) {
          ctx.getTaskOutput().warning("Could not destroy process directory: " +
                        f.getAbsolutePath());
        }
      }
    }

    _config.getServices().getProcesses().getActiveProcesses().removeProcess(_proc.getProcessID());
    if(_proc.isDeleteOnKill()){
      ctx.getTaskOutput().warning("Process successfully terminated and cleaned up: " + _proc.getProcessID());
    }
    else{
      ctx.getTaskOutput().warning("Process successfully terminated: " + _proc.getProcessID());
    }
    return true;
  }

}
