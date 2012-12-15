package org.sapia.corus.repository.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.sapia.corus.client.exceptions.deployer.DistributionNotFoundException;
import org.sapia.corus.client.services.cluster.Endpoint;
import org.sapia.corus.client.services.repository.DistributionDeploymentRequest;
import org.sapia.corus.client.services.repository.RepoDistribution;
import org.sapia.corus.deployer.InternalDeployer;
import org.sapia.corus.taskmanager.core.DefaultThrottleKey;
import org.sapia.corus.taskmanager.core.TaskExecutionContext;
import org.sapia.corus.taskmanager.core.ThrottleKey;
import org.sapia.corus.taskmanager.util.RunnableTask;
import org.sapia.corus.taskmanager.util.RunnableThrottleableTask;
import org.sapia.corus.util.Queue;

/**
 * A task that handles {@link DistributionDeploymentRequest}s.
 * 
 * @author yduchesne
 *
 */
public class DistributionDeploymentRequestHandlerTask extends RunnableThrottleableTask {
  
  /**
   * The {@link ThrottleKey} that this class uses.
   */
  public static final ThrottleKey DEPLOY_REQUEST_THROTTLE = new DefaultThrottleKey();

  private Queue<DistributionDeploymentRequest> distDeployRequestQueue;
  
  public DistributionDeploymentRequestHandlerTask(Queue<DistributionDeploymentRequest> distDeployRequestQueue) {
    super(DEPLOY_REQUEST_THROTTLE);
    this.distDeployRequestQueue = distDeployRequestQueue;
  }
  
  @Override
  public void run() {
    
    InternalDeployer deployer = context().getServerContext().lookup(InternalDeployer.class);
    
    Map<RepoDistribution, Set<Endpoint>> distributionTargets = getDistributionTargets(context());
    
    if (distributionTargets.isEmpty()) {
      context().debug("No targets to deploy to");
    } else {
      context().info(String.format("Got %s targets to deploy to", distributionTargets));
      PerformDeploymentTask deployTasks = new PerformDeploymentTask();
      for (Map.Entry<RepoDistribution, Set<Endpoint>> entry : distributionTargets.entrySet()) {
        context().debug(String.format("Triggering deployment of %s to %s", entry.getKey(), entry.getValue()));
        try {
          RunnableTask task = new DeploymentRequestHandlerTask(
              deployer.getDistributionFile(entry.getKey().getName(), entry.getKey().getVersion()), 
              new ArrayList<Endpoint>(entry.getValue())
          );
          deployTasks.add(task);
        } catch (DistributionNotFoundException e) {
          context().error("Caught error attempting to initiate distribution deployment", e);
        }
      }
      context().getTaskManager().execute(deployTasks, null);    
    }
  }
  
  // --------------------------------------------------------------------------
  // Package visibility for unit testing
  
  Map<RepoDistribution, Set<Endpoint>> getDistributionTargets(TaskExecutionContext context) {
    Map<RepoDistribution, Set<Endpoint>> distributionTargets = new HashMap<RepoDistribution, Set<Endpoint>>();
    for (DistributionDeploymentRequest req : distDeployRequestQueue.removeAll()) {
      context.debug("Processing deployment request: " + req);
      for (RepoDistribution dist : req.getDistributions()) {
        context.debug(String.format("Adding target %s for deployment of distribution %s", req.getEndpoint(), dist));
        Set<Endpoint> targets = distributionTargets.get(dist);
        if (targets == null) {
          targets = new HashSet<Endpoint>();
          distributionTargets.put(dist, targets);
        }
        targets.add(req.getEndpoint());
      }
    }
    return distributionTargets;
  }
}
