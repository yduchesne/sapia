package org.sapia.corus.processor.task.action;

import java.io.File;

import org.sapia.corus.processor.ProcessInfo;
import org.sapia.corus.taskmanager.Action;
import org.sapia.taskman.TaskContext;

/**
 * @author Yanick Duchesne
 */
public class MakeProcessDirAction implements Action{
  
  private ProcessInfo _info;
  private File        _processDir;
  
  public MakeProcessDirAction(ProcessInfo info){
    _info = info;
  } 

  public boolean execute(TaskContext ctx) {
    File processDir = new File(_info.getDistribution().getProcessesDir() +
                               File.separator +
                               _info.getProcess().getProcessID());

    if (_info.isRestart() && !processDir.exists()) {
      ctx.getTaskOutput().warning("Process directory: " + processDir +
                    " does not exist; restart aborted");
      return false;
    } else {
      processDir.mkdirs();

      if (!processDir.exists()) {
        ctx.getTaskOutput().warning("Could not make process directory: " + processDir +
                      "; startup aborted");

        return false;
      }
    }
    
    _processDir = processDir;
    return true;
  }
  
  public File getProcessDir(){
    return _processDir;
  }

}
