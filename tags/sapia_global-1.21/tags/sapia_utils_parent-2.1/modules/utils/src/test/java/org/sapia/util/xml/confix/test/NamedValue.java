package org.sapia.util.xml.confix.test;

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
public class NamedValue {

  private String _theName;
  private String _theValue;

  public NamedValue() {
  }

  public NamedValue(String aName, String aValue) {
    _theName = aName;
    _theValue = aValue;
  }

  public String getName() {
    return _theName;
  }

  public String getValue() {
    return _theValue;
  }

  public void setName(String aName) {
    _theName = aName;
  }

  public void setValue(String aValue) {
    _theValue = aValue;
  }
}
