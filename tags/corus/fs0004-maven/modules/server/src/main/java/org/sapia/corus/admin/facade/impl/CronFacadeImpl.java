package org.sapia.corus.admin.facade.impl;

import java.util.List;

import org.sapia.corus.admin.Results;
import org.sapia.corus.admin.exceptions.cron.DuplicateScheduleException;
import org.sapia.corus.admin.exceptions.cron.InvalidTimeException;
import org.sapia.corus.admin.exceptions.processor.ProcessConfigurationNotFoundException;
import org.sapia.corus.admin.facade.CorusConnectionContext;
import org.sapia.corus.admin.facade.CronFacade;
import org.sapia.corus.admin.services.cron.CronJobInfo;
import org.sapia.corus.admin.services.cron.CronModule;
import org.sapia.corus.core.ClusterInfo;

public class CronFacadeImpl extends FacadeHelper<CronModule> implements CronFacade{
  
  public CronFacadeImpl(CorusConnectionContext context) {
    super(context, CronModule.class);
  }
  
  @Override
  public void addCronJon(CronJobInfo info) throws InvalidTimeException,
      DuplicateScheduleException, ProcessConfigurationNotFoundException,
      Exception {
    context.lookup(CronModule.class).addCronJob(info);
  }
  
  @Override
  public Results<List<CronJobInfo>> getCronJobs(ClusterInfo cluster) {
    Results<List<CronJobInfo>> results = new Results<List<CronJobInfo>>();
    proxy.listCronJobs();
    invoker.invokeLenient(results, cluster);
    return results;
  }
  
  @Override
  public void removeCronJob(String id) {
    context.lookup(CronModule.class).removeCronJob(id);
  }

}
