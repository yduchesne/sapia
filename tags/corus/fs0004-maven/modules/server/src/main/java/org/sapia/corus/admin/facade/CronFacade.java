package org.sapia.corus.admin.facade;

import java.util.List;

import org.sapia.corus.admin.Results;
import org.sapia.corus.admin.exceptions.cron.DuplicateScheduleException;
import org.sapia.corus.admin.exceptions.cron.InvalidTimeException;
import org.sapia.corus.admin.exceptions.processor.ProcessConfigurationNotFoundException;
import org.sapia.corus.admin.services.cron.CronJobInfo;
import org.sapia.corus.core.ClusterInfo;

public interface CronFacade {
  
  /**
   * Adds a "cron job" (scheduled process) to the Corus server.
   *
   * @param info a {@link CronJobInfo} instance.
   */
  public void addCronJon(CronJobInfo info) 
  throws     
  InvalidTimeException,
  DuplicateScheduleException, 
  ProcessConfigurationNotFoundException, 
  Exception;

  /**
   * Removes the Cron job corresponding to the given identifier.
   *
   * @param pid the identifier of the Cron job to remove.
   */
  public void removeCronJob(String pid);

  /**
   * List the currently configured Cron jobs.
   *
   * @param cluster a {@link ClusterInfo} instance.
   *
   * @return a {@link Results} instance containing {@link CronJobInfo} instances.
   * @see org.sapia.corus.admin.services.cron.CronJobInfo

   */
  public Results<List<CronJobInfo>> getCronJobs(ClusterInfo cluster);

}
