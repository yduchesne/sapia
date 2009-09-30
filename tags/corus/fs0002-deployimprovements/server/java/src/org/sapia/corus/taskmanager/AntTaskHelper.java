package org.sapia.corus.taskmanager;

import org.apache.tools.ant.Project;
import org.sapia.corus.taskmanager.core.TaskExecutionContext;
import org.sapia.corus.taskmanager.core.Task;


/**
 * Wraps an Ant task in a {@link Task} instance.
 * 
 * @author Yanick Duchesne
 */
public class AntTaskHelper {
  
  public static Task init(org.apache.tools.ant.Task antTask) {
    return new DynAntTask(antTask);
  }

  static class DynAntTask extends Task{
    private org.apache.tools.ant.Task _antTask;

    DynAntTask(org.apache.tools.ant.Task antTask) {
      _antTask = antTask;
    }
    
    @Override
    public Object execute(TaskExecutionContext ctx) throws Throwable {
      try {
        Project p = new Project();
        p.addBuildListener(new AntTaskLog(this, ctx.getLog()));
        _antTask.setProject(p);
        _antTask.execute();
      } catch (Throwable t) {
        ctx.error(t);
      }
      return null;
    }
  }
}
