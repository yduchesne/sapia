package org.sapia.corus.admin.services.processor;

import java.io.Serializable;

public class ProcessorConfigurationImpl implements Serializable, ProcessorConfiguration {
  
  static final long serialVersionUID = 1L;
  
  /**
   * This constant specifies the default "process timeout" - delay (in seconds)
   * after which a process is considered idled and must be restarted.
   */
  public static final int DEFAULT_PROCESS_TIMEOUT = 25;

  /**
   * This constant specifies the default interval (in seconds) at which
   * process status is checked.
   */
  public static final int DEFAULT_CHECK_INTERVAL = 15;

  /**
   * This constant specifies the default interval (in seconds) at which
   * kill attempts occur.
   */
  public static final int DEFAULT_KILL_INTERVAL = 15;
  
  /**
   * This constant specifies the amount of time (in seconds) to wait
   * between process startups.
   */
  public static final int DEFAULT_START_INTERVAL = 15;  

  /**
   * This constant specifies the minimum amount of time (in
   * seconds) required between two startups for the second one
   * to be authorized; value is 120 (seconds).
   */
  public static int DEFAULT_RESTART_INTERVAL   = 120;
  
  public static int DEFAULT_EXEC_TASK_INTERVAL = 10;

  public static int DEFAULT_BOOT_EXEC_DELAY = 30;

  
  private int processTimeout = DEFAULT_PROCESS_TIMEOUT;
  private int processCheckInterval = DEFAULT_CHECK_INTERVAL;
  private int killInterval = DEFAULT_KILL_INTERVAL;
  private int startInterval = DEFAULT_START_INTERVAL;
  private int restartInterval = DEFAULT_RESTART_INTERVAL;
  private int execInterval = DEFAULT_EXEC_TASK_INTERVAL;
  private int bootExecDelay = DEFAULT_BOOT_EXEC_DELAY;
  
  public long getProcessTimeoutMillis(){
    return processTimeout * 1000;
  }
  
  public void setProcessTimeout(int processTimeout) {
    this.processTimeout = processTimeout;
  }
  
  public int getProcessCheckInterval() {
    return processCheckInterval;
  }

  public void setProcessCheckInterval(int processCheckInterval) {
    this.processCheckInterval = processCheckInterval;
  }

  public long getProcessCheckIntervalMillis(){
    return processCheckInterval * 1000;
  }
  
  public int getKillInterval() {
    return killInterval;
  }
  
  public long getKillIntervalMillis(){
    return killInterval * 1000;
  }

  public void setKillInterval(int killInterval) {
    this.killInterval = killInterval;
  }
  
  public int getStartInterval() {
    return startInterval;
  }

  public long getStartIntervalMillis(){
    return this.startInterval * 1000;
  }

  public void setStartInterval(int startInterval) {
    this.startInterval = startInterval;
  }
  
  public int getRestartInterval() {
    return restartInterval;
  }

  public long getRestartIntervalMillis(){
    return this.restartInterval * 1000;
  }
  
  public void setRestartInterval(int restartInterval) {
    this.restartInterval = restartInterval;
  }
  
  public int getExecInterval() {
    return execInterval;
  }

  public long getExecIntervalMillis(){
    return execInterval * 1000;
  }
  
  public void setExecInterval(int execInterval) {
    this.execInterval = execInterval;
  }
  
  public int getBootExecDelay() {
    return bootExecDelay;
  }
  
  public long getBootExecDelayMillis() {
    return bootExecDelay * 1000;
  }

  public void setBootExecDelay(int bootExecDelay) {
    this.bootExecDelay = bootExecDelay;
  }
  
}
