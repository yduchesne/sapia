package org.sapia.corus.cron;

import org.sapia.corus.admin.ArgFactory;

import org.sapia.corus.admin.annotations.Transient;
import org.sapia.corus.admin.services.cron.CronJobInfo;
import org.sapia.corus.admin.services.processor.Processor;
import org.sapia.corus.core.ServerContext;
import org.sapia.corus.util.progress.ProgressQueue;
import org.sapia.corus.util.progress.ProgressQueueLogger;

import fr.dyade.jdring.AlarmEntry;
import fr.dyade.jdring.AlarmListener;

/**
 * @author Yanick Duchesne
 */
public class CronJob implements java.io.Serializable, AlarmListener {

  static final long serialVersionUID = 1L;
  
  private transient CronModuleImpl _owner;
  private transient ServerContext _serverContext;
  private CronJobInfo _info;

  CronJob(){}
  
  CronJob(CronJobInfo info) {
    _info = info;
  }
  
  void init(CronModuleImpl owner, ServerContext ctx){
    _owner = owner;
    _serverContext = ctx;
  }
  
  @Transient
  ServerContext getServerContext() {
    return _serverContext;
  }
  
  @Transient
  public CronModuleImpl getOwner() {
    return _owner;
  }

  CronJobInfo getInfo() {
    return _info;
  }

  /**
   * @see com.jalios.jdring.AlarmListener#handleAlarm(AlarmEntry)
   */
  public void handleAlarm(AlarmEntry entry) {
    try {
      Processor     proc  = _serverContext.getServices().getProcessor();
      ProgressQueue queue = proc.exec(ArgFactory.parse(_info.getDistribution()),
                                      ArgFactory.parse(_info.getVersion()), _info.getProfile(),
                                      ArgFactory.parse(_info.getProcessName()), 1);
      _owner.logger().info("executing schedule VM " + _info);
      ProgressQueueLogger.transferMessages(_owner.logger(), queue);
    } catch (Throwable t) {
      entry.isRepetitive = false;
      _owner.removeCronJob(_info.getId());
    }
  }
}
