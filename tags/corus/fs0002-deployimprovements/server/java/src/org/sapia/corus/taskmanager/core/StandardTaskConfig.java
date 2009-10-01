package org.sapia.corus.taskmanager.core;

public class StandardTaskConfig extends TaskConfig {
  
  private TaskListener listener;

  void setListener(TaskListener listener) {
    this.listener = listener;
  }
  
  public TaskListener getListener() {
    return listener;
  }
}
