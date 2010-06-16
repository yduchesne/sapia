package org.sapia.corus.admin.facade.impl;

import java.util.List;

import org.sapia.corus.admin.Results;
import org.sapia.corus.admin.exceptions.port.PortActiveException;
import org.sapia.corus.admin.exceptions.port.PortRangeConflictException;
import org.sapia.corus.admin.exceptions.port.PortRangeInvalidException;
import org.sapia.corus.admin.facade.CorusConnectionContext;
import org.sapia.corus.admin.facade.PortManagementFacade;
import org.sapia.corus.admin.services.port.PortManager;
import org.sapia.corus.admin.services.port.PortRange;
import org.sapia.corus.core.ClusterInfo;

public class PortManagementFacadeImpl extends FacadeHelper<PortManager> implements PortManagementFacade {
  
  public PortManagementFacadeImpl(CorusConnectionContext context) {
    super(context, PortManager.class);
  }
  
  @Override
  public void addPortRange(String name, int min, int max, ClusterInfo cluster)
      throws PortRangeConflictException, PortRangeInvalidException {
    proxy.addPortRange(name, min, max);
    invoker.invokeLenient(void.class, cluster);
  }
  
  @Override
  public Results<List<PortRange>> getPortRanges(ClusterInfo cluster) {
    Results<List<PortRange>> results = new Results<List<PortRange>>();
    proxy.getPortRanges();
    invoker.invokeLenient(results, cluster);
    return results;
  }
  
  @Override
  public void releasePortRange(String rangeName, ClusterInfo cluster) {
    proxy.releasePortRange(rangeName);
    invoker.invokeLenient(void.class, cluster);
  }
  
  @Override
  public void removePortRange(String name, boolean force, ClusterInfo cluster)
      throws PortActiveException {
    proxy.removePortRange(name, force);
    invoker.invokeLenient(void.class, cluster);
  }
 
}
