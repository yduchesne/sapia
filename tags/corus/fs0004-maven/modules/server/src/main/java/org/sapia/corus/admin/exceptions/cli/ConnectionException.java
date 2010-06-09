package org.sapia.corus.admin.exceptions.cli;

import org.sapia.corus.admin.exceptions.CorusRuntimeException;
import org.sapia.corus.admin.exceptions.ExceptionCode;

public class ConnectionException extends CorusRuntimeException{
  
  static final long serialVersionUID = 1L;
  
  public ConnectionException(String msg, Throwable err) {
    super(msg, ExceptionCode.CONNECTION_ERROR.getFullCode(), err);
  }

}
