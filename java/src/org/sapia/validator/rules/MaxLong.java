package org.sapia.validator.rules;

/**
 * Asserts that the object to evaluate is lower or equal than a specified value.
 * The evaluated object is expected to be an instance of <code>Long</code>
 * 
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class MaxLong extends AbstractMinMax{
  private int    _val;
  
  /**
   * Constructor for MaxLong.
   */
  public MaxLong() {
    super(false);
  }
  
  /**
   * Sets the value against which the comparison should be peformed.
   * 
   * @param val a <code>long</code>.
   */
  public void setValue(long val){
		super.setComparable(new Long(val));
  }

}
