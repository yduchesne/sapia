package org.sapia.corus.processor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.sapia.corus.admin.Arg;
import org.sapia.corus.admin.services.processor.Process;
import org.sapia.corus.db.DbMap;
import org.sapia.corus.exceptions.LogicException;


/**
 * Holds <code>Process</code> instances.
 * 
 * @author Yanick Duchesne
 */
public class ProcessDatabaseImpl implements ProcessDatabase {
  private DbMap<String, Process> _processes;

  
  public ProcessDatabaseImpl(DbMap<String, Process> map) {
    _processes = map;
  }
  
  public synchronized void addProcess(Process process) {
    _processes.put(process.getProcessID(), process);
  }
  
  public synchronized boolean containsProcess(String corusPid){
    return _processes.get(corusPid) != null;
  }

  public synchronized void removeProcesses(Arg name, Arg version) {
    Process  current;
    Iterator<Process> processes = _processes.values();
    List<Process>     toRemove  = new ArrayList<Process>();

    for (; processes.hasNext();) {
      current = (Process) processes.next();

      if (name.matches(current.getDistributionInfo().getName()) &&
            version.matches(current.getDistributionInfo().getVersion())) {
        toRemove.add(current);
      }
    }

    for (int i = 0; i < toRemove.size(); i++) {
      _processes.remove(((Process) toRemove.get(i)).getProcessID());
    }
  }

  public synchronized List<Process> getProcesses() {
    List<Process>     toReturn  = new ArrayList<Process>();
    Iterator<Process> processes = _processes.values();

    for (; processes.hasNext();) {
      toReturn.add((Process) processes.next());
    }

    return toReturn;
  }
  
  public synchronized List<Process> getProcesses(Arg name) {
    List<Process> toReturn  = new ArrayList<Process>();
    Process  current;
    Iterator<Process> processes = _processes.values();

    for (; processes.hasNext();) {
      current = (Process) processes.next();

      if (name.matches(current.getDistributionInfo().getName())) {
        toReturn.add(current);
      }
    }

    return toReturn;
  }
 
  public synchronized List<Process> getProcesses(Arg name, Arg version) {
    List<Process>     toReturn  = new ArrayList<Process>();
    Process  current;
    Iterator<Process> processes = _processes.values();

    for (; processes.hasNext();) {
      current = (Process) processes.next();

      if (name.matches(current.getDistributionInfo().getName()) &&
            version.matches(current.getDistributionInfo().getVersion())) {
        toReturn.add(current);
      }
    }

    return toReturn;
  }
  
  public synchronized List<Process> getProcesses(String name, String version, String processName, String profile) {
    List<Process>     toReturn  = new ArrayList<Process>();
    Process  current;
    Iterator<Process> processes = _processes.values();

    for (; processes.hasNext();) {
      current = (Process) processes.next();

      if (name.equals(current.getDistributionInfo().getName()) &&
          version.equals(current.getDistributionInfo().getVersion()) &&
          profile.equals(current.getDistributionInfo().getProfile()) &&
          processName.equals(current.getDistributionInfo().getProcessName())) {
        toReturn.add(current);
      }
    }

    return toReturn;
  }
  
  public synchronized List<Process> getProcesses(Arg name, Arg version, String profile) {
    List<Process>     toReturn  = new ArrayList<Process>();
    Process  current;
    Iterator<Process> processes = _processes.values();

    for (; processes.hasNext();) {
      current = (Process) processes.next();

      if (name.matches(current.getDistributionInfo().getName()) &&
          version.matches(current.getDistributionInfo().getVersion()) &&
            (profile == null || current.getDistributionInfo().getProfile().equals(profile))) {
        toReturn.add(current);
      }
    }

    return toReturn;
  }
   
  public synchronized List<Process> getProcesses(Arg name, Arg version, String profile,
                                 Arg processName) {
    List<Process>     toReturn  = new ArrayList<Process>();
    Process  current;
    Iterator<Process> processes = _processes.values();

    for (; processes.hasNext();) {
      current = (Process) processes.next();
      if (name.matches(current.getDistributionInfo().getName()) &&
          version.matches(current.getDistributionInfo().getVersion()) &&
          (profile == null || current.getDistributionInfo().getProfile().equals(profile)) &&
          processName.matches(current.getDistributionInfo().getProcessName())) {
        toReturn.add(current);
      }
    }

    return toReturn;
  }

  public synchronized void removeProcess(String corusPid) {
    _processes.remove(corusPid);
  }

  public synchronized Process getProcess(String corusPid) throws LogicException {
    Process current = (Process) _processes.get(corusPid);

    if (current == null) {
      throw new LogicException("No process for ID: " + corusPid);
    }

    return current;
  }
}
