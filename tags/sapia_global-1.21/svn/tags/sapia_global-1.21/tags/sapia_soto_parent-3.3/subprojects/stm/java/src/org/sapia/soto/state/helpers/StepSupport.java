package org.sapia.soto.state.helpers;

import org.apache.commons.lang.ClassUtils;

import org.sapia.soto.state.Step;

/**
 * @author Yanick Duchesne
 *         <dl>
 *         <dt><b>Copyright: </b>
 *         <dd>Copyright &#169; 2002-2003 <a
 *         href="http://www.sapia-oss.org">Sapia Open Source Software </a>. All
 *         Rights Reserved.</dd>
 *         </dt>
 *         <dt><b>License: </b>
 *         <dd>Read the license.txt file of the jar or visit the <a
 *         href="http://www.sapia-oss.org/license.html">license page </a> at the
 *         Sapia OSS web site</dd>
 *         </dt>
 *         </dl>
 */
public abstract class StepSupport implements Step {
  public StepSupport() {
  }

  /**
   * @see org.sapia.soto.state.Step#getName()
   */
  public String getName() {
    return ClassUtils.getShortClassName(getClass());
  }
}
