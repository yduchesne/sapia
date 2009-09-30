package org.sapia.corus.processor.task;

import org.sapia.corus.admin.services.processor.Process.ProcessTerminationRequestor;
import org.sapia.corus.taskmanager.v2.TaskExecutionContext;


/**
 * @author Yanick Duchesne
 */
public class TestProcessTerminationTask extends ProcessTerminationTask {
  int onExec     = 0;
  int onMaxRetry = 0;

  public TestProcessTerminationTask(ProcessTerminationRequestor requestor, 
                                    String corusPid, 
                                    int maxRetry) throws Exception{
    super(requestor, corusPid, maxRetry);
  }
  
  protected void cleanupProcess(Process proc, TaskExecutionContext ctx) {
  }
  
  protected void onExec(TaskExecutionContext ctx) {
    onExec++;
  }

  protected boolean onMaxRetry(TaskExecutionContext ctx) {
    onMaxRetry++;

    return false;
  }
  
  protected void onKillConfirmed(TaskExecutionContext ctx) {}
}
