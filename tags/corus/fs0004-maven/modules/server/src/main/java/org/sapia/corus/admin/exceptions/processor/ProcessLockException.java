package org.sapia.corus.admin.exceptions.processor;

import org.sapia.corus.admin.exceptions.CorusException;
import org.sapia.corus.admin.exceptions.ExceptionCode;

/**
 * Indicates that the lock on a given process could not be obtained.
 * 
 * @author Yanick Duchesne
 */
public class ProcessLockException extends CorusException {
  
  static final long serialVersionUID = 1L;
  
  public ProcessLockException(String msg) {
    super(msg, ExceptionCode.PROCESS_LOCK_UNAVAILABLE.getFullCode());
  }
}
