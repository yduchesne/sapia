package org.sapia.corus.admin.exceptions.processor;

import org.sapia.corus.admin.exceptions.CorusException;
import org.sapia.corus.admin.exceptions.ExceptionCode;

public class ProcessNotFoundException extends CorusException{
  
  static final long serialVersionUID = 1L;
  
  public ProcessNotFoundException(String msg) {
    super(msg, ExceptionCode.PROCESS_NOT_FOUND.getFullCode());
  }

}
