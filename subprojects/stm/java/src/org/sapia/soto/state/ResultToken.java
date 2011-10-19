package org.sapia.soto.state;

/**
 * An instance of this class is kept as part of a <code>Result</code>
 * instance. The value of the result token is set by applications. It is
 * an arbitrary value that may be used to conditionally trigger given
 * states.
 * 
 * @author yduchesne
 *
 */
public class ResultToken {
  
  /**
   * "Standard" constant whose value can be used to set an instance of this
   * class' token value, to signal a "normal" or "success" condition.
   */
  public static final String OK = "OK";
  
  /**
   * "Standard" constant whose value can be used to set an instance of this
   * class' token value, to signal an error condition.
   */  
  public static final String ERROR = "ERROR";

  private Object _value;
  
  /**
   * @param an arbitrary value.
   */
  public void setValue(Object value){
    _value = value;
  }
  
  /**
   * @return the <code>value</code> held by this instance, or <code>null</code>
   * if no such value internally exists.
   */
  public Object getValue(){
    return _value;
  }
  
  /**
   * @return the value held by this instance. The internal reference
   * is set to <code>null</code>. 
   */
  public Object consume(){
    Object value = _value;
    _value = null;
    return value;
  }
  
}
