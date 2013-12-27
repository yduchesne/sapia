package org.sapia.validator.rules;

import org.sapia.validator.BeanRule;

import java.util.Collection;

/**
 * Insures that the size/length of a collection or array is lower or equal
 * to a given maximum.
 *
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class MaxSize extends BeanRule {
  private String _attribute;
  private int    _size;

  /**
   * Constructor for MinSize.
   */
  public MaxSize() {
    super.throwExceptionOnNull(true);
  }

  /**
   * Sets the maximum expected size of collections/arrays that
   * are validated by an instance of this class.
   *
   * @param size a size.
   */
  public void setSize(int size) {
    _size = size;
  }

  /**
   * @see org.sapia.validator.Rule#validate(ValidationContext)
   */
  public boolean doValidate(Object obj) {
    if (obj instanceof Object[]) {
      return ((Object[]) obj).length <= _size;
    } else if (obj instanceof Collection) {
      return ((Collection) obj).size() <= _size;
    } else {
      throw new IllegalArgumentException(
        "maxSize only processes java.util.Collection instances or arrays " + qualifiedName());
    }
  }
}
