package org.sapia.corus.admin.services.cron;

import java.util.List;

import org.sapia.corus.admin.Module;
import org.sapia.corus.admin.exceptions.CorusException;
import org.sapia.corus.admin.exceptions.cron.DuplicateScheduleException;
import org.sapia.corus.admin.exceptions.cron.InvalidTimeException;
import org.sapia.corus.admin.exceptions.processor.ProcessConfigurationNotFoundException;


/**
 * @author Yanick Duchesne
 */
public interface CronModule extends java.rmi.Remote, Module {
  public static final String ROLE = CronModule.class.getName();

  public void addCronJob(CronJobInfo info) throws 
    InvalidTimeException,
    DuplicateScheduleException, 
    ProcessConfigurationNotFoundException, 
    CorusException;

  public void removeCronJob(String id);

  public List<CronJobInfo> listCronJobs();
}
