package org.sapia.corus.admin.facade;

import java.util.List;

import org.sapia.corus.admin.Results;
import org.sapia.corus.admin.exceptions.port.PortActiveException;
import org.sapia.corus.admin.exceptions.port.PortRangeConflictException;
import org.sapia.corus.admin.exceptions.port.PortRangeInvalidException;
import org.sapia.corus.admin.services.port.PortRange;
import org.sapia.corus.core.ClusterInfo;

public interface PortManagementFacade {
  
  /**
   * @param name the name of the port range to add.
   * @param min the lowerbound port of the range.
   * @param max the higherbound port of the range.
   * @param cluster a {@link ClusterInfo} instance. 
   */
  public void addPortRange(String name, int min, int max, ClusterInfo cluster)
   throws PortRangeConflictException, PortRangeInvalidException;
  
  /**
   * @param name the name of the port range to add.
   * @param force if <code>true</code>, indicates that the port range should 
   * be removed even if the Corus server has corresponding ports flagged as
   * active.
   * @param cluster a {@link ClusterInfo} instance. 
   */
  public void removePortRange(String name, boolean force, ClusterInfo cluster)
    throws PortActiveException;  
  
  /**
   * Forces the releases of all ports of the given range.
   *
   * @param rangeName the name of an existing port range.
   * @param cluster a {@link ClusterInfo} instance. 
   */
  public void releasePortRange(String rangeName, ClusterInfo cluster);
  
  /**
   * Returns the {@link PortRange} instances that hold the pre-configured
   * ports of the specified Corus servers.
   *
   * @param cluster a <code>ClusterInfo</code> instance. 
   */
  public Results<List<PortRange>> getPortRanges(ClusterInfo cluster);  

}
