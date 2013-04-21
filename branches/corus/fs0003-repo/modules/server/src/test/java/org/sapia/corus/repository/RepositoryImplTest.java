package org.sapia.corus.repository;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anySet;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Properties;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.sapia.corus.client.services.cluster.ClusterManager;
import org.sapia.corus.client.services.cluster.ClusterNotification;
import org.sapia.corus.client.services.cluster.CorusHost;
import org.sapia.corus.client.services.cluster.CorusHost.RepoRole;
import org.sapia.corus.client.services.cluster.Endpoint;
import org.sapia.corus.client.services.configurator.Configurator;
import org.sapia.corus.client.services.configurator.Configurator.PropertyScope;
import org.sapia.corus.client.services.event.EventDispatcher;
import org.sapia.corus.client.services.processor.ExecConfig;
import org.sapia.corus.client.services.processor.Processor;
import org.sapia.corus.client.services.repository.ConfigNotification;
import org.sapia.corus.client.services.repository.DistributionDeploymentRequest;
import org.sapia.corus.client.services.repository.DistributionListRequest;
import org.sapia.corus.client.services.repository.DistributionListResponse;
import org.sapia.corus.client.services.repository.ExecConfigNotification;
import org.sapia.corus.core.ServerContext;
import org.sapia.corus.taskmanager.core.BackgroundTaskConfig;
import org.sapia.corus.taskmanager.core.Task;
import org.sapia.corus.taskmanager.core.TaskManager;
import org.sapia.corus.util.Queue;
import org.sapia.ubik.mcast.RemoteEvent;
import org.sapia.ubik.net.ServerAddress;
import org.sapia.ubik.rmi.server.transport.socket.TcpSocketAddress;
import org.sapia.ubik.util.Collections2;
import org.springframework.context.ApplicationContext;

public class RepositoryImplTest {
  
  private ClusterManager     cluster;
  private Configurator       config;
  private EventDispatcher    dispatcher;
  private Processor          processor;
  private TaskManager        tasks;
  private ApplicationContext appCtx;
  private ServerContext      serverCtx;
  private CorusHost          host;
  private RepositoryImpl     repo;
  private Set<CorusHost>     peers;
  private Queue<DistributionListRequest>       distListRequestQueue;
  private Queue<DistributionDeploymentRequest> distDeployRequestQueue;
  private int                corusPort;
  @Before
  public void setUp() throws Exception {
    repo = new RepositoryImpl();

    cluster    = mock(ClusterManager.class);
    config     = mock(Configurator.class);
    dispatcher = mock(EventDispatcher.class);
    processor  = mock(Processor.class);
    tasks      = mock(TaskManager.class);
    appCtx     = mock(ApplicationContext.class);
    serverCtx  = mock(ServerContext.class);
    distListRequestQueue   = mock(Queue.class);
    distDeployRequestQueue = mock(Queue.class);
    
    host       = createCorusHost(RepoRole.SERVER);
    
    peers = Collections2.arrayToSet(
        createCorusHost(RepoRole.CLIENT), 
        createCorusHost(RepoRole.CLIENT)
    );
    
    repo.setClusterManager(cluster);
    repo.setConfigurator(config);
    repo.setDispatcher(dispatcher);
    repo.setTaskManager(tasks);
    repo.setApplicationContext(appCtx);
    repo.setServerContext(serverCtx);
    repo.setDeployRequestQueue(distDeployRequestQueue);
    repo.setDistributionListRequestQueue(distListRequestQueue);
    
    when(serverCtx.getCorusHost()).thenReturn(host);
    when(cluster.getHosts()).thenReturn(peers);
  }
  
  private CorusHost createCorusHost(RepoRole repoRole) {
    
    CorusHost host = CorusHost.newInstance(new Endpoint(new TcpSocketAddress("test", corusPort++), new TcpSocketAddress("test", corusPort++)), "testOsInfo", "testVMInfo");
    host.setRepoRole(repoRole);
    return host;
  }
  
  @Test
  public void testPullForClientNode() {
    host.setRepoRole(RepoRole.CLIENT);
    repo.pull();
    verify(tasks).executeBackground(any(Task.class), any(Void.class), any(BackgroundTaskConfig.class)); 
  }
  
  @Test(expected = IllegalStateException.class)
  public void testPullForServerNode() {
    host.setRepoRole(RepoRole.SERVER);
    repo.pull();
    verify(tasks).executeBackground(any(Task.class), any(Void.class), any(BackgroundTaskConfig.class)); 
  }

  @Test(expected = IllegalStateException.class)
  public void testPullForUndefinedNode() {
    host.setRepoRole(RepoRole.NONE);
    repo.pull();
    verify(tasks).executeBackground(any(Task.class), any(Void.class), any(BackgroundTaskConfig.class)); 
  }
  
  @Test(expected = IllegalStateException.class)
  public void testPushForClientNode() throws Exception {
    host.setRepoRole(RepoRole.CLIENT);
    repo.push();
    verify(cluster, never()).send(any(ClusterNotification.class)); 
  }
  
