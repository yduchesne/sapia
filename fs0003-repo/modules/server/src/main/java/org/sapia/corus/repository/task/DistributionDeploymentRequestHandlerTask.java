package org.sapia.corus.repository.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sapia.corus.client.exceptions.deployer.DistributionNotFoundException;
import org.sapia.corus.client.services.cluster.Endpoint;
import org.sapia.corus.client.services.processor.ExecConfig;
import org.sapia.corus.client.services.processor.ProcessDef;
import org.sapia.corus.client.services.repository.DistributionDeploymentRequest;
import org.sapia.corus.client.services.repository.RepoDistribution;
import org.sapia.corus.deployer.InternalDeployer;
import org.sapia.corus.taskmanager.core.DefaultThrottleKey;
import org.sapia.corus.taskmanager.core.TaskExecutionContext;
import org.sapia.corus.taskmanager.core.ThrottleKey;
import org.sapia.corus.taskmanager.util.RunnableTask;
import org.sapia.corus.taskmanager.util.RunnableThrottleableTask;
import org.sapia.corus.util.CollectionUtil;
import org.sapia.corus.util.Queue;
import org.sapia.ubik.util.Collections2;
import org.sapia.ubik.util.Condition;
import org.sapia.ubik.util.Function;

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
    
    List<DistributionDeploymentRequest> requests = distDeployRequestQueue.removeAll();
    Set<Endpoint> allTargets = Collections2.convertAsSet(requests, new Function<Endpoint, DistributionDeploymentRequest>() {
      public Endpoint call(DistributionDeploymentRequest req) {
        return req.getEndpoint();
      }
    });
    
    Map<RepoDistribution, Set<Endpoint>> distributionTargets = getDistributionTargets(context(), requests);
    List<ExecConfig> execConfigs = context().getServerContext().getServices().getProcessor().getExecConfigs();
    
    context().info(String.format("Got %s targets to deploy to", distributionTargets));
    PerformDeploymentTask deployTasks = new PerformDeploymentTask();
    deployTasks.add(new SendConfigNotificationTask(allTargets));
    for (final Map.Entry<RepoDistribution, Set<Endpoint>> entry : distributionTargets.entrySet()) {
      context().debug(String.format("Triggering deployment of %s to %s", entry.getKey(), entry.getValue()));
      try {
        RunnableTask task = new DeploymentRequestHandlerTask(
            deployer.getDistributionFile(entry.getKey().getName(), entry.getKey().getVersion()), 
            new ArrayList<Endpoint>(entry.getValue())
        );
        deployTasks.add(task);
        List<ExecConfig> execConfigsForDistribution = CollectionUtil.filterToArrayList(execConfigs, new Condition<ExecConfig>() {
          @Override
          public boolean apply(ExecConfig item) {
            for (ProcessDef p : item.getProcesses()) {
              if (p.getDist().equals(entry.getKey().getName()) && p.getVersion().equals(entry.getKey().getVersion())) {
                return true;
              }
            }
            return false;
          }
        });
        if (!execConfigsForDistribution.isEmpty()) {
          deployTasks.add(new SendExecConfigNotificationTask(execConfigsForDistribution, entry.getValue()));          
        }
      } catch (DistributionNotFoundException e) {
        context().error("Caught error attempting to initiate distribution deployment", e);
      }
    }
    context().getTaskManager().execute(deployTasks, null);    
  }
  
  // --------------------------------------------------------------------------
  // Package visibility for unit testing
  
  Map<RepoDistribution, Set<Endpoint>> getDistributionTargets(TaskExecutionContext context, List<DistributionDeploymentRequest> requests) {
    Map<RepoDistribution, Set<Endpoint>> distributionTargets = new HashMap<RepoDistribution, Set<Endpoint>>();
    for (DistributionDeploymentRequest req : requests) {
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
