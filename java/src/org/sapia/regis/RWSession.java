package org.sapia.regis;

/**
 * This interface extends the <code>RegisSession</code> interface 
 * in order to provide transaction demarcation. All write operations
 * on a registry (and its underlying nodes) must be performed in the
 * context of a transaction, using the transaction-demarcation methods
 * provided by an instance of this class.
 * 
 * @author yduchesne
 *
 */
public interface RWSession extends RegisSession{
  
  /**
   * Begins a new transaction. 
   */
  public void begin();

  /**
   * Commits the current transaction.
   */
  public void commit();
  
  /**
   * Rolls back the current transaction.
   */
  public void rollback();

}
