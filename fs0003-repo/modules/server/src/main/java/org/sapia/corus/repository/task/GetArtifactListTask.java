package org.sapia.corus.repository.task;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.sapia.corus.client.services.cluster.CorusHost;
import org.sapia.corus.client.services.cluster.CorusHost.RepoRole;
import org.sapia.corus.client.services.repository.ArtifactListRequest;
import org.sapia.corus.taskmanager.util.RunnableTask;
import org.sapia.ubik.mcast.EventChannel;

/**
 * This task sends a {@link ArtifactListRequest} to Corus repository server nodes.
 * 
 * @author yduchesne
 *
 */
public class GetArtifactListTask extends RunnableTask {
  
  private Set<CorusHost> repos = new HashSet<CorusHost>();
  
  @Override
  public void run() {
    Set<CorusHost> hosts = context()
        .getServerContext()
        .getServices()
        .getClusterManager()
        .getHosts();
    if (hosts.isEmpty()) {
      context().debug("Host list is empty - hosts probably not discovered yet");
    } else {
      context().debug("Got Corus peers: " + hosts);
      for (CorusHost h : hosts) {
        if (h.getRepoRole() == RepoRole.SERVER) {
          if (repos.add(h)) {
            context().debug("Adding repo node: " + h);
            try {
              EventChannel channel = context().getServerContext().getServices().getClusterManager().getEventChannel();
              channel.dispatch(
                  h.getEndpoint().getChannelAddress(),
                  ArtifactListRequest.EVENT_TYPE, 
                  new ArtifactListRequest(context().getServerContext().getCorusHost().getEndpoint())
              );
            } catch (IOException e) {
              context().error("Could not dispatch distribution list request", e);
            } 
          } else {
            context().debug("Corus host already contacted, ignoring: " + h);
          }
        }
      }
    }
  }
  
}
