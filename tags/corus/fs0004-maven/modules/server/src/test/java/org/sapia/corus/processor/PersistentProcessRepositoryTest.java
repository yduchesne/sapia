package org.sapia.corus.processor;


import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sapia.corus.client.exceptions.processor.ProcessNotFoundException;
import org.sapia.corus.client.services.db.DbMap;
import org.sapia.corus.client.services.processor.DistributionInfo;
import org.sapia.corus.client.services.processor.LockOwner;
import org.sapia.corus.client.services.processor.Process;
import org.sapia.corus.db.TestDbModule;

public class PersistentProcessRepositoryTest {

  private static TestDbModule db;
  private static ProcessRepository repo;
  
  @BeforeClass
  public static void setUp() throws Exception {
    db = new TestDbModule();
    db.setup();
    
    DbMap<String, org.sapia.corus.client.services.processor.Process> suspended = db.createDbMap("suspended", String.class, org.sapia.corus.client.services.processor.Process.class);
    DbMap<String, org.sapia.corus.client.services.processor.Process> active = db.createDbMap("active", String.class, org.sapia.corus.client.services.processor.Process.class);
    DbMap<String, org.sapia.corus.client.services.processor.Process> toRestart = db.createDbMap("toRestart", String.class, org.sapia.corus.client.services.processor.Process.class);

    repo = new ProcessRepositoryImpl(
        new ProcessDatabaseImpl(suspended), 
        new ProcessDatabaseImpl(active), 
        new ProcessDatabaseImpl(toRestart));
  }
  
  @AfterClass
  public static void tearDown(){
    db.teardown();
  }
  
  @Test
  public void testAddProcess() throws Exception{
    DistributionInfo info = new DistributionInfo("test", "1.0", "prod", "app");
    Process proc = new Process(info);
    repo.getActiveProcesses().addProcess(proc);
    repo.getActiveProcesses().getProcess(proc.getProcessID());
  }
  
  @Test
  public void testRemoveProcess() throws Exception{
    DistributionInfo info = new DistributionInfo("test", "1.0", "prod", "app");
    Process proc = new Process(info);
    
    repo.getActiveProcesses().addProcess(proc);
    repo.getActiveProcesses().removeProcess(proc.getProcessID());
    try{
      repo.getActiveProcesses().getProcess(proc.getProcessID());
      Assert.fail("Process was not removed");
    }catch(ProcessNotFoundException e){
    }
  }
  
  @Test
  public void testUpdateProcess() throws Exception{
    DistributionInfo info = new DistributionInfo("test", "1.0", "prod", "app");
    Process proc = new Process(info);
    
    repo.getActiveProcesses().addProcess(proc);
    proc.setOsPid("1234");
    proc.save();
    
    Process updated = repo.getActiveProcesses().getProcess(proc.getProcessID());
    Assert.assertEquals("1234", updated.getOsPid());
  }
  
  @Test
  public void testLock() throws Exception{
    DistributionInfo info = new DistributionInfo("test", "1.0", "prod", "app");
    Process proc = new Process(info);
    
    LockOwner lck = new LockOwner();
    repo.getActiveProcesses().addProcess(proc);
    proc.acquireLock(lck);
    proc.save();
    
    Process updated = repo.getActiveProcesses().getProcess(proc.getProcessID());
    Assert.assertTrue("Process should be locked", updated.isLocked());
  }

  
}
