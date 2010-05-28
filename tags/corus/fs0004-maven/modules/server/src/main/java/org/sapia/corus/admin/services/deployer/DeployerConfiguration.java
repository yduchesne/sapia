package org.sapia.corus.admin.services.deployer;

import org.sapia.corus.property.Property;

public interface DeployerConfiguration {

  public abstract Property getDeployDir();

  public abstract Property getFileLockTimeout();

  public abstract Property getTempDir();

}