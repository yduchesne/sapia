package org.sapia.corus;

import org.sapia.corus.client.services.configurator.Configurator;
import org.sapia.corus.client.services.configurator.InternalConfigurator;
import org.sapia.corus.client.services.deployer.Deployer;
import org.sapia.corus.client.services.event.EventDispatcher;
import org.sapia.corus.client.services.file.FileSystemModule;
import org.sapia.corus.client.services.os.OsModule;
import org.sapia.corus.client.services.port.PortManager;
import org.sapia.corus.client.services.processor.Processor;
import org.sapia.corus.configurator.TestConfigurator;
import org.sapia.corus.core.InternalServiceContext;
import org.sapia.corus.core.ServerContextImpl;
import org.sapia.corus.deployer.DeployerThrottleKeys;
import org.sapia.corus.deployer.DistributionDatabase;
import org.sapia.corus.deployer.TestDeployer;
import org.sapia.corus.event.TestDispatcher;
import org.sapia.corus.file.TestFileSystemModule;
import org.sapia.corus.os.TestOsModule;
import org.sapia.corus.port.TestPortManager;
import org.sapia.corus.processor.ProcessRepository;
import org.sapia.corus.processor.ProcessorThrottleKeys;
import org.sapia.corus.processor.TestProcessor;
import org.sapia.corus.taskmanager.core.TaskManager;
import org.sapia.corus.taskmanager.core.TestTaskManager;
import org.sapia.corus.taskmanager.core.Throttle;
import org.sapia.corus.taskmanager.core.ThrottleFactory;
import org.sapia.ubik.net.TCPAddress;

public class TestServerContext extends ServerContextImpl{

  private TestDispatcher        _disp;
  private TestDeployer          _depl;
  private TestProcessor         _proc;
  private TestPortManager       _ports;
  private TestConfigurator      _tc;
  private TestTaskManager       _tm;
  private TestFileSystemModule  _fs;
  private TestOsModule          _os;
  
  public static TestServerContext create() {
    TestServerContext created = new TestServerContext();

    created._disp  = new TestDispatcher();
    created._depl  = new TestDeployer();
    created._proc  = new TestProcessor();
    created._ports = new TestPortManager();
    created._tc    = new TestConfigurator();
    created._tm    = new TestTaskManager(created);
    created._fs    = new TestFileSystemModule();
    created._os    = new TestOsModule();
    
    created.getServices().bind(EventDispatcher.class, created._disp);
    created.getServices().bind(Deployer.class, created._depl);
    created.getServices().bind(DistributionDatabase.class, created._depl.getDistributionDatabase());
    created.getServices().bind(Processor.class, created._proc);
    created.getServices().bind(PortManager.class, created._ports);
    created.getServices().bind(TaskManager.class, created._tm);
    created.getServices().bind(Configurator.class, created._tc);
    created.getServices().bind(InternalConfigurator.class, created._tc);
    created.getServices().bind(ProcessRepository.class, created._proc.getProcessRepository());
    created.getServices().bind(FileSystemModule.class, created._fs);
    created.getServices().bind(OsModule.class, created._os);
    
    registerThrottles(created);
    
    return created;
  }
  
  private static void registerThrottles(TestServerContext ctx){
    
    Throttle throttle = new Throttle() {
      @Override
      public void execute(Runnable toRun) {
        toRun.run();
      }
    };
    
    ctx.getTm().registerThrottle(ProcessorThrottleKeys.PROCESS_EXEC, throttle);
    ctx.getTm().registerThrottle(DeployerThrottleKeys.DEPLOY_DISTRIBUTION, throttle);
    ctx.getTm().registerThrottle(DeployerThrottleKeys.UNDEPLOY_DISTRIBUTION, throttle);
  }
  

  public TestServerContext() {
    super(
        null,
        null, 
        new TCPAddress("localhost", 33000), 
        "test", 
        System.getProperty("user.dir"),
        new InternalServiceContext());
  }

  /**
   * Returns the disp attribute.
   *
   * @return The disp value.
   */
  public TestDispatcher getDisp() {
    return _disp;
  }

  /**
   * Returns the depl attribute.
   *
   * @return The depl value.
   */
  public TestDeployer getDepl() {
    return _depl;
  }

  /**
   * Returns the proc attribute.
   *
   * @return The proc value.
   */
  public TestProcessor getProc() {
    return _proc;
  }

  /**
   * Returns the ports attribute.
   *
   * @return The ports value.
   */
  public TestPortManager getPorts() {
    return _ports;
  }

  /**
   * Returns the tc attribute.
   *
   * @return The tc value.
   */
  public TestConfigurator getTc() {
    return _tc;
  }

  /**
   * Returns the tm attribute.
   *
   * @return The tm value.
   */
  public TestTaskManager getTm() {
    return _tm;
  }
  
}