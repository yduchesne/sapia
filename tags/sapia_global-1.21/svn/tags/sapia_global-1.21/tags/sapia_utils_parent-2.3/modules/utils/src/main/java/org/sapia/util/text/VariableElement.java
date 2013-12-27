package org.sapia.util.text;


/**
 * This instance holds a variable that is interpolated at rendering time.
 *
 * @author JC Desrochers
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class VariableElement implements TemplateElementIF {
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////  INSTANCE ATTRIBUTES  /////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /** The name of this variable element. */
  private String _theName;
  
  private boolean _throwEx;

  /////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////  CONSTRUCTORS  /////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Creates a new VariableElement with the argument passed in.
   *
   * @param aName The name of this variable element.
   */
  public VariableElement(String aName, boolean throwEx) {
    _throwEx = throwEx;
    _theName = aName;
  }

  /////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////  ACCESSOR METHODS  ///////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Returns the name of this variable element.
   *
   * @return The name of this variable element.
   */
  public String getName() {
    return _theName;
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
    Object aValue = aContext.getValue(_theName);

    if (aValue == null) {
      if(_throwEx)
        throw new TemplateException("No value found for: " + _theName);
      else
        aValue = _theName;
    } else if (!(aValue instanceof String)) {
      throw new TemplateException("The value for " + _theName +
        " is not a string : " + aValue);
    }

    return (String) aValue;
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
    Object aValue = aContext.getValue(_theName);

    if (aValue == null) {
      if(_throwEx)
        throw new TemplateException("No value found for: " + _theName);
      else
        aValue = _theName;
    } else if (!(aValue instanceof String)) {
      throw new TemplateException("The value for " + _theName +
        " is not a string : " + aValue);
    }

    aBuffer.append(aValue);
  }
}
