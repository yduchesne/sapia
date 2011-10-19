package org.sapia.soto.util.matcher;

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
public abstract class AbstractPatternElement implements PatternElement {
  protected PatternElement _next;

  /**
   * Constructor for AbstractPatternElement.
   */
  public AbstractPatternElement() {
    super();
  }

  public void setNext(PatternElement elem) {
    _next = elem;
  }

  /**
   * @see org.sapia.soto.util.matcher.PatternElement#getNext()
   */
  public PatternElement getNext() {
    return _next;
  }
}
