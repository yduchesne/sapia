package org.sapia.corus.deployer;

import java.util.List;

import org.sapia.corus.LogicException;
import org.sapia.corus.admin.CommandArg;
import org.sapia.corus.deployer.config.Distribution;
import org.sapia.corus.util.ProgressQueue;
import org.sapia.corus.util.ProgressQueueImpl;

public class BaseDeployer implements Deployer{

  private DistributionStore   _store       = new DistributionStore();
  
  public Distribution getDistribution(CommandArg name, CommandArg version)
      throws LogicException {
    return _store.getDistribution(name, version);
  }
  
  public List<Distribution> getDistributions() {
    return _store.getDistributions();
  }
  
  public List<Distribution> getDistributions(CommandArg name) {
    return _store.getDistributions(name);
  }
  
  public List<Distribution> getDistributions(CommandArg name, CommandArg version) {
    return _store.getDistributions(name, version);
  }
  
  public ProgressQueue undeploy(CommandArg distName, CommandArg version) {
    ProgressQueue q = new ProgressQueueImpl();
    q.close();
    return q;
  }
  
  public String getRoleName() {
    return Deployer.ROLE;
  }
  
  protected DistributionStore getDistributionStore(){
    return _store;
  }
  
}
