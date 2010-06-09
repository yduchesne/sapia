package org.sapia.corus.admin.exceptions.cron;

import org.sapia.corus.admin.exceptions.CorusException;
import org.sapia.corus.admin.exceptions.ExceptionCode;


/**
 * 
 * @author Yanick Duchesne
 */
public class InvalidTimeException extends CorusException {
  
  static final long serialVersionUID = 1L;
  
  public InvalidTimeException(String msg) {
    super(msg, ExceptionCode.INVALID_TIME.getFullCode());
  }

}
