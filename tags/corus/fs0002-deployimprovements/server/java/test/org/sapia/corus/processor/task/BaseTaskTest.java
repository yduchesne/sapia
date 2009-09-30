package org.sapia.corus.processor.task;

import junit.framework.TestCase;

import org.sapia.corus.TestServerContext;
import org.sapia.corus.admin.services.processor.Processor;
import org.sapia.corus.admin.services.processor.ProcessorConfigurationImpl;
import org.sapia.corus.server.processor.ProcessRepository;
import org.sapia.corus.taskmanager.v2.TaskManagerV2;

/**
 * @author Yanick Duchesne
 */
public class BaseTaskTest extends TestCase{
  
  protected TestServerContext ctx;
  protected ProcessRepository db;
  protected TaskManagerV2 tm;
  protected Processor processor;
  protected ProcessorConfigurationImpl processorConf;
  
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
    tm = ctx.lookup(TaskManagerV2.class);
    processor = ctx.lookup(Processor.class);
    processorConf = (ProcessorConfigurationImpl)processor.getConfiguration();
  }
  
  public void testNoop(){}

}
