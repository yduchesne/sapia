package org.sapia.corus.admin;

import org.sapia.corus.admin.exceptions.core.ServiceNotFoundException;

/**
 * This class is the corus server's remote interface.
 *
 * @author Yanick Duchesne
 */
public interface Corus extends java.rmi.Remote {
  
  /**
   * @return the version of the corus server.
   */
  public String getVersion();
  
  /**
   * @return the domain of the Corus server.
   */
  public String getDomain();
  
  /**
   * @param moduleName the name of the module to lookup.
   * @return the remote module instance.
   * @throws ServiceNotFoundException when the desired service is not found.
   */
  public Object lookup(String moduleName) throws ServiceNotFoundException;
  
}
