package org.sapia.util.text;


/**
 * A <code>TemplateContextIF<code> that resolves values using the system
 * properties.
 *
 * @author JC Desrochers
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class SystemContext implements TemplateContextIF {
  /**
   * Returns the value of this context for the property name passed in.
   *
   * @param aName The name of the property.
   */
  public Object getValue(String aName) {
    return System.getProperty(aName);
  }

  /**
   * @see org.sapia.util.text.TemplateContextIF#put(String, Object)
   */
  public void put(String name, Object value) {
    System.setProperty(name, (value == null) ? null : value.toString());
  }
}
