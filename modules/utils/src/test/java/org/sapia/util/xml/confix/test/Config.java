package org.sapia.util.xml.confix.test;

// Import of Sun's JDK classes
// ---------------------------
import java.util.ArrayList;
import java.util.List;

import org.sapia.util.xml.confix.ObjectHandlerIF;

/**
 *
 *
 * @author Jean-Cedric Desrochers
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class Config implements ObjectHandlerIF {

  private String _theName;
  private ArrayList _theNamedValues;
  private Object[] _theCustomObject;

  public Config() {
    _theNamedValues = new ArrayList();
    _theCustomObject = new Object[2];
  }

  public String getName() {
    return _theName;
  }

  public List getNamedValues() {
    return _theNamedValues;
  }

  public void setName(String aName) {
    _theName = aName;
  }

  public void addWrappedNamedValue(WrappedNamedValue aWrapper) {
    _theNamedValues.add(aWrapper.getNamedValue());
  }

  public void addNamedValue(NamedValue aNamedValue) {
    _theNamedValues.add(aNamedValue);
  }

  public NamedValue createParam() {
    NamedValue aNamedValue = new NamedValue();
    _theNamedValues.add(aNamedValue);
    return aNamedValue;
  }

  public void setParam(NamedValue aNamedValue) {
    _theNamedValues.add(aNamedValue);
  }

  public Object[] getCustomObject() {
    return _theCustomObject;
  }

  /**
   * Handles the passed in object that was created for the element name passed in.
   *
   * @param anElementName The xml element name for which the object was created.
   * @param anObject The object to handle.
   */
  public void handleObject(String anElementName, Object anObject) {
    _theCustomObject[0] = anElementName;
    _theCustomObject[1] = anObject;
  }
}
