package org.sapia.archie;


/**
 * Thrown when a given node or value is not found for a given name.
 * 
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class NotFoundException extends Exception {
  
  private Name _remaining, _resolved;
  
  /**
   * Constructor for NotFoundException.
   */
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
