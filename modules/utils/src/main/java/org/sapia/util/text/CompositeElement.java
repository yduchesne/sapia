package org.sapia.util.text;


// Import of Sun's JDK classes
// ---------------------------
import java.util.Iterator;
import java.util.List;


/**
 * This class implements a composite <code>TemplateElementIF</code>, which holds
 * <code>VariableElement</code> and <code>ConstantElment</code> instances.
 *
 * @author JC Desrochers
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class CompositeElement implements TemplateElementIF {
  /////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////  CLASS ATTRIBUTES  ///////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /** Defines the logger instance for this class. */

  //private static final Logger _theLogger = Logger.getLogger(CompositeElement.class);
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////  INSTANCE ATTRIBUTES  /////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /** The list of template elements. */
  private List _theElements;

  /////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////  CONSTRUCTORS  /////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Creates a new CompositeElement instance with the argument passed in.
   *
   * @param someElements The template elements of this composite element.
   */
  public CompositeElement(List someElements) {
    _theElements = someElements;
  }

  /////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////  ACCESSOR METHODS  ///////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Returns the template elements of this composite element.
   *
   * @return The template elements of this composite element.
   */
  public List getElements() {
    return _theElements;
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
    StringBuffer aBuffer = new StringBuffer();
    render(aContext, aBuffer);

    return aBuffer.toString();
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
    try {
      for (Iterator it = _theElements.iterator(); it.hasNext();) {
        TemplateElementIF anElement = (TemplateElementIF) it.next();
        anElement.render(aContext, aBuffer);
      }
    } catch (TemplateException re) {
      String aMessage = "Unable to render the elements of this composite element";
      throw new TemplateException(aMessage, re);
    }
  }
}
