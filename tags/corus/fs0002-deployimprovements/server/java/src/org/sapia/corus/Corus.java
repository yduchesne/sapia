package org.sapia.corus;


/**
 * This class is the corus server's remote interface.
 *
 * @author Yanick Duchesne
 */
public interface Corus extends java.rmi.Remote {

  public String getDomain();
  public Object lookup(String module) throws CorusException;
  
}
