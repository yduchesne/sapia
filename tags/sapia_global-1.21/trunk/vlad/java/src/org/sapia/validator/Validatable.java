package org.sapia.validator;


/**
 * Specifies the behavior of objects that can be "validated".
 *
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public interface Validatable {
  /**
   * Returns this instance's unique identifier.
   *
   * @return a unique identifier.
   */
  public String getId();

  /**
   * Validates this instance.
   *
   * @param a <code>ValidationContext</code>.
   */
  public void validate(ValidationContext ctx);
}
