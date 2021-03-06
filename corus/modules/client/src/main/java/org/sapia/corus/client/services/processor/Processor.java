package org.sapia.corus.client.services.processor;

import java.util.List;

import org.sapia.corus.client.Module;
import org.sapia.corus.client.common.Arg;
import org.sapia.corus.client.common.ProgressQueue;
import org.sapia.corus.client.exceptions.processor.ProcessNotFoundException;
import org.sapia.corus.client.exceptions.processor.TooManyProcessInstanceException;
import org.sapia.corus.interop.Status;

/**
 * This interface specifies remote process administration behavior.
 * 
 * @author Yanick Duchesne
 */

public interface Processor extends java.rmi.Remote, Module {
  String ROLE = Processor.class.getName();

  /**
   * Starts process(es) corresponding to the passed in parameters.
   * 
   * @param criteria
   *          the {@link ProcessCriteria} indicating which process should be
   *          executed.
   * @param instances
   *          the number of process(es) to start.
   * 
   * @return a {@link ProgressQueue}
   */
  public ProgressQueue exec(ProcessCriteria criteria, int instances) throws TooManyProcessInstanceException;

  /**
   * @param execConfigName
   *          the name of the execution configuration from which to start
   *          processes.
   * @return a {@link ProgressQueue}
   */
  public ProgressQueue execConfig(String execConfigName);

  /**
   * Resumes all suspended processes.
   * 
   * @return a {@link ProgressQueue}.
   */
  public ProgressQueue resume();

  /**
   * Shuts down and restarts the process with the given ID.
   * 
   * @param pid
   *          a Corus pid.
   * @param prefs
   *          a {@link KillPreferences} instance. 
   */
  public void restartByAdmin(String pid, KillPreferences prefs) throws ProcessNotFoundException;

  /**
   * Restarts the process with the given ID.
   * 
   * @see Processor#restart(String)
   * @param prefs
   *          a {@link KillPreferences} instance.
   */
  public void restart(String pid, KillPreferences prefs) throws ProcessNotFoundException;

  /**
   * Restarts the process(es) corresponding to the passed in parameters.
   * 
   * @param criteria
   *          the {@link ProcessCriteria} indicating which process should be
   *          restarted.
   * @param prefs
   *          a {@link KillPreferences} instance.
   */
  public void restart(ProcessCriteria criteria, KillPreferences prefs);

  /**
   * Kill the process(es) corresponding to the passed in parameters.
   * 
   * @param criteria
   *          the {@link ProcessCriteria} indicating which process should be
   *          restarted.
   * @param prefs
   *          a {@link KillPreferences} instance.
   */
  public void kill(ProcessCriteria criteria, KillPreferences prefs);

  /**
   * Kills the process with the given identifier.
   * 
   * @param corusPid
   *          a process identifier.
   * @param prefs
   *          a {@link KillPreferences} instance.
   */
  public void kill(String corusPid, KillPreferences prefs) throws ProcessNotFoundException;

  /**
   * @param criteria
   *          a {@link ProcessCriteria}.
   * @return a {@link ProgressQueue}.
   */
  public ProgressQueue resume(ProcessCriteria criteria);

  /**
   * Performs cleanup following process shutdown confirmation.
   * 
   * @param corusPid
   *          a process identifier.
   */
  public void confirmShutdown(String corusPid) throws ProcessNotFoundException;

  /**
   * Return the process whose identifier is given.
   * 
   * @param corusPid
   *          a process identifier.
   * @return a {@link Process} instance.
   */
  public Process getProcess(String corusPid) throws ProcessNotFoundException;

  /**
   * Returns the processes that match the given criteria.
   */
  public List<Process> getProcesses(ProcessCriteria criteria);

  /**
   * Return the status of the process whose identifier is given..
   * 
   * @param corusPid
   *          a process identifier.
   * @return a <code>Status</code> instance.
   */
  public ProcStatus getStatusFor(String corusPid) throws ProcessNotFoundException;

  /**
   * Returns the status for the processes that match the given criteria.
   * 
   * @param criteria
   *          the {@link ProcessCriteria} indicating which process should be
   *          restarted.
   * @return a {@link List} of {@link Status} instances.
   */
  public List<Status> getStatus(ProcessCriteria criteria);

  /**
   * Returns all processes that have acquired one or more ports.
   * 
   * @return a {@link List} of {@link Process} instances.
   */
  public List<Process> getProcessesWithPorts();

  /**
   * @param conf
   *          adds an {@link ExecConfig} to this instance.
   */
  public void addExecConfig(ExecConfig conf);

  /**
   * @return the list of {@link ExecConfig}s that are contained in this
   *         instance.
   */
  public List<ExecConfig> getExecConfigs();

  /**
   * @param name
   *          removes the {@link ExecConfig} whose name matches the given
   *          argument.
   */
  public void removeExecConfig(Arg name);
  
  /**
   * Cleans process info.
   */
  public void clean();

  /**
   * @return this instance's {@link ProcessorConfiguration}
   */
  public ProcessorConfiguration getConfiguration();
}
