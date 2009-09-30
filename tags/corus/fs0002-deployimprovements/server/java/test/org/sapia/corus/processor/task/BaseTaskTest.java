package org.sapia.corus.processor.task;

import org.sapia.corus.TestServerContext;
import org.sapia.corus.processor.task.action.ActionFactory;
import org.sapia.corus.server.processor.ProcessRepository;
import org.sapia.corus.taskmanager.TaskManager;
import org.sapia.corus.taskmanager.v2.TaskManagerV2;

import junit.framework.TestCase;

/**
 * @author Yanick Duchesne
 */
public class BaseTaskTest extends TestCase{
  
  protected TestServerContext ctx;
  protected ProcessRepository db;
  protected TaskManagerV2 tm;
  
  /**
   * @param arg0
   */
  public BaseTaskTest(String arg0) {
    super(arg0);
  }
  
  /**
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp() throws Exception {
    ctx = TestServerContext.create();
    db = ctx.getServices().getProcesses();
    tm = ctx.getServices().lookup(TaskManagerV2.class);
  }
  
  public void testNoop(){}

}
