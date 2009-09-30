package org.sapia.corus.processor;

import java.util.ArrayList;
import java.util.List;

import org.sapia.corus.CorusException;
import org.sapia.corus.LogicException;
import org.sapia.corus.admin.CommandArg;
import org.sapia.corus.admin.services.processor.ExecConfig;
import org.sapia.corus.admin.services.processor.Process;
import org.sapia.corus.admin.services.processor.Processor;
import org.sapia.corus.admin.services.processor.ProcessorConfiguration;
import org.sapia.corus.admin.services.processor.ProcessorConfigurationImpl;
import org.sapia.corus.interop.Status;
import org.sapia.corus.server.processor.ProcessRepository;
import org.sapia.corus.util.ProgressQueue;
import org.sapia.corus.util.ProgressQueueImpl;

public class TestProcessor implements Processor{
  
  private TestProcessRepository db = new TestProcessRepository();
  private ProcessorConfiguration configuration = new  ProcessorConfigurationImpl();
  
  public TestProcessRepository getProcessRepository(){
    return db;
  }
  
  public ProcessorConfiguration getConfiguration() {
    return configuration;
  }
  
  public ProgressQueue exec(CommandArg distName, CommandArg version,
      String profile, CommandArg processName, int instances) {
    ProgressQueue q = new ProgressQueueImpl();
    return q;
  }
  
  public ProgressQueue exec(String execConfigName) {
    ProgressQueue q = new ProgressQueueImpl();
    return q;
  }  

  public ProgressQueue exec(CommandArg distName, CommandArg version,
      String profile, int instances) {
    ProgressQueue q = new ProgressQueueImpl();
    return q;
  }

  public Process getProcess(String corusPid) throws LogicException {
    return db.getProcess(corusPid);
  }
  
  public List<Process> getProcesses() {
    return db.getProcesses();
  }
  
  public List<Process> getProcesses(CommandArg distName) {
    return db.getProcesses(distName);
  }
  
  public List<Process> getProcesses(CommandArg distName, CommandArg version) {
    return db.getProcesses(distName, version);
  }
  
  public List<Process> getProcesses(CommandArg distName, CommandArg version,
      String profile) {
    return db.getProcesses(distName, version, profile);
  }
  
  public List<Process> getProcesses(CommandArg distName, CommandArg version,
      String profile, CommandArg processName) {
    return db.getProcesses(distName, version, profile, processName);
  }
  
  public List<Process> getProcessesWithPorts() {
    return new ArrayList<Process>();
  }
  
  public List<Status> getStatus() {
    return new ArrayList<Status>();
  }
  
  public List<Status> getStatus(CommandArg distName) {
    return getStatus();
  }
  
  public List<Status> getStatus(CommandArg distName, CommandArg version) {
    return getStatus();
  }
  
  public List<Status> getStatus(CommandArg distName, CommandArg version,
      String profile) {
    return getStatus();
  }
  
  public List<Status> getStatus(CommandArg distName, CommandArg version,
      String profile, CommandArg processName) {
    return getStatus();
  }
  
  public ProcStatus getStatusFor(String corusPid) throws LogicException {
    return new ProcStatus(getProcess(corusPid));
  }
  
  public void kill(CommandArg distName, CommandArg version, String profile,
      boolean suspend) throws CorusException {
  }
  
  public void kill(CommandArg distName, CommandArg version, String profile,
      CommandArg processName, boolean suspend) throws CorusException {
  }
  
  public void kill(String corusPid, boolean suspend) throws CorusException {
  }
  
  public void restart(String pid) throws CorusException {
  }
  
  public void restartByAdmin(String pid) throws CorusException {
  }
  
  public ProgressQueue resume() {
    ProgressQueue q = new ProgressQueueImpl();
    return q;
  }
  
  public void addExecConfig(ExecConfig conf) throws LogicException {
    // TODO Auto-generated method stub
    
  }
  
  public List<ExecConfig> getExecConfigs() {
    // TODO Auto-generated method stub
    return null;
  }
  
  public void removeExecConfig(CommandArg name) {
    // TODO Auto-generated method stub
    
  }
  
  public String getRoleName() {
    return Processor.ROLE;
  }

  public ProcessRepository getProcessDB(){
    return db;
  }
}
