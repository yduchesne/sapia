package org.sapia.corus.processor;

import java.util.List;

import org.sapia.corus.client.common.Arg;
import org.sapia.corus.client.exceptions.processor.ProcessNotFoundException;
import org.sapia.corus.client.services.processor.Process;

public interface ProcessDatabase {

  /**
   * @param process a <code>Process</code>.
   */
  public abstract void addProcess(Process process);
  
  /**
   * @param corusPid a process identifier.
   * @return <code>true</code> if this instance contains the given
   * identifier.
   */
  public abstract boolean containsProcess(String corusPid);

  /**
   * Removes all processes corresponding to the given parameters.
   * 
   * @param name the name of the distribution from which to remove processes.
   * @param version the version of the distribution from which to remove processes. 
   */
  public abstract void removeProcesses(Arg name, Arg version);

  /**
   * @return the <code>List</code> of <code>Process</code>es held by
   * this instance.
   */
  public abstract List<Process> getProcesses();

  /**
   * Returns the list of processes corresponding to the distribution
   * whose name is given.
   * 
   * @param name a distribution name.
   * @return a <code>List</code> of processes.
   */
  public abstract List<Process> getProcesses(Arg name);

  /**
   * Returns the list of processes corresponding to the distribution
   * whose name and version are given.
   * 
   * @param name a distribution name.
   * @param version a distribution version. 
   * @return a <code>List</code> of processes.
   */
  public abstract List<Process> getProcesses(Arg name, Arg version);

  /**
   * @param name a distribution name
   * @param version a distribution version.
   * @return
   */
  public abstract List<Process> getProcesses(String name, String version,
      String processName, String profile);

  /**
   * Returns the list of processes corresponding to the distribution
   * whose name, version and profile are given.
   * 
   * @param name a distribution name.
   * @param version a distribution version. 
   * @param profile a distribution . 
   * @return a <code>List</code> of processes.
   */
  public abstract List<Process> getProcesses(Arg name,
      Arg version, String profile);

  /**
   * Returns the list of processes corresponding to the distribution
   * whose name, version, profile and process config name are given.
   * 
   * @param name a distribution name.
   * @param version a distribution version. 
   * @param profile a distribution . 
   * @param processName a process config name.
   * @return a <code>List</code> of processes.
   */
  public abstract List<Process> getProcesses(Arg name,
      Arg version, String profile, Arg processName);

  /**
   * Removes the process with the given ID.
   * 
   * @param corusPid a Corus process ID.
   */
  public abstract void removeProcess(String corusPid);

  /**
   * @param corusPid a Corus process ID.
   * @return the {@link Process} corresponding to the given ID.
   * @throws ProcessNotFoundException if no process with the given ID could
   * be found.
   */
  public abstract Process getProcess(String corusPid) throws ProcessNotFoundException;

}