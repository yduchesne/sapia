package org.sapia.soto.ubik.monitor;

import java.rmi.RemoteException;

/**
 * This interface specifies the behavior of a basic monitoring agent.
 * 
 * @author Yanick Duchesne
 */
public interface MonitorAgent {
  
  /**
   * Invoked by a monitoring client to check for this instance's status
   * 
   * @throws RemoteException if this agent is not reachable on the network
   */
  public Status checkStatus() throws RemoteException;
  
}
