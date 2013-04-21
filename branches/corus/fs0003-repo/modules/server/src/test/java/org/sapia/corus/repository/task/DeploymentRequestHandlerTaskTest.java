package org.sapia.corus.repository.task;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.sapia.corus.client.services.cluster.ClusterNotification;
import org.sapia.corus.client.services.cluster.Endpoint;
import org.sapia.corus.client.services.processor.ExecConfig;
import org.sapia.corus.processor.ExecConfigDatabase;
import org.sapia.ubik.net.ServerAddress;
import org.sapia.ubik.util.Collections2;


public class DeploymentRequestHandlerTaskTest extends AbstractRepoTaskTest {
  
  private TestDeploymentRequestHandlerTask task;
  private List<Endpoint>                   targets;
  private Set<String>                      tags;
  private Properties                       processProperties;
  private ExecConfigDatabase               configDb;
  private List<ExecConfig>                 execConfigs;
  
  @Before
  public void setUp() throws Exception {
    super.doSetUp();
    tags = Collections2.arrayToSet("tag1", "tag2");
    
    processProperties = new Properties();
    processProperties.setProperty("prop1", "val1");
    processProperties.setProperty("prop2", "val2");
  
    configDb = mock(ExecConfigDatabase.class);
    execConfigs = Collections2.arrayToList(new ExecConfig());
    
    Endpoint ep = new Endpoint(mock(ServerAddress.class), mock(ServerAddress.class));
    targets     = Collections2.arrayToList(ep);
    
    task = new TestDeploymentRequestHandlerTask(new File("test"), targets);
    
    when(configurator.getTags()).thenReturn(tags);
    when(serverContext.getProcessProperties()).thenReturn(processProperties);
    when(configDb.getConfigs()).thenReturn(execConfigs);
    when(serviceContext.getExecConfigs()).thenReturn(configDb);
  }

  @Test
  public void testExecuteWithEmptyTargets() throws Throwable {
    targets.clear();
    task.execute(taskContext, null);
    assertFalse(task.deploy);
    verify(cluster, never()).send(any(ClusterNotification.class));
  }

  @Test
  public void testExecuteWithTargets() throws Throwable {
    task.execute(taskContext, null);
    assertTrue(task.deploy);
  }
  
  // ==========================================================================
  
  static class TestDeploymentRequestHandlerTask extends DeploymentRequestHandlerTask {
    
    boolean deploy;
    
    public TestDeploymentRequestHandlerTask(File distFile, List<Endpoint> targets) {
      super(distFile, targets);
    }
    
    @Override
    void doDeployDistribution() throws IOException {
      deploy = true;
    }
    
  }

}
