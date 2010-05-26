package org.sapia.util.text;


/**
 * Holds values that are bound to names. Provides a "lookup" method that is
 * used by a <code>TemplateElementIF</code> to resolve variables.
 *
 * @author JC Desrochers
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public interface TemplateContextIF {
  /**
   * Returns the value of this context for the property name passed in.
   *
   * @param aName The name of the property.
   */
  public Object getValue(String aName);

  /**
   * Puts the given value in this context; if one already exists
   * under the given name, it is overwritten.
   *
   * @param name the name under which to map the given value.
   * @param value an <code>Object</code>.
   */
  public void put(String name, Object value);
}