  public void testPushForServerNode() throws Exception {
    host.setRepoRole(RepoRole.SERVER);
    repo.push();
    verify(cluster).send(any(ClusterNotification.class)); 
  }

  @Test(expected = IllegalStateException.class)
  public void testPushForUndefinedNode() throws Exception {
    host.setRepoRole(RepoRole.NONE);
    repo.push();
    verify(cluster, never()).send(any(ClusterNotification.class)); 
  }
  
  @Test
  public void testHandleDistributionListResponse() throws Exception {
    host.setRepoRole(RepoRole.CLIENT);
    DistributionListResponse res = new DistributionListResponse(createCorusHost(RepoRole.SERVER).getEndpoint());
    RemoteEvent event = new RemoteEvent(DistributionListResponse.EVENT_TYPE, res);
    repo.onAsyncEvent(event);
    verify(tasks).execute(any(Task.class), any(Void.class));
  }
  
  @Test
  public void testHandleDistributionListRequest() throws Exception {
    DistributionListRequest req = new DistributionListRequest(createCorusHost(RepoRole.CLIENT).getEndpoint());
    RemoteEvent event = new RemoteEvent(DistributionListRequest.EVENT_TYPE, req);
    repo.onAsyncEvent(event);
    verify(distListRequestQueue).add(any(DistributionListRequest.class));
  }
  
  @Test
  public void testHandleDistributionDeploymentRequest() throws Exception {
    DistributionDeploymentRequest req = new DistributionDeploymentRequest(createCorusHost(RepoRole.CLIENT).getEndpoint());
    RemoteEvent event = new RemoteEvent(DistributionDeploymentRequest.EVENT_TYPE, req);
    repo.onAsyncEvent(event);
    verify(distDeployRequestQueue).add(any(DistributionDeploymentRequest.class));
  }
  
  @Test
  public void testHandleExecConfigNotification() throws Exception {
    host.setRepoRole(RepoRole.CLIENT);
    ExecConfig conf = new ExecConfig();
    conf.setName("test");
    conf.setStartOnBoot(true);
    ExecConfigNotification notif = new ExecConfigNotification(Collections2.arrayToList(conf));
    notif.addTarget(host.getEndpoint());
    
    RemoteEvent event = new RemoteEvent(ExecConfigNotification.EVENT_TYPE, notif);
    repo.onSyncEvent(event);

    verify(tasks).executeBackground(any(Task.class), any(Void.class), any(BackgroundTaskConfig.class));
    verify(cluster).send(any(ExecConfigNotification.class));
  }
  
  @Test
  public void testHandleExecConfigNotificationHostNotTargeted() throws Exception {
    host.setRepoRole(RepoRole.CLIENT);
    ExecConfig conf = new ExecConfig();
    conf.setName("test");
    ExecConfigNotification notif = new ExecConfigNotification(Collections2.arrayToList(conf));
    notif.addTarget(createCorusHost(RepoRole.CLIENT).getEndpoint());
    
    RemoteEvent event = new RemoteEvent(ExecConfigNotification.EVENT_TYPE, notif);
    repo.onSyncEvent(event);
    
    verify(tasks, never()).executeBackground(any(Task.class), any(Void.class), any(BackgroundTaskConfig.class));
    verify(cluster).send(any(ExecConfigNotification.class));
  }
  
  @Test
  public void testHandleConfigNotification() throws Exception {
    host.setRepoRole(RepoRole.CLIENT);
    ConfigNotification notif = new ConfigNotification();
    notif.addTarget(host.getEndpoint());
    
    Properties props = new Properties();
    props.setProperty("test", "val");
    notif.addProperties(props);
    notif.addTags(Collections2.arrayToSet("tag1", "tag2"));
    
    RemoteEvent event = new RemoteEvent(ConfigNotification.EVENT_TYPE, notif);
    repo.onSyncEvent(event);
    
    verify(config).addProperties(eq(PropertyScope.PROCESS), any(Properties.class), eq(Boolean.FALSE));
    verify(config).addTags(anySet());
    
    verify(cluster).send(any(ExecConfigNotification.class));
  }
  
  @Test
  public void testConfigNotificationHostNotTargeted() throws Exception {
    host.setRepoRole(RepoRole.CLIENT);
    ConfigNotification notif = new ConfigNotification();
    notif.addTarget(createCorusHost(RepoRole.CLIENT).getEndpoint());
    
    Properties props = new Properties();
    props.setProperty("test", "val");
    notif.addProperties(props);
    notif.addTags(Collections2.arrayToSet("tag1", "tag2"));
    
    RemoteEvent event = new RemoteEvent(ConfigNotification.EVENT_TYPE, notif);
    repo.onSyncEvent(event);
    
    verify(config, never()).addProperties(eq(PropertyScope.PROCESS), any(Properties.class), eq(Boolean.FALSE));
    verify(config, never()).addTags(anySet());
    
    verify(cluster).send(any(ExecConfigNotification.class));

  }
}
