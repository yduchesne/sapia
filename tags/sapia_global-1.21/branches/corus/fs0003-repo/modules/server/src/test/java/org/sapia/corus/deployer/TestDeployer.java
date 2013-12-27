package org.sapia.corus.deployer;


public class TestDeployer extends BaseDeployer{
  
  public TestDeployer() {
    super.configuration.setDeployDir("deployDir");
    super.configuration.setTempDir("tmpDir");
  }
  
  public DistributionDatabase getDistributionDatabase(){
    return super.getDistributionDatabase();
  }

}
