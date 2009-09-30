package org.sapia.corus.taskmanager.core;

import org.apache.log.Hierarchy;
import org.sapia.corus.ServerContext;
import org.sapia.corus.taskmanager.core.TaskManagerImpl;

public class TestTaskManager extends TaskManagerImpl{
  
  public TestTaskManager(ServerContext ctx) {
    super(Hierarchy.getDefaultHierarchy().getLoggerFor("taskmanager"), 
          ctx);
  }

}
