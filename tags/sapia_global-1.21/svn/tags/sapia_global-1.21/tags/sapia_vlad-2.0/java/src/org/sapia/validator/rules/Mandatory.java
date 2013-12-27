package org.sapia.validator.rules;

import org.sapia.validator.BeanRule;

/**
 * Insures that an expected value is not null.
 *
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class Mandatory extends BeanRule {
  /**
   * Constructor for Mandatory.
   */
  public Mandatory() {
    super();
  }

  /**
   * @see org.sapia.validator.BeanRule#doValidate(Object)
   */
  protected boolean doValidate(Object toValidate) {
    return toValidate != null;
  }
}
