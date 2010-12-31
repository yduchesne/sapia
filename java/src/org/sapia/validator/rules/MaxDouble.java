package org.sapia.validator.rules;

/**
 * Asserts that the object to evaluate is smaller or equal than a specified value.
 * The evaluated object is expected to be an instance of <code>Double</code>
 * 
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class MaxDouble extends AbstractMinMax{
  private int    _val;
  
  /**
   * Constructor for MaxDouble.
   */
  public MaxDouble() {
    super(false);
  }
  
  /**
   * Sets the value against which the comparison should be peformed.
   * 
   * @param val a <code>double</code>.
   */
  public void setValue(double val){
		super.setComparable(new Double(val));
  }

}
