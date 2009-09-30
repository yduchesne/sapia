package org.sapia.corus.taskmanager;

import org.sapia.taskman.TaskOutput;

/**
 * @author Yanick Duchesne
 */
public class TestTaskOutput implements TaskOutput{

  public void close() {
  }

  public TaskOutput debug(Object arg0) {
    return this;
  }

  public TaskOutput error(Object arg0, Throwable arg1) {
    return this;
  }

  public TaskOutput error(Object arg0) {
    return this;
  }

  public TaskOutput error(Throwable arg0) {
    return this;
  }

  public TaskOutput info(Object arg0) {
    return this;
  }

  public void setTaskName(String arg0) {}

  public TaskOutput warning(Object arg0) {
    return this;
  }

}
