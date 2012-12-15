package org.sapia.corus.repository;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.sapia.corus.client.services.cluster.ClusterManager;
import org.sapia.corus.client.services.cluster.CorusHost;
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
import org.sapia.corus.client.services.repository.ForceClientPullNotification;
import org.sapia.corus.client.services.repository.Repository;
import org.sapia.corus.core.ModuleHelper;
import org.sapia.corus.core.ServerStartedEvent;
import org.sapia.corus.repository.task.DistributionDeploymentRequestHandlerTask;
import org.sapia.corus.repository.task.DistributionListRequestHandlerTask;
import org.sapia.corus.repository.task.DistributionListResponseHandlerTask;
import org.sapia.corus.repository.task.ForcePullTask;
import org.sapia.corus.repository.task.GetDistributionListTask;
import org.sapia.corus.taskmanager.core.BackgroundTaskConfig;
import org.sapia.corus.taskmanager.core.SemaphoreThrottle;
import org.sapia.corus.taskmanager.core.Task;
import org.sapia.corus.taskmanager.core.TaskManager;
import org.sapia.corus.util.Queue;
import org.sapia.corus.util.TimeUtil;
import org.sapia.ubik.mcast.AsyncEventListener;
import org.sapia.ubik.mcast.RemoteEvent;
import org.sapia.ubik.mcast.SyncEventListener;
import org.sapia.ubik.rmi.interceptor.Interceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * Implements the {@link Repository} interface.
 * 
 * @author yduchesne
 *
 */
public class RepositoryImpl extends ModuleHelper implements Repository, AsyncEventListener, SyncEventListener, Interceptor {
  
  private static final long                    MIN_BOOSTRAP_INTERVAL                      = 10000;
  private static final int                     MAX_BOOSTRAP_INTERVAL_OFFSET               = 10000;
  private static final long                    DEFAULT_DIST_DISCO_INTERVAL                = 10000;
  private static final int                     DEFAULT_DIST_DISCO_MAX_ATTEMPTS            = 3;
  private static final int                     DEFAULT_MAX_CONCURRENT_DEPLOYMENT_REQUESTS = 2;

  @Autowired
  private TaskManager                          taskManager;
  
  @Autowired
  private ClusterManager                       clusterManager;
  
  @Autowired
  private Configurator                         configurator;
  
  @Autowired
  private Processor                            processor;
  
  @Autowired
  private EventDispatcher                      dispatcher;

  private Queue<DistributionListRequest>       distListRequestQueue   = new Queue<DistributionListRequest>();
  private Queue<DistributionDeploymentRequest> distDeployRequestQueue = new Queue<DistributionDeploymentRequest>();
  private int                                  maxConcurrentDeploymentRequests = DEFAULT_MAX_CONCURRENT_DEPLOYMENT_REQUESTS;
  private long                                 distDiscoIntervalSeconds        = DEFAULT_DIST_DISCO_INTERVAL;
  private int                                  maxDistDiscoAttempts            = DEFAULT_DIST_DISCO_MAX_ATTEMPTS;
  
  /**
   * @param maxDistDiscoAttempts the maximum number of attempts when performing discovery distributions.
   */
  public void setMaxDistDiscoAttempts(int maxDistDiscoAttempts) {
    this.maxDistDiscoAttempts = maxDistDiscoAttempts;
  }

  /**
   * @param discoIntervalSeconds interval (in seconds) to wait for when attempting to discover relational distributions.
   */
  public void setDistDiscoveryIntervalSeconds(long discoIntervalSeconds) {
    this.distDiscoIntervalSeconds = discoIntervalSeconds;
  }
  
  /**
   * @param maxConcurrentDeploymentRequests the maximum number of deployment requests to process concurrently.
   */
  public void setMaxConcurrentDeploymentRequests(int maxConcurrentDeploymentRequests) {
    Assert.isTrue(maxConcurrentDeploymentRequests > 0, "Max concurrent deployment requests must be greater than 0");
    this.maxConcurrentDeploymentRequests = maxConcurrentDeploymentRequests;
  }
  
  void setClusterManager(ClusterManager clusterManager) {
    this.clusterManager = clusterManager;
  }
  
  void setConfigurator(Configurator configurator) {
    this.configurator = configurator;
  }
  
  void setDispatcher(EventDispatcher dispatcher) {
    this.dispatcher = dispatcher;
  }
  
  void setProcessor(Processor processor) {
    this.processor = processor;
  }
  
  void setTaskManager(TaskManager taskManager) {
    this.taskManager = taskManager;
  }
  
