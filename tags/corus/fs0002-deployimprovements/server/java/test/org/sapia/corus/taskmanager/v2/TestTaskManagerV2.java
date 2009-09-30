package org.sapia.corus.taskmanager.v2;

import org.apache.log.Hierarchy;
import org.sapia.corus.ServerContext;

public class TestTaskManagerV2 extends TaskManagerV2Impl{
  
  public TestTaskManagerV2(ServerContext ctx) {
    super(Hierarchy.getDefaultHierarchy().getLoggerFor("taskmanager"), 
          ctx);
  }

}
