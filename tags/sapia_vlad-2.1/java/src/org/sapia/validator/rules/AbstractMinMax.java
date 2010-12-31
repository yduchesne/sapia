package org.sapia.validator.rules;

import org.sapia.validator.BeanRule;

/**
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public abstract class AbstractMinMax extends BeanRule{
  private Comparable _comparant;
  private boolean _min;
  
  /**
   * Constructor for AbstractMin.
   */
  protected AbstractMinMax(boolean min) {
    _min = min;
  }
  
  protected void setComparable(Comparable c){
    _comparant = c;
  }
  
  /**
   * @see org.sapia.validator.BeanRule#doValidate(Object)
   */
  protected boolean doValidate(Object toValidate) {
    if(_min){
      return ((Comparable)toValidate).compareTo(_comparant) >= 0;
    } else{
      return ((Comparable)toValidate).compareTo(_comparant) <= 0;
    }
  }
}
