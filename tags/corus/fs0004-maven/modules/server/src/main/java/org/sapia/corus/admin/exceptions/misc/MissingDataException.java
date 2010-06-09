package org.sapia.corus.admin.exceptions.misc;

import org.sapia.corus.admin.exceptions.CorusRuntimeException;
import org.sapia.corus.admin.exceptions.ExceptionCode;

/**
 * Thrown when a given looked up service is not found.
 * 
 * @author yduchesne
 *
 */
public class MissingDataException extends CorusRuntimeException{
  
  static final long serialVersionUID = 1L;
  
  public MissingDataException(String msg) {
    super(msg, ExceptionCode.MISSING_DATA.getFullCode());
  }

}
