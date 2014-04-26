package org.sapia.archie;


/**
 * Thrown when a given node or value is not found for a given name.
 * 
 * @author Yanick Duchesne
 */
public class NotFoundException extends Exception {
  
  static final long serialVersionUID = 1L;
  
  private Name _remaining, _resolved;
  
  public NotFoundException(String msg) {
    super(msg);
  }
  
  public void setRemainingName(Name name){
    _remaining = name;
  }
  
  public void setResolvedName(Name name){
    _resolved = name;
  }
  
  public Name getRemainingName(){
    return _remaining;
  }
  
  public Name getResolvedName(){
    return _resolved;
  }
}
