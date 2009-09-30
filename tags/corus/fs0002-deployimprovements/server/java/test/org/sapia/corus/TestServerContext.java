package org.sapia.corus;

import org.sapia.corus.admin.services.configurator.Configurator;
import org.sapia.corus.admin.services.deployer.Deployer;
import org.sapia.corus.admin.services.port.PortManager;
import org.sapia.corus.admin.services.processor.Processor;
import org.sapia.corus.configurator.TestConfigurator;
import org.sapia.corus.deployer.TestDeployer;
import org.sapia.corus.port.TestPortManager;
import org.sapia.corus.processor.TestProcessor;
import org.sapia.corus.processor.task.TestProcessorTaskStrategy;
import org.sapia.corus.processor.task.v2.ProcessorTaskStrategy;
import org.sapia.corus.server.deployer.DistributionDatabase;
import org.sapia.corus.server.processor.ProcessRepository;
import org.sapia.corus.taskmanager.v2.TaskManagerV2;
import org.sapia.corus.taskmanager.v2.TestTaskManagerV2;
import org.sapia.ubik.net.TCPAddress;

public class TestServerContext extends ServerContext{
  
  public TestServerContext() {
    super(
        new TCPAddress("localhost", 33000), 
        "test", 
        "home",
        new InternalServiceContext());
  }
  
  public static TestServerContext create(){
    TestDeployer      depl  = new TestDeployer();
    TestProcessor     proc  = new TestProcessor();
    TestPortManager   ports = new TestPortManager();
    TestConfigurator  tc    = new TestConfigurator();
    TestServerContext ctx   = new TestServerContext();
    TestTaskManagerV2 tm    = new TestTaskManagerV2(ctx);
    TestProcessorTaskStrategy procStrat = new TestProcessorTaskStrategy();

    ctx.getServices().bind(Deployer.class, depl);
    ctx.getServices().bind(Processor.class, proc);
    ctx.getServices().bind(ProcessorTaskStrategy.class, procStrat);
    ctx.getServices().bind(PortManager.class, ports);
    ctx.getServices().bind(TaskManagerV2.class, tm);
    ctx.getServices().bind(Configurator.class, tc);
    ctx.getServices().bind(ProcessRepository.class, proc.getProcessRepository());
    ctx.getServices().bind(DistributionDatabase.class, depl.getDistributionDatabase());
    
    return ctx;
  }
}
