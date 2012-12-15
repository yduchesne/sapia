package org.sapia.corus.repository.task;

import java.util.List;

import org.sapia.corus.client.services.cluster.ClusterManager;
import org.sapia.corus.client.services.deployer.Deployer;
import org.sapia.corus.client.services.deployer.DistributionCriteria;
import org.sapia.corus.client.services.deployer.dist.Distribution;
import org.sapia.corus.client.services.repository.DistributionListRequest;
import org.sapia.corus.client.services.repository.DistributionListResponse;
import org.sapia.corus.client.services.repository.RepoDistribution;
import org.sapia.corus.taskmanager.core.TaskExecutionContext;
import org.sapia.corus.taskmanager.util.RunnableTask;
import org.sapia.ubik.net.ServerAddress;
import org.sapia.ubik.util.Collections2;
import org.sapia.ubik.util.Function;

/**
 * Internally removes {@link DistributionListRequest}s from the passed in queue, sending back 
 * the corresponding response {@link DistributionListResponse} for each such request. 
 *  
 * @author yduchesne
 *
 */
public class DistributionListRequestHandlerTask extends RunnableTask {
  
  private org.sapia.corus.util.Queue<DistributionListRequest> requestQueue;
  
  public DistributionListRequestHandlerTask(org.sapia.corus.util.Queue<DistributionListRequest> requests) {
    this.requestQueue = requests;
  }

  @Override
  public void run() {
    for (DistributionListRequest req : requestQueue.removeAll()) {
      context().info("Processing " + req);
      processRequest(req, context());
    }
  }
  
  // --------------------------------------------------------------------------
  // Package visibility for testing purposes

  void processRequest(DistributionListRequest request, TaskExecutionContext ctx) {
    ServerAddress  addr     = ctx.getServerContext().getCorusHost().getEndpoint().getServerAddress();
    Deployer       deployer = ctx.getServerContext().getServices().getDeployer();
    ClusterManager cluster  = ctx.getServerContext().getServices().getClusterManager();

    List<RepoDistribution> dists = Collections2.convertAsList(
        deployer.getDistributions(DistributionCriteria.builder().all()),
        new Function<RepoDistribution, Distribution>() {
          @Override
          public RepoDistribution call(Distribution dist) {
            return new RepoDistribution(dist.getName(), dist.getVersion());
          }
        });
    
    DistributionListResponse response = new DistributionListResponse(
        ctx.getServerContext().getCorusHost().getEndpoint()
    );
    response.addDistributions(dists);
    try {
      cluster.getEventChannel().dispatch(request.getEndpoint().getChannelAddress(), DistributionListResponse.EVENT_TYPE, response);
    } catch (Exception e) {
      ctx.error(String.format("Could not send distribution list to %s", addr), e);
    }
  }
}
