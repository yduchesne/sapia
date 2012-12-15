package org.sapia.corus.client.services.repository;

import org.sapia.corus.client.services.cluster.CorusHost.RepoRole;


/**
 * This interface specifies the behavior of the Repository module.
 * 
 * @author yduchesne
 *
 */
public interface Repository {
  
  /**
   * The module's role constant.
   */
  public static final String ROLE = Repository.class.getName();
  
  /**
   * Forces a pull from repository server nodes.
   * 
   * @throws IllegalStateException if this instance's role is not {@link RepoRole#CLIENT}
   */
  public void pull() throws IllegalStateException;
  
  /**
   * Forces a push to repository client nodes.
   * 
   * @throws IllegalStateException if this instance's role is not {@link RepoRole#SERVER}
   */
  public void push() throws IllegalStateException;
}
