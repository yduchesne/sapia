package org.sapia.corus.admin.exceptions.port;

import org.sapia.corus.admin.exceptions.CorusException;
import org.sapia.corus.admin.exceptions.ExceptionCode;

/**
 * Thrown when port range is invalid.
 * 
 * @author yduchesne
 */
public class PortRangeInvalidException extends CorusException{

  static final long serialVersionUID = 1L;

  /** Creates a new instance of PortRangeInvalidException */
  public PortRangeInvalidException(String msg) {
    super(msg, ExceptionCode.PORT_RANGE_INVALID.getFullCode());
  }
  
}
