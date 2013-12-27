package org.sapia.util.text;


/**
 * A template element holding non-variable data.
 *
 * @author JC Desrochers
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class ConstantElement implements TemplateElementIF {
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////  INSTANCE ATTRIBUTES  /////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /** The content of this constant element. */
  private String _theContent;

  /////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////  CONSTRUCTORS  /////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Creates a new ConstantElement instance with the content passed in.
   *
   * @param aContent The content of this constant element.
   */
  public ConstantElement(String aContent) {
    _theContent = aContent;
  }

  /////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////  ACCESSOR METHODS  ///////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Returns the content of this constant element.
   *
   * @return The content of this constant element.
   */
  public String getContent() {
    return _theContent;
  }

  /////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////  INTERACE IMPLEMENTATION  ///////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Renders this template element using the template context passed in and
   * returns the result in a new string.
   *
   * @param aContext The template context to use in the rendering process.
   * @return The result of the rendering operation as a new string.
   * @exception TemplateException If an error occurs rendering this template element.
   */
  public String render(TemplateContextIF aContext) throws TemplateException {
    return _theContent;
  }

  /**
   * Renders this template element using the template context passed in and
   * appending the result in the string buffer passed in.
   *
   * @param aContext The template context to use in the rendering process.
   * @exception TemplateException If an error occurs rendering this template element.
   */
  public void render(TemplateContextIF aContext, StringBuffer aBuffer)
    throws TemplateException {
    aBuffer.append(_theContent);
  }
}
