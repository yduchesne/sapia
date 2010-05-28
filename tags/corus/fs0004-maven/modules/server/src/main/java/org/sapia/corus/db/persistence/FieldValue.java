package org.sapia.corus.db.persistence;

/**
 * An instance of this class encapsulates a field value.
 * 
 * @author yduchesne
 *
 */
public class FieldValue {

  private FieldDescriptor descriptor;
  Object value;
  
  public FieldValue(FieldDescriptor descriptor, Object value) {
    this.descriptor = descriptor;
    this.value = value;
  }
  
  /**
   * @return this instance's corresponding {@link FieldDescriptor}
   */
  public FieldDescriptor getDescriptor() {
    return descriptor;
  }
  
  /**
   * @return this instance's corresponding value.
   */
  public Object getValue() {
    return value;
  }
  
  /**
   * @param otherValue the object on which to perform the matching test.
   * @return true if the other value matche's this instance's value.
   */
  public boolean matches(Object otherValue){
    if(value == null){
      return true;
    }
    else if(otherValue == null){
      return false;
    }
    else{
      return value.equals(otherValue);
    }
  }
}
