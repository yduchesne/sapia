package org.sapia.corus.admin.exceptions.deployer;

import org.sapia.corus.admin.exceptions.CorusException;
import org.sapia.corus.admin.exceptions.ExceptionCode;



/**
 * Signals that a distribution with a given name and version has already been deployed.
 * 
 * @author Yanick Duchesne
 */
public class RunningProcessesException extends CorusException {
  
  static final long serialVersionUID = 1L;
  
  /**
   * Constructor for DuplicateDistributionException.
   * @param msg
   */
  public RunningProcessesException(String msg) {
    super(msg, ExceptionCode.RUNNING_PROCESSES.getFullCode());
  }
}
 
