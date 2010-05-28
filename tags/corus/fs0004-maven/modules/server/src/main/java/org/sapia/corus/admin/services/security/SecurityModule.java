package org.sapia.corus.admin.services.security;

import java.rmi.Remote;

import org.sapia.corus.admin.Module;

/**
 * @author Yanick Duchesne
 */
public interface SecurityModule extends Module, Remote{

  /** Defines the role name of this module. */  
  public static final String ROLE = SecurityModule.class.getName();

}
