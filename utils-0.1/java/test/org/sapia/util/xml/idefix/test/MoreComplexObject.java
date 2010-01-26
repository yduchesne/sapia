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
public class MoreComplexObject extends ComplexObject {

  public MoreComplexObject(Object o1, Object o2) {
    super(o1, o2);
  }

  public Object[] getNotFillObjectArray() {
    Object[] someObjects = new Object[5];
    someObjects[2] = java.util.TimeZone.getTimeZone("EST");
    return someObjects;
  }
}