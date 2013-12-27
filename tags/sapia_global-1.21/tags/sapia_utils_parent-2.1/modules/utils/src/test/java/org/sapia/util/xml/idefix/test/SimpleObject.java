package org.sapia.util.xml.idefix.test;

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
public class SimpleObject {

  private String _name;

  private String _value;

  private String _type;

  public SimpleObject(String aName, String aValue, String aType) {
    _name = aName;
    _value = aValue;
    _type = aType;
  }

  public String getName() {
    return _name;
  }

  public String getValue() {
    return _value;
  }

  public String getType() {
    return _type;
  }
}
