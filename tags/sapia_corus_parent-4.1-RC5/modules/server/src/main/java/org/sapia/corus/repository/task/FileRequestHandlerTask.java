package org.sapia.corus.repository.task;

import java.io.File;
import java.util.List;

import org.sapia.corus.client.services.cluster.Endpoint;
import org.sapia.corus.client.services.deployer.transport.DeploymentMetadata;
import org.sapia.corus.client.services.deployer.transport.FileDeploymentMetadata;
import org.sapia.corus.client.services.repository.FileDeploymentRequest;
import org.sapia.ubik.util.Function;

/**
 * This task performs the deployment to a provided list of nodes, following a {@link FileDeploymentRequest}.
 * 
 * @author yduchesne
 *
 */
public class FileRequestHandlerTask extends ArtifactRequestHandlerTaskSupport {

  /**
   * @param file the {@link File} consisting of the file to deploy.
   * @param targets the {@link List} of {@link Endpoint}s corresponding to the Corus nodes to deploy to.
   */
  public FileRequestHandlerTask(final File file, List<Endpoint> targets) {
    super(file, targets, new Function<DeploymentMetadata, Boolean>() {
      @Override
      public DeploymentMetadata call(Boolean clustered) {
        return new FileDeploymentMetadata(file.getName(), file.length(), clustered, null);
      }
    });
  }

}