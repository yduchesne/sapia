package org.sapia.soto.scheduler;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.quartz.Calendar;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.SchedulerMetaData;
import org.quartz.Trigger;
import org.quartz.TriggerListener;
import org.quartz.UnableToInterruptJobException;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.JobFactory;
import org.sapia.soto.Service;
import org.sapia.soto.util.Param;

/**
 * This class implements the Quartz <code>Scheduler</code> interface on top of a 
 * scheduler instance. The internal instance is created in this class' <code>init()</code>
 * method.
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2005 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class SchedulerService implements Scheduler, Service{
  
  private Scheduler _sched;
  private List _params = new ArrayList();
  private Properties _props = new Properties();
  
  ///////////////////////// Instance methods //////////////////////////
  
  public Param createProperty(){
    Param p = new Param();
    _params.add(p);
    return p;
  }
  
  public void setProperties(Properties props){
    Enumeration names = props.propertyNames();
    while(names.hasMoreElements()){
      String name = (String)names.nextElement();
      _props.setProperty(name, props.getProperty(name));
    }
  }
  
  //////////////////// Service interface methods ////////////////////  
  
  /**
   * @see org.sapia.soto.Service#init()
   */
  public void init() throws Exception {
    StdSchedulerFactory fac;
    
    if (_params.size() == 0 && _props.size() == 0) {
      // Use default config file of quartz
      fac = new StdSchedulerFactory();
      
    } else {
      // Create properties with the service parameters
      Param param;
      for (int i = 0; i < _params.size(); i++) {
        param = (Param) _params.get(i);
        if (param.getName() != null && param.getValue() != null) {
          _props.setProperty(param.getName(), param.getValue().toString());
        }
      }
      
      if (_props.size() > 0) {
        fac = new StdSchedulerFactory(_props);
      } else {
        fac = new StdSchedulerFactory();
      }
    }
    _sched = fac.getScheduler();
    _sched.start();
  }
  
  /**
   * @see org.sapia.soto.Service#start()
   */
  public void start() throws SchedulerException {
    // start() is also a method of the Scheduler interface
  }

  /**
   * @see org.sapia.soto.Service#dispose()
   */
  public void dispose() {
    if(_sched != null){
      try{
        _sched.shutdown(true);
      }catch(SchedulerException e){
        //noop
      }
    }
  }

  //////////////////// Scheduler interface methods ////////////////////
  
  /**
   * @see org.quartz.Scheduler#addCalendar(java.lang.String, org.quartz.Calendar, boolean, boolean)
   */
  public void addCalendar(String name, Calendar cal, boolean replace, boolean updateTriggers)
      throws SchedulerException {
    _sched.addCalendar(name, cal, replace, updateTriggers);
  }
  
  /**
   * @see org.quartz.Scheduler#addGlobalJobListener(org.quartz.JobListener)
   */
  public void addGlobalJobListener(JobListener listener) throws SchedulerException {
    _sched.addGlobalJobListener(listener);
  }
  
  /**
   * @see org.quartz.Scheduler#addGlobalTriggerListener(org.quartz.TriggerListener)
   */
  public void addGlobalTriggerListener(TriggerListener listener)
      throws SchedulerException {
    _sched.addTriggerListener(listener);
  }
  
  /**
   * @see org.quartz.Scheduler#addJob(org.quartz.JobDetail, boolean)
   */
  public void addJob(JobDetail detail, boolean replace) throws SchedulerException {
    _sched.addJob(detail, replace);
  }
  
  /**
   * @see org.quartz.Scheduler#addJobListener(org.quartz.JobListener)
   */
  public void addJobListener(JobListener listener) throws SchedulerException {
    _sched.addJobListener(listener);
  }
  
  /**
   * @see org.quartz.Scheduler#addSchedulerListener(org.quartz.SchedulerListener)
   */
  public void addSchedulerListener(SchedulerListener listener)
      throws SchedulerException {
    _sched.addSchedulerListener(listener);
  }
  
  /**
   * @see org.quartz.Scheduler#addTriggerListener(org.quartz.TriggerListener)
   */
  public void addTriggerListener(TriggerListener listener)
      throws SchedulerException {
    _sched.addTriggerListener(listener);
  }
  
  /* (non-Javadoc)
   * @see org.quartz.Scheduler#setJobFactory(org.quartz.spi.JobFactory)
   */
  public void setJobFactory(JobFactory aFacotry) throws SchedulerException {
    _sched.setJobFactory(aFacotry);
  }

  /**
   * @see org.quartz.Scheduler#deleteCalendar(java.lang.String)
   */
  public boolean deleteCalendar(String name) throws SchedulerException {
    return _sched.deleteCalendar(name);
  }
  
  /**
   * @see org.quartz.Scheduler#deleteJob(java.lang.String, java.lang.String)
   */
  public boolean deleteJob(String name, String groupName) throws SchedulerException {
    return _sched.deleteJob(name, groupName);
  }
  
  /**
   * @see org.quartz.Scheduler#getCalendar(java.lang.String)
   */
  public Calendar getCalendar(String name) throws SchedulerException {
    return _sched.getCalendar(name);
  }
  
  /**
   * @see org.quartz.Scheduler#getCalendarNames()
   */
  public String[] getCalendarNames() throws SchedulerException {
      return _sched.getCalendarNames();
  }
  
  /**
   * @see org.quartz.Scheduler#getContext()
   */
  public SchedulerContext getContext() throws SchedulerException {
    return _sched.getContext();
  }
  
  /**
   * @see org.quartz.Scheduler#getCurrentlyExecutingJobs()
   */
  public List getCurrentlyExecutingJobs() throws SchedulerException {
    return _sched.getCurrentlyExecutingJobs();
  }
  
  /**
   * @see org.quartz.Scheduler#getGlobalJobListeners()
   */
  public List getGlobalJobListeners() throws SchedulerException {
    return _sched.getGlobalJobListeners();
  }
  
  /**
   * @see org.quartz.Scheduler#getGlobalTriggerListeners()
   */
  public List getGlobalTriggerListeners() throws SchedulerException {
    return _sched.getGlobalTriggerListeners();
  }
  
  /**
   * @see org.quartz.Scheduler#getJobDetail(java.lang.String, java.lang.String)
   */
  public JobDetail getJobDetail(String name, String group)
      throws SchedulerException {
    return _sched.getJobDetail(name, group);
  }
  
  /**
   * @see org.quartz.Scheduler#getJobGroupNames()
   */
  public String[] getJobGroupNames() throws SchedulerException {
    return _sched.getJobGroupNames();
  }
  
  /**
   * @see org.quartz.Scheduler#getJobListener(java.lang.String)
   */
  public JobListener getJobListener(String name) throws SchedulerException {
    return _sched.getJobListener(name);
  }
  
  /**
   * @see org.quartz.Scheduler#getJobListenerNames()
   */
  public Set getJobListenerNames() throws SchedulerException {
    return _sched.getJobListenerNames();
  }
  
  /**
   * @see org.quartz.Scheduler#getJobNames(java.lang.String)
   */
  public String[] getJobNames(String group) throws SchedulerException {
    return _sched.getJobNames(group);
  }
  
  /**
   * @see org.quartz.Scheduler#getMetaData()
   */
  public SchedulerMetaData getMetaData() throws SchedulerException {
    return _sched.getMetaData();
  }
  
  /**
   * @see org.quartz.Scheduler#getPausedTriggerGroups()
   */
  public Set getPausedTriggerGroups() throws SchedulerException {
    return _sched.getPausedTriggerGroups();
  }
  
  /**
   * @see org.quartz.Scheduler#getSchedulerInstanceId()
   */
  public String getSchedulerInstanceId() throws SchedulerException {
    return _sched.getSchedulerInstanceId();
  }
  
  /**
   * @see org.quartz.Scheduler#getSchedulerListeners()
   */
  public List getSchedulerListeners() throws SchedulerException {
    return _sched.getSchedulerListeners();
  }
  
  /**
   * @see org.quartz.Scheduler#getSchedulerName()
   */
  public String getSchedulerName() throws SchedulerException {
    return _sched.getSchedulerName();
  }
  
  /**
   * @see org.quartz.Scheduler#getTrigger(java.lang.String, java.lang.String)
   */
  public Trigger getTrigger(String name, String group) throws SchedulerException {
    return _sched.getTrigger(name, group);
  }

  /**
   * @see org.quartz.Scheduler#getTriggerGroupNames()
   */
  public String[] getTriggerGroupNames() throws SchedulerException {
    return _sched.getTriggerGroupNames();
  }
  
  /**
   * @see org.quartz.Scheduler#getTriggerListener(java.lang.String)
   */
  public TriggerListener getTriggerListener(String name)
      throws SchedulerException {
    return _sched.getTriggerListener(name);
  }
  
  /**
   * @see org.quartz.Scheduler#getTriggerListenerNames()
   */
  public Set getTriggerListenerNames() throws SchedulerException {
    return _sched.getTriggerListenerNames();
  }
  
  /**
   * @see org.quartz.Scheduler#getTriggerNames(java.lang.String)
   */
  public String[] getTriggerNames(String group) throws SchedulerException {
    return _sched.getTriggerNames(group);
  }
  
  /**
   * @see org.quartz.Scheduler#getTriggersOfJob(java.lang.String, java.lang.String)
   */
  public Trigger[] getTriggersOfJob(String name, String group)
      throws SchedulerException {
    return _sched.getTriggersOfJob(name, group);
  }
  
  /**
   * @see org.quartz.Scheduler#getTriggerState(java.lang.String, java.lang.String)
   */
  public int getTriggerState(String name, String group)
      throws SchedulerException {
    return _sched.getTriggerState(name, group);
  }
  
  /**
   * @see org.quartz.Scheduler#interrupt(java.lang.String, java.lang.String)
   */
  public boolean interrupt(String name, String group)
      throws UnableToInterruptJobException {
    return _sched.interrupt(name, group);
  }
  
  /**
   * @see org.quartz.Scheduler#isInStandbyMode()
   */
  public boolean isInStandbyMode() throws SchedulerException {
    return _sched.isInStandbyMode();
  }
  
  /**
   * @see org.quartz.Scheduler#isPaused()
   */
  public boolean isPaused() throws SchedulerException {
    // _sched.isPaused() is now deprecated - replaced with stanby
    return _sched.isInStandbyMode();
  }
  
  /**
   * @see org.quartz.Scheduler#isShutdown()
   */
  public boolean isShutdown() throws SchedulerException {
    return _sched.isShutdown();
  }
  
  /**
   * @see org.quartz.Scheduler#pause()
   */
  public void pause() throws SchedulerException {
    // _sched.pause() is now deprecated - replaced with stanby
    _sched.standby();
  }
  
  /**
   * @see org.quartz.Scheduler#standby()
   */
  public void standby() throws SchedulerException {
    _sched.standby();
  }
  
  /**
   * @see org.quartz.Scheduler#pauseAll()
   */
  public void pauseAll() throws SchedulerException {
    _sched.pauseAll();
  }
  
  /**
   * @see org.quartz.Scheduler#pauseJob(java.lang.String, java.lang.String)
   */  
  public void pauseJob(String name, String group) throws SchedulerException {
    _sched.pauseJob(name, group);
  }
  
  /**
   * @see org.quartz.Scheduler#pauseJobGroup(java.lang.String)
   */
  public void pauseJobGroup(String name) throws SchedulerException {
    _sched.pauseJobGroup(name);
  }
  
  /**
   * @see org.quartz.Scheduler#pauseTrigger(java.lang.String, java.lang.String)
   */
  public void pauseTrigger(String name, String group) throws SchedulerException {
    _sched.pauseTrigger(name, group);
  }
  
  /**
   * @see org.quartz.Scheduler#pauseTriggerGroup(java.lang.String)
   */
  public void pauseTriggerGroup(String name) throws SchedulerException {
    _sched.pauseTriggerGroup(name);
  }
  
  /**
   * @see org.quartz.Scheduler#removeGlobalJobListener(org.quartz.JobListener)
   */
  public boolean removeGlobalJobListener(JobListener listener)
      throws SchedulerException {
    return _sched.removeGlobalJobListener(listener);
  }
  
  /**
   * @see org.quartz.Scheduler#removeGlobalTriggerListener(org.quartz.TriggerListener)
   */
  public boolean removeGlobalTriggerListener(TriggerListener listener)
      throws SchedulerException {
    return _sched.removeGlobalTriggerListener(listener);
  }
  
  /**
   * @see org.quartz.Scheduler#removeJobListener(java.lang.String)
   */
  public boolean removeJobListener(String name) throws SchedulerException {
    return _sched.removeJobListener(name);
  }
  
  /**
   * @see org.quartz.Scheduler#removeSchedulerListener(org.quartz.SchedulerListener)
   */
  public boolean removeSchedulerListener(SchedulerListener listener)
      throws SchedulerException {
    return _sched.removeSchedulerListener(listener);
  }
  
  /**
   * @see org.quartz.Scheduler#removeTriggerListener(java.lang.String)
   */
  public boolean removeTriggerListener(String name) throws SchedulerException {
    return _sched.removeTriggerListener(name);
  }
  
  /**
   * @see org.quartz.Scheduler#rescheduleJob(java.lang.String, java.lang.String, org.quartz.Trigger)
   */
  public Date rescheduleJob(String triggerName, String group, Trigger newTrigger)
      throws SchedulerException {
    return _sched.rescheduleJob(triggerName, group, newTrigger);
  }
  
  /**
   * @see org.quartz.Scheduler#resumeAll()
   */
  public void resumeAll() throws SchedulerException {
    _sched.resumeAll();
  }
  
  /**
   * @see org.quartz.Scheduler#resumeJob(java.lang.String, java.lang.String)
   */
  public void resumeJob(String name, String group) throws SchedulerException {
    _sched.resumeJob(name, group);
  }
  
  /**
   * @see org.quartz.Scheduler#resumeJobGroup(java.lang.String)
   */
  public void resumeJobGroup(String group) throws SchedulerException {
    _sched.resumeJobGroup(group);  
  }

  /**
   * @see org.quartz.Scheduler#resumeTrigger(java.lang.String, java.lang.String)
   */
  public void resumeTrigger(String triggerName, String group) throws SchedulerException {
    _sched.resumeTrigger(triggerName, group);
  }
  
  /**
   * @see org.quartz.Scheduler#resumeTriggerGroup(java.lang.String)
   */
  public void resumeTriggerGroup(String group) throws SchedulerException {
    _sched.resumeTriggerGroup(group);
  }

  /**
   * @see org.quartz.Scheduler#scheduleJob(org.quartz.JobDetail, org.quartz.Trigger)
   */
  public Date scheduleJob(JobDetail detail, Trigger trigger)
      throws SchedulerException {
    return _sched.scheduleJob(detail, trigger);
  }
  
  /**
   * @see org.quartz.Scheduler#scheduleJob(org.quartz.Trigger)
   */
  public Date scheduleJob(Trigger trigger) throws SchedulerException {
    return _sched.scheduleJob(trigger);
  }
  
  /**
   * This method has an empty implementation. This instance is rather shut
   * down by calling <code>dispose()</code>.
   * 
   * @see #dispose()
   * @see org.quartz.Scheduler#shutdown()
   */
  public void shutdown() throws SchedulerException {
  }
  
  /**
   * This method has an empty implementation. This instance is rather shut
   * down by calling <code>dispose()</code>.
   * 
   * @see #dispose()
   * @see org.quartz.Scheduler#shutdown(boolean)
   */
  public void shutdown(boolean waitForCompletion) throws SchedulerException {
  }

  /**
   * @see org.quartz.Scheduler#triggerJob(java.lang.String, java.lang.String)
   */
  public void triggerJob(String name, String group) throws SchedulerException {
    _sched.triggerJob(name, group);
  }
  

  /* (non-Javadoc)
   * @see org.quartz.Scheduler#triggerJob(java.lang.String, java.lang.String, org.quartz.JobDataMap)
   */
  public void triggerJob(String name, String group, JobDataMap jobData) throws SchedulerException {
    _sched.triggerJob(name, group, jobData);
  }

  /**
   * @see org.quartz.Scheduler#triggerJobWithVolatileTrigger(java.lang.String, java.lang.String)
   */
  public void triggerJobWithVolatileTrigger(String name, String group) throws SchedulerException {
    _sched.triggerJobWithVolatileTrigger(name, group);
  }

  /* (non-Javadoc)
   * @see org.quartz.Scheduler#triggerJobWithVolatileTrigger(java.lang.String, java.lang.String, org.quartz.JobDataMap)
   */
  public void triggerJobWithVolatileTrigger(String name, String group, JobDataMap jobData) throws SchedulerException {
    _sched.triggerJobWithVolatileTrigger(name, group, jobData);
  }  
  
  /**
   * @see org.quartz.Scheduler#unscheduleJob(java.lang.String, java.lang.String)
   */
  public boolean unscheduleJob(String name, String group) throws SchedulerException {
    return _sched.unscheduleJob(name, group);
  }
}
