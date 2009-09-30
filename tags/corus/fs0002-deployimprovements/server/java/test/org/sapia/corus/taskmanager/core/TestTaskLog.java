package org.sapia.corus.taskmanager.core;

import org.sapia.corus.taskmanager.core.TaskLog;
import org.sapia.corus.taskmanager.core.Task;

public class TestTaskLog implements TaskLog{

  public void debug(Task task, String msg) {
  }

  public void error(Task task, String msg, Throwable err) {
  }

  public void error(Task task, String msg) {
  }

  public void info(Task task, String msg) {
  }

  public void warn(Task task, String msg, Throwable err) {
  }

  public void warn(Task task, String msg) {
  }

  public void close() {
  }
}