  void setDeployRequestQueue(Queue<DistributionDeploymentRequest> distDeployRequestQueue) {
    this.distDeployRequestQueue = distDeployRequestQueue;
  }
  
  void setDistributionListRequestQueue(Queue<DistributionListRequest> distListRequestQueue) {
    this.distListRequestQueue = distListRequestQueue;
  }
  
  // --------------------------------------------------------------------------
  // Lifecycle
  
  @Override
  public void init() throws Exception {
    dispatcher.addInterceptor(ServerStartedEvent.class, this);

    if (serverContext().getCorusHost().getRepoRole().isServer()) {
      logger().info("Node is repo server");
      clusterManager.getEventChannel().registerAsyncListener(DistributionListRequest.EVENT_TYPE, this);
      clusterManager.getEventChannel().registerAsyncListener(DistributionDeploymentRequest.EVENT_TYPE, this);
      taskManager.registerThrottle(
          DistributionDeploymentRequestHandlerTask.DEPLOY_REQUEST_THROTTLE, 
          new SemaphoreThrottle(maxConcurrentDeploymentRequests)
      );
    } else if (serverContext().getCorusHost().getRepoRole().isClient()){
      logger().info("Node is repo client");
      clusterManager.getEventChannel().registerAsyncListener(DistributionListResponse.EVENT_TYPE, this);
      clusterManager.getEventChannel().registerSyncListener(ConfigNotification.EVENT_TYPE, this);
      clusterManager.getEventChannel().registerSyncListener(ExecConfigNotification.EVENT_TYPE, this);
      
    } else {
      logger().info("This node will not act as either a repository server or client");
    }
  }
  
  @Override
  public void dispose() throws Exception {
  }

  // --------------------------------------------------------------------------
  // Module interface
  
  @Override
  public String getRoleName() {
    return ROLE;
  }

  // --------------------------------------------------------------------------
  // Repository interface

  @Override
  public void pull() throws IllegalStateException {
    if (!serverContext().getCorusHost().getRepoRole().isClient()) {
      throw new IllegalStateException("Node is not CLIENT: " + serverContext().getCorusHost().getRepoRole());
    }
    logger().debug("Node is a repo client: will try to acquire distributions from repo server");
    GetDistributionListTask task = new GetDistributionListTask();
    task.setMaxExecution(maxDistDiscoAttempts);
    
    taskManager.executeBackground(
        task, 
        null,
        BackgroundTaskConfig.create()
           .setExecDelay(TimeUtil.createRandomDelay(MIN_BOOSTRAP_INTERVAL, MAX_BOOSTRAP_INTERVAL_OFFSET))
           .setExecInterval(TimeUnit.SECONDS.convert(distDiscoIntervalSeconds, TimeUnit.SECONDS))
    );  
  }
  
  @Override
  public void push() throws IllegalStateException {
    if (!serverContext().getCorusHost().getRepoRole().isServer()) {
      throw new IllegalStateException("Node is not SERVER: " + serverContext().getCorusHost().getRepoRole());
    }
    
    ForceClientPullNotification notif = new ForceClientPullNotification(serverContext().getCorusHost().getEndpoint());
    for (CorusHost host : clusterManager.getHosts()) {
      if (host.getRepoRole().isClient()) {
        notif.addTarget(host.getEndpoint());
      }
    }
    try {
      clusterManager.send(notif);
    } catch (Exception e) {
      logger().error("Could not send pull notification", e);
      throw new IllegalStateException("Error trying to send pull notification to repo clients - " + e.getMessage());
    }
  }
  
  // -------------------------------------------------------------------------
  // Interceptor interface
  
  public void onServerStartedEvent(ServerStartedEvent event) {
    if (serverContext().getCorusHost().getRepoRole().isClient()) {
      logger().debug("Node is a repo client: will request distributions from repository");
      Task<Void, Void> task = new ForcePullTask(this);
      task.executeOnce();
      taskManager.executeBackground(
          task, 
          null,
          BackgroundTaskConfig.create()
             .setExecDelay(
                 TimeUtil.createRandomDelay(
                     MIN_BOOSTRAP_INTERVAL, MAX_BOOSTRAP_INTERVAL_OFFSET
             )
          )
      );      
    } else {
      logger().debug(String.format("Node is %s, Will not pull distributions from repos", serverContext().getCorusHost().getRepoRole()));
    }
  }
  
  // --------------------------------------------------------------------------
  // AsyncEventListener interface
 
