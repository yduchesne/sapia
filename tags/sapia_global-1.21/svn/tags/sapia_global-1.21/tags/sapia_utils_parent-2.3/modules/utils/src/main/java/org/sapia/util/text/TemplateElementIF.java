package org.sapia.util.text;


/**
 * Renders parsed data from a <code>TemplateFactoryIF</code>.
 *
 * @author JC Desrochers.
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public interface TemplateElementIF {
  /**
   * Renders this template element using the template context passed in and
   * returns the result in a new string.
   *
   * @param aContext The template context to use in the rendering process.
   * @return The result of the rendering operation as a new string.
   * @exception TemplateException If an error occurs rendering this template element.
   */
  public String render(TemplateContextIF aContext) throws TemplateException;

  /**
   * Renders this template element using the template context passed in and
   * appending the result in the string buffer passed in.
   *
   * @param aContext The template context to use in the rendering process.
   * @exception TemplateException If an error occurs rendering this template element.
   */
  public void render(TemplateContextIF aContext, StringBuffer aBuffer)
    throws TemplateException;
}
