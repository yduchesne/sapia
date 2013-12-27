package org.sapia.qool;

/**
 * Holds various enums used has constants.
 * 
 * @author yduchesne
 *
 */
public interface Constants {
  
  public enum Type{
    GENERIC, QUEUE, TOPIC;
  }
  
  public enum TxStatus{
    NONE, STARTED, COMMITTED, ROLLED_BACK;
  }
}