  @Override
  public void onAsyncEvent(RemoteEvent evt) {
    try {
      if (evt.getType().equals(DistributionListRequest.EVENT_TYPE)) {
        logger().debug("Got distribution list request");
        handleDistributionListRequest((DistributionListRequest) evt.getData());
      } else if (evt.getType().equals(DistributionListResponse.EVENT_TYPE)) {
        logger().debug("Got distribution list response");
        handleDistributionListResponse((DistributionListResponse) evt.getData());
      } else if (evt.getType().equals(DistributionDeploymentRequest.EVENT_TYPE)) {
        logger().debug("Got distribution deployment request");
        handleDistributionDeploymentRequest((DistributionDeploymentRequest) evt.getData());
      } else {
        logger().debug("Unknown event type: " + evt.getType()); 
      }
    } catch (IOException e) {
      logger().error("IO Error caught trying to handle event: " + evt.getType(), e);
    }
  }
  
  @Override
  public Object onSyncEvent(RemoteEvent evt) {
    try {
      if (evt.getType().equals(ExecConfigNotification.EVENT_TYPE)) {
        logger().debug("Got exec config notification");
        handleExecConfigNotification((ExecConfigNotification) evt.getData()); 
      } else if (evt.getType().equals(ConfigNotification.EVENT_TYPE)) {
        logger().debug("Got config notification");
        handleConfigNotification((ConfigNotification) evt.getData()); 
      } 
    } catch (IOException e) {
      logger().error("IO Error caught trying to handle event: " + evt.getType(), e);
    }
    return null;
  }
  
  // --------------------------------------------------------------------------
  // Restricted methods (event handlers)
  
  void handleExecConfigNotification(ExecConfigNotification notif) {
    if (notif.isTargeted(serverContext().getCorusHost().getEndpoint())) {
      for (ExecConfig ec : notif.getConfigs()) {
        logger().info(String.format("Adding exec config: %s, %s", ec.getName(), ec.getProfile()));
        processor.addExecConfig(ec);
      }
      for (ExecConfig ec : notif.getConfigs()) {
        if (ec.isStartOnBoot()) {
          logger().info(String.format("Triggering startup for exec config: %s, %s", ec.getName(), ec.getProfile()));
          processor.exec(ec.getName());
        }
      }
    } 
    
    // cascading to next host
    try {
      clusterManager.send(notif);
    } catch (Exception e) {
      logger().error("Could not cascade notification to next host", e);
    }
  } 
  
  void handleConfigNotification(ConfigNotification notif) {
    if (notif.isTargeted(serverContext().getCorusHost().getEndpoint())) {
      logger().info(String.format("Adding config %s", notif));
      
      if (logger().isDebugEnabled()) {
        Properties props = notif.getProperties();
        for (String n : props.stringPropertyNames()) {
          logger().debug(String.format("Property %s = %s", n, props.getProperty(n)));
        }
      }
      configurator.addProperties(PropertyScope.PROCESS, notif.getProperties(), false);
      
      if (logger().isDebugEnabled()) {
        for (String t : notif.getTags()) {
          logger().debug("tag: " + t);
        }
      }
      configurator.addTags(notif.getTags());
    } 
    
    // cascading to next host
    try {
      clusterManager.send(notif);
    } catch (Exception e) {
      logger().error("Could not cascade notification to next host", e);
    }
  } 
  
  void handleDistributionListRequest(DistributionListRequest distsReq) {
    if (serverContext().getCorusHost().getRepoRole().isServer()) {
      distListRequestQueue.add(distsReq);
      taskManager.execute(new DistributionListRequestHandlerTask(distListRequestQueue), null);
    } else {
      logger().debug("Ignoring " + distsReq + "; repo type is " + serverContext().getCorusHost().getRepoRole());
    }
  }
  
  void handleDistributionListResponse(final DistributionListResponse distsRes) {
    if (serverContext().getCorusHost().getRepoRole().isClient()) {
      taskManager.execute(new DistributionListResponseHandlerTask(distsRes), null);
    } else {
      logger().debug("Ignoring " + distsRes + "; repo type is " + serverContext().getCorusHost().getRepoRole());
    }
  }
  
  void handleDistributionDeploymentRequest(DistributionDeploymentRequest req) {
    if (serverContext().getCorusHost().getRepoRole().isServer()) {
      distDeployRequestQueue.add(req);
      taskManager.execute(new DistributionDeploymentRequestHandlerTask(distDeployRequestQueue), null);
    } else {
      logger().debug("Ignoring " + req + "; repo type is " + serverContext().getCorusHost().getRepoRole());
    }
  }
  
}