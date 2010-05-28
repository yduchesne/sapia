package org.sapia.corus.admin.services.deployer;

import java.io.Serializable;

import org.sapia.corus.property.LongProperty;
import org.sapia.corus.property.Property;

public class DeployerConfigurationImpl implements Serializable, DeployerConfiguration{
  
  static final long serialVersionUID = 1L;

  public static final Property DEFAULT_FILELOCK_TIMEOUT = new LongProperty(120000L);

  
  private Property deployDir;
  private Property tempDir;
  private Property fileLockTimeout = DEFAULT_FILELOCK_TIMEOUT;
  
  public Property getDeployDir() {
    return deployDir;
  }
  public void setDeployDir(Property deployDir) {
    this.deployDir = deployDir;
  }
 
  public Property getFileLockTimeout() {
    return fileLockTimeout;
  }
  public void setFileLockTimeout(Property fileLockTimeout) {
    this.fileLockTimeout = fileLockTimeout;
  }
  
  public Property getTempDir() {
    return tempDir;
  }
  public void setTempDir(Property tempDir) {
    this.tempDir = tempDir;
  }

}
