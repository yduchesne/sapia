package org.sapia.corus.admin.services.deployer;


public interface DeployerConfiguration {

  public abstract String getDeployDir();

  public abstract long getFileLockTimeout();

  public abstract String getTempDir();

}