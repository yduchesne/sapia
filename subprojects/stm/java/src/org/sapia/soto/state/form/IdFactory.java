/*
 * IdFactory.java
 *
 * Created on September 3, 2005, 7:44 AM
 *
 */

package org.sapia.soto.state.form;

/**
 * A per-user factory of identifiers.
 *
 * @author yduchesne
 */
public class IdFactory {
  
  private static int _idCount;
  
  /** Creates a new instance of IdFactory */
  IdFactory() {
  }
  
  /**
   * @return an identifier.
   */
  public synchronized int id(){
    int id = ++_idCount;
    if(_idCount == Integer.MAX_VALUE){
      _idCount = 0;
    }
    return id;
  }    
}
