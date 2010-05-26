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
public class WrappedTextualNamedValue implements ObjectWrapperIF {

  private TextualNamedValue _theTextualNamedValue;

  public WrappedTextualNamedValue() {
    _theTextualNamedValue = new TextualNamedValue();
  }

  public TextualNamedValue getTextualNamedValue() {
    return _theTextualNamedValue;
  }

//  public void addName(String aName) {
//    throw new UnsupportedOperationException("The method addName is not implemented");
//  }
//
//  public void setName(String aName) {
//    throw new UnsupportedOperationException("The method setName is not implemented");
//  }
//
//  public void addValue(String aValue) {
//    throw new UnsupportedOperationException("The method addValue is not implemented");
//  }
//
//  public void setValue(String aValue) {
//    throw new UnsupportedOperationException("The method setValue is not implemented");
//  }
//
//  public void setText(String aContent) {
//    throw new UnsupportedOperationException("The method setText is not implemented");
//  }

  public Object getWrappedObject() {
    return _theTextualNamedValue;
  }
}
