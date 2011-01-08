package org.sapia.corus.processor.task;

import org.sapia.corus.client.exceptions.processor.ProcessNotFoundException;
import org.sapia.corus.client.services.processor.DistributionInfo;
import org.sapia.corus.client.services.processor.Process;
import org.sapia.corus.client.services.processor.Process.ProcessTerminationRequestor;
import org.sapia.corus.processor.ProcessorConfigurationImpl;
import org.sapia.corus.taskmanager.core.TaskExecutionContext;

/**
 * @author Yanick Duchesne
 */
public class KillTaskTest extends BaseTaskTest{
  
  /**
   * Constructor for KillTaskTest.
   * @param arg0
   */
  public KillTaskTest(String arg0) {
    super(arg0);
  }
  
  
  public void testKillFromCorusNotConfirmed() throws Exception {
    DistributionInfo dist = new DistributionInfo("test", "1.0", "test", "testVm");
    Process          proc = new Process(dist);
    db.getActiveProcesses().addProcess(proc);

    TestKill kill = new TestKill(ProcessTerminationRequestor.KILL_REQUESTOR_SERVER,
                                 proc.getProcessID());
    tm.executeAndWait(kill).get();
    super.assertTrue(kill.killed != true);
  }
  
  public void testKillFromCorusConfirmed() throws Exception {
    DistributionInfo dist = new DistributionInfo("test", "1.0", "test", "testVm");
    Process          proc = new Process(dist);
    db.getActiveProcesses().addProcess(proc);
    
    TestKill kill = new TestKill(
        ProcessTerminationRequestor.KILL_REQUESTOR_SERVER, 
        proc.getProcessID());
    
    proc.confirmKilled();
    proc.save();

    tm.executeAndWait(kill).get();  
    super.assertTrue(kill.killed);

    try {
      db.getActiveProcesses().getProcess(proc.getProcessID());
      throw new Exception("Process not removed from db");
    } catch (ProcessNotFoundException e) {
      //ok
    }

    super.assertTrue(!kill.restart);
  }

  public void testKillMaxAttemptReached() throws Exception {
    DistributionInfo dist = new DistributionInfo("test", "1.0", "test", "testVm");
    Process          proc = new Process(dist);
    db.getActiveProcesses().addProcess(proc);

    Thread.sleep(1500);

    TestKill kill = new TestKill(
        ProcessTerminationRequestor.KILL_REQUESTOR_SERVER, 
        proc.getProcessID());

    tm.executeAndWait(kill).get();
    super.assertTrue(!kill.killed);
    super.assertTrue(!kill.restart);
    tm.executeAndWait(kill).get();
    super.assertTrue(!kill.killed);
    super.assertTrue(!kill.restart);
    tm.executeAndWait(kill).get();
    proc.confirmKilled();    
    tm.executeAndWait(kill).get();    
    super.assertTrue(kill.killed);

    try {
      db.getActiveProcesses().getProcess(proc.getProcessID());
      throw new Exception("Process not removed from db");
    } catch (ProcessNotFoundException e) {
      //ok
    }
  }

  public void testRestart() throws Exception {
    DistributionInfo dist = new DistributionInfo("test", "1.0", "test", "testVm");
    Process          proc = new Process(dist);
    db.getActiveProcesses().addProcess(proc);
    
    ProcessorConfigurationImpl processorConf = (ProcessorConfigurationImpl) ctx.getProc().getConfiguration();
    processorConf.setRestartInterval(1);

    Thread.sleep(1500);

    TestKill kill = new TestKill(ProcessTerminationRequestor.KILL_REQUESTOR_SERVER, 
                                 proc.getProcessID());
    proc.confirmKilled();
    proc.save();
    tm.executeAndWait(kill).get();
    super.assertTrue(kill.killed);
    super.assertTrue(kill.restart);

    try {
      db.getActiveProcesses().getProcess(proc.getProcessID());
      throw new Exception("Process not removed from db");
    } catch (ProcessNotFoundException e) {
      //ok
    }
  }
  

  public void testRestartDenied() throws Exception {
    DistributionInfo dist = new DistributionInfo("test", "1.0", "test", "testVm");
    Process          proc = new Process(dist);
    db.getActiveProcesses().addProcess(proc);
    Thread.sleep(500);

    TestKill kill = new TestKill(
        ProcessTerminationRequestor.KILL_REQUESTOR_SERVER, proc.getProcessID());
    proc.confirmKilled();
    proc.save();
    tm.executeAndWait(kill).get();
    super.assertTrue(kill.killed);
    super.assertTrue(kill.restart == false);

    try {
      db.getActiveProcesses().getProcess(proc.getProcessID());
      throw new Exception("Process not removed from db");
    } catch (ProcessNotFoundException e) {
      //ok
    }
  }

  static class TestKill extends KillTask {
    boolean killed;
    boolean restart;

    TestKill(ProcessTerminationRequestor requestor, String vmId, int maxRetry) throws Exception{
      super(requestor, vmId, maxRetry);
    }
    
    TestKill(ProcessTerminationRequestor requestor, String vmId) throws Exception{
      super(requestor, vmId, 3);
    }
    
    @Override
    protected void onKillConfirmed(TaskExecutionContext ctx) throws Throwable {
      killed = true;
      super.onKillConfirmed(ctx);
    }
    
    @Override
    protected void onMaxExecutionReached(TaskExecutionContext ctx)
        throws Throwable {
      super.onMaxExecutionReached(ctx);
      killed = true;
    }
    
    @Override
    protected void onRestarted(TaskExecutionContext ctx) {
      restart = true;
    }
    
  }
}