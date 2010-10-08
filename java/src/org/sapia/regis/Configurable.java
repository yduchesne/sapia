package org.sapia.regis;

import java.rmi.RemoteException;
import java.util.Properties;


public interface Configurable{

  /**
   * @param nodePath the path of the <code>Node</code> to update.
   * @param username a username.
   * @param password a password.
   * @param xmlConf a registry XML configuration.
   * @param props the <code>Properties</code> to use as variables for substitution
   * purposes, or <code>null</code> if no such properties are appropriate.
   * @throws RemoteException if the remote registry could not be accessed.
   * @throws Exception if an error occurs performing the update.
   */
  public void load(Path nodePath, 
                   String username, 
                   String password, 
                   String xmlConf,
                   Properties props) throws RemoteException, Exception;

  /**
   * This method is meant to be used as a replication hook when multiple configurable instances are set up in peer-to-peer.
   * 
   * @see #load(Path, String, String, String, Properties)
   */ 
  public void syncLoad(Path nodePath, String username, String password, String xmlConf, Properties props) throws RemoteException, Exception;

}
