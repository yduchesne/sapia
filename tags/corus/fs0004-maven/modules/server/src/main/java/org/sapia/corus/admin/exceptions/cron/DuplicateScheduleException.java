package org.sapia.corus.admin.exceptions.cron;

import org.sapia.corus.admin.exceptions.CorusException;
import org.sapia.corus.admin.exceptions.ExceptionCode;

public class DuplicateScheduleException extends CorusException{

  static final long serialVersionUID = 1L;
  
  public DuplicateScheduleException(String msg) {
    super(msg, ExceptionCode.DUPLICATE_SCHEDULE.getFullCode());
  }

}
