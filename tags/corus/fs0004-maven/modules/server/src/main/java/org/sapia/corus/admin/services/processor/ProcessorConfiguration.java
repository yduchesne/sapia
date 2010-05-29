package org.sapia.corus.admin.services.processor;


public interface ProcessorConfiguration {

  public long getProcessTimeoutMillis();
  
  public long getProcessCheckIntervalMillis();

  public long getKillIntervalMillis();

  public long getStartIntervalMillis();

  public long getRestartIntervalMillis();

  public long getExecIntervalMillis();

  public long getBootExecDelayMillis();

}