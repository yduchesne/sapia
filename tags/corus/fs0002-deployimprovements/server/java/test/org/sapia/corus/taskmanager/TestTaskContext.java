package org.sapia.corus.taskmanager;

import org.sapia.taskman.TaskContext;
import org.sapia.taskman.TaskManager;
import org.sapia.taskman.TaskOutput;

/**
 * @author Yanick Duchesne
 */
public class TestTaskContext extends TaskContext{
  
  public TestTaskContext(TaskOutput out, TaskManager mgr){
    super(out, mgr);
  }
}
