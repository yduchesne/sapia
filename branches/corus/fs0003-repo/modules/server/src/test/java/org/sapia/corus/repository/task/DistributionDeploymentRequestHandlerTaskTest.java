package org.sapia.corus.repository.task;

import static org.junit.Assert.*;

import static org.mockito.Mockito.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.sapia.corus.client.services.cluster.Endpoint;
import org.sapia.corus.client.services.repository.DistributionDeploymentRequest;
import org.sapia.corus.client.services.repository.RepoDistribution;
import org.sapia.corus.deployer.InternalDeployer;
import org.sapia.corus.taskmanager.core.Task;
import org.sapia.corus.util.Queue;
import org.sapia.ubik.net.ServerAddress;

public class DistributionDeploymentRequestHandlerTaskTest extends AbstractRepoTaskTest {
  
  private DistributionDeploymentRequest            request1, request2;
  private RepoDistribution                         dist1, dist2;
  private Queue<DistributionDeploymentRequest>     requests;
  private InternalDeployer                         internalDeployer;
  private DistributionDeploymentRequestHandlerTask task;
  
  @Before
  public void setUp() throws Exception {
    super.doSetUp(); 
    
    internalDeployer = mock(InternalDeployer.class);
    
    Endpoint ep = new Endpoint(mock(ServerAddress.class), mock(ServerAddress.class));
    
    dist1 = new RepoDistribution("dist1", "1.0");
    dist2 = new RepoDistribution("dist2", "1.0");
    
    request1  = new DistributionDeploymentRequest(ep);
    request1.addDistribution(dist1);
    
    request2  = new DistributionDeploymentRequest(ep);
    request2.addDistribution(dist2);
    
    requests = new Queue<DistributionDeploymentRequest>();
    requests.add(request1);
    requests.add(request2);
    
    task = new DistributionDeploymentRequestHandlerTask(requests);
    
    when(serverContext.lookup(eq(InternalDeployer.class))).thenReturn(internalDeployer);
    when(internalDeployer.getDistributionFile(anyString(), anyString())).thenReturn(new File("test"));
    
  }
  
  @Test
  public void testExecuteWithMultipleDistributions() throws Throwable {
    
    final AtomicReference<PerformDeploymentTask> expected = new AtomicReference<PerformDeploymentTask>();
    
    doAnswer(new Answer<Void>() {
      @Override
      public Void answer(InvocationOnMock invocation) throws Throwable {
        PerformDeploymentTask task = (PerformDeploymentTask) invocation.getArguments()[0];
        expected.set(task);
        return null;
      }
    }).when(taskMan).execute(any(Task.class), any(Void.class));

    task.execute(taskContext, null);

    assertEquals(2, expected.get().getChildTasks().size());
  }
  
  @Test
  public void testExecuteWithSingleDistribution() throws Throwable {
    
    requests.removeLast();
    
    final AtomicReference<PerformDeploymentTask> expected = new AtomicReference<PerformDeploymentTask>();
    
    doAnswer(new Answer<Void>() {
      @Override
      public Void answer(InvocationOnMock invocation) throws Throwable {
        PerformDeploymentTask task = (PerformDeploymentTask) invocation.getArguments()[0];
        expected.set(task);
        return null;
      }
    }).when(taskMan).execute(any(Task.class), any(Void.class));

    task.execute(taskContext, null);

    assertEquals(1, expected.get().getChildTasks().size());
  }

  @Test
  public void testExecuteEmptyDistributions() throws Throwable {
    requests.removeAll();
    task.execute(taskContext, null);

    verify(taskMan, never()).execute(any(Task.class), any(Void.class));
  }
  
  @Test
  public void testGetDistributionTargets() {
    Map<RepoDistribution, Set<Endpoint>> targets = task.getDistributionTargets(taskContext);

    assertTrue(targets.containsKey(dist1));
    assertTrue(targets.containsKey(dist2));
    assertTrue(targets.get(dist1).contains(request1.getEndpoint()));
    assertTrue(targets.get(dist2).contains(request2.getEndpoint()));
  }
}
