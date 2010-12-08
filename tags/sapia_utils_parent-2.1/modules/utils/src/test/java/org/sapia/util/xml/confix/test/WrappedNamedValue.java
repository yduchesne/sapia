package org.sapia.util.xml.confix.test;

import org.sapia.util.xml.confix.ObjectWrapperIF;

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
public class WrappedNamedValue implements ObjectWrapperIF {

  private NamedValue _theNamedValue;

  public WrappedNamedValue() {
    _theNamedValue = new NamedValue();
  }

  public NamedValue getNamedValue() {
    return _theNamedValue;
  }

//  public void setName(String aName) {
//    throw new UnsupportedOperationException("The method setName is not implemented");
//  }
//
//  public void setValue(String aValue) {
//    throw new UnsupportedOperationException("The method setValue is not implemented");
//  }

  public Object getWrappedObject() {
    return _theNamedValue;
  }
}
