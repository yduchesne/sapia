/*
 * AbstractState.java
 *
 * Created on September 8, 2005, 2:21 PM
 *
 */

package org.sapia.soto.state;

/**
 * An abstract <code>State</code> implementation. Provide a setter and getter
 * for the state identifier.
 * 
 * @see State
 *
 * @author yduchesne
 */
public abstract class AbstractState implements State{
  
  private String _id;
  
  /** Creates a new instance of AbstractState */
  public AbstractState() {
  }
  
  public void setId(String id){
    _id = id;
  }
  
  public String getId(){
    return _id;
  }
  
}
