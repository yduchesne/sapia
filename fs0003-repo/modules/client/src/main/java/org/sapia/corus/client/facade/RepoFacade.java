package org.sapia.corus.client.facade;

import org.sapia.corus.client.ClusterInfo;

/**
 * Provides Repository-related methods.
 * 
 * @author yduchesne
 *
 */
public interface RepoFacade {
  
  /**
   * Triggers a pull from Corus repo nodes.
   * 
   * @param cluster a {@link ClusterInfo}.
   */
  public void pull(ClusterInfo cluster);

}
