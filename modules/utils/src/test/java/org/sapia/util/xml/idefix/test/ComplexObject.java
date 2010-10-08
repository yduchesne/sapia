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
public class ComplexObject {

  public Object _object1;

  public Object _object2;

  public ComplexObject(Object anObject, Object anotherObject) {
    _object1 = anObject;
    _object2 = anotherObject;
  }

  public Object getObject1() {
    return _object1;
  }

  public Object getObject2() {
    return _object2;
  }

  public void setObject1(Object anObject) {
    _object1 = anObject;
  }

  public void setObject2(Object anObject) {
    _object2 = anObject;
  }

  public int[] getIntArray() {
    return new int[] {1, 2, 3};
  }
}