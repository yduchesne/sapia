package org.sapia.corus.client.services.deployer.transport;

import org.sapia.corus.client.ClusterInfo;

/**
 * Corresponds to a file deployment.
 * 
 * @author yduchesne
 *
 */
public class FileDeploymentMetadata extends DeploymentMetadata {
  
  private static final long serialVersionUID = 1L;
  
  private String dirName;

  public FileDeploymentMetadata(String fileName, long contentLen, String dirName, ClusterInfo cluster) {
    super(fileName, contentLen, cluster, DeploymentMetadata.Type.FILE);
    this.dirName = dirName;
  }
  
  /**
   * @return the directory to which to deploy the file.
   */
  public String getDirName() {
    return dirName;
  }

}
