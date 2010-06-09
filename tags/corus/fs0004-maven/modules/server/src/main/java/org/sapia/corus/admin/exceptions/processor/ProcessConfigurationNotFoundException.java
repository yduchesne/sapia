package org.sapia.corus.admin.exceptions.processor;

import org.sapia.corus.admin.exceptions.CorusException;
import org.sapia.corus.admin.exceptions.ExceptionCode;

public class ProcessConfigurationNotFoundException extends CorusException{
  
  static final long serialVersionUID = 1L;
  
  public ProcessConfigurationNotFoundException(String msg) {
    super(msg, ExceptionCode.PROCESS_CONFIG_NOT_FOUND.getFullCode());
  }

}
