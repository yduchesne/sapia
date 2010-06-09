package org.sapia.corus.admin.exceptions.deployer;

import org.sapia.corus.admin.exceptions.CorusException;
import org.sapia.corus.admin.exceptions.ExceptionCode;

public class DistributionNotFoundException extends CorusException{
  
  static final long serialVersionUID = 1L;
  
  public DistributionNotFoundException(String msg) {
    super(msg, ExceptionCode.DISTRIBUTION_NOT_FOUND.getFullCode());
  }

}
