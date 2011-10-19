package org.sapia.soto.scheduler;

import java.io.Serializable;

/**
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2005 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public abstract class CronField implements Serializable, Fields{
  
  private int    _value = UNDEFINED;
  private String _name;
  private CronDescriptor _owner;
  
  protected CronField(String name){
    _name = name;
  }
  
  void setDescriptor(CronDescriptor owner){
    _owner = owner;
  }
  
  protected CronDescriptor descriptor(){
    return _owner;
  }
  
  /**
   * @return the name of this field.
   */
  public String getName(){
    return _name;
  }
  
  /**
   * @param value the value to assign to this instance.
   */
  public void setValue(int value){
    if(value != UNDEFINED){
      if(!isValid(value)){
        throw new IllegalArgumentException("Unvalid value for field " + getName() + ": " + value);
      }
    }
    _value = value;
  }
  
  /**
   * Sets the value of this field to "undefined".
   * 
   * @see Fields#UNDEFINED
   */
  public void unsetValue(){
    _value = UNDEFINED;
  }
  
  /**
   * @return <code>true</code> if this field's value has been defined.
   */
  public boolean isDefined(){
    return _value != UNDEFINED;
  }
  
  /**
   * @return the value that this instance holds.
   */
  public int getValue(){
    return _value;
  }
  
  public abstract boolean isValid(int value);

}
