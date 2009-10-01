package org.sapia.corus.taskmanager.core;

public class Tasks {
  
  public static StandardTaskConfig createStandardConfig(){
    return new StandardTaskConfig();
  }

  public static StandardTaskConfig createStandardConfig(TaskListener listener){
    StandardTaskConfig c = createStandardConfig();
    c.setListener(listener);
    return c;
  }
  
  public static BackgroundTaskConfig createBackgroundTaskConfig(){
    return new BackgroundTaskConfig();
  }

  public static BackgroundTaskConfig createBackgroundTaskConfig(BackgroundTaskListener listener){
    BackgroundTaskConfig cfg = new BackgroundTaskConfig();
    cfg.setListener(listener);
    return cfg;
  }

}
