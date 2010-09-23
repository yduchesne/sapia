package org.sapia.corus.taskmanager.core;

/**
 * This interface specifies the behavior of internal Corus transactions.
 * @author yduchesne
 *
 */
public interface CorusTransaction {
  
  /**
   * Commits this transaction (the associated tasks will be started).
   */
  public void commit();
  
  /**
   * Aborts this transaction (the associated tasks will be stopped).
   */
  public void abort();

}
