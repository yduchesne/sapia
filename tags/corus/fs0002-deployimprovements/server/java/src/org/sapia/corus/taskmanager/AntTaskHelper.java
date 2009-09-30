package org.sapia.corus.taskmanager;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.sapia.corus.taskmanager.v2.TaskExecutionContext;
import org.sapia.corus.taskmanager.v2.TaskV2;
import org.sapia.taskman.TaskContext;


/**
 * Wraps an Ant task in a <code>org.sapia.taskman.Task</code> instance.
 * 
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class AntTaskHelper {
  
  public static TaskV2 init(Task antTask) {
    return new DynAntTask(antTask);
  }

  static class DynAntTask extends TaskV2{
    private Task _antTask;

    DynAntTask(Task antTask) {
      _antTask = antTask;
    }
    
    @Override
    public Object execute(TaskExecutionContext ctx) throws Throwable {
      try {
        Project p = new Project();
        p.addBuildListener(new AntTaskoutput(this, ctx.getLog()));
        _antTask.setProject(p);
        _antTask.execute();
      } catch (Throwable t) {
        ctx.error(t);
      }
      return null;
    }
  }
}
