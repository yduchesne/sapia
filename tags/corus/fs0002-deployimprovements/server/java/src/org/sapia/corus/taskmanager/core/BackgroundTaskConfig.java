package org.sapia.corus.taskmanager.core;

public class BackgroundTaskConfig extends TaskConfig{

  private BackgroundTaskListener listener;

  void setListener(BackgroundTaskListener listener) {
    this.listener = listener;
  }
  
  public BackgroundTaskListener getListener() {
    return listener;
  }

}
