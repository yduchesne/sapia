package org.sapia.validator.rules;

import org.sapia.validator.BeanRule;

/**
 * Validates that the length of a string is greater or equal
 * to a given length.
 * 
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class MinLen extends BeanRule{
  
  private int _len;

  /**
   * Constructor for MinLen.
   */
  public MinLen() {
    throwExceptionOnNull(true);
  }
  
  /**
   * The minimum expected length of validated strings.
   * 
   * @param len a minimum length.
   */
  public void setLen(int len){
    _len = len;
  }
  
  /**
   * @see org.sapia.validator.BeanRule#doValidate(Object)
   */
  protected boolean doValidate(Object toValidate) {
    return ((String)toValidate).length() >= _len;
  }


}
