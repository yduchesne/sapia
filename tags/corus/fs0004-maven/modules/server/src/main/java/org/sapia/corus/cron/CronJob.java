package org.sapia.corus.cron;

import org.sapia.corus.admin.ArgFactory;
import org.sapia.corus.admin.services.cron.CronJobInfo;
import org.sapia.corus.admin.services.processor.Processor;
import org.sapia.corus.core.CorusRuntime;
import org.sapia.corus.util.progress.ProgressQueue;
import org.sapia.corus.util.progress.ProgressQueueLogger;

import fr.dyade.jdring.AlarmEntry;
import fr.dyade.jdring.AlarmListener;


/**
 * @author Yanick Duchesne
 */
public class CronJob implements java.io.Serializable, AlarmListener {

  static final long serialVersionUID = 1L;
  
  private CronJobInfo _info;

  CronJob(){}
  
  CronJob(CronJobInfo info) {
    _info = info;
  }

  CronJobInfo getInfo() {
    return _info;
  }

  /**
   * @see com.jalios.jdring.AlarmListener#handleAlarm(AlarmEntry)
   */
  public void handleAlarm(AlarmEntry entry) {
    try {
      Processor     proc  = (Processor) CorusRuntime.getCorus().lookup(Processor.ROLE);
      ProgressQueue queue = proc.exec(ArgFactory.parse(_info.getDistribution()),
                                      ArgFactory.parse(_info.getVersion()), _info.getProfile(),
                                      ArgFactory.parse(_info.getVmName()), 1);
      CronModuleImpl.instance.logger().info("executing schedule VM " + _info);
      ProgressQueueLogger.transferMessages(CronModuleImpl.instance.logger(), queue);
    } catch (Throwable t) {
      entry.isRepetitive = false;
      CronModuleImpl.instance.removeCronJob(_info.getId());
    }
  }
}
