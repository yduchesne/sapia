package org.sapia.util.xml.idefix.test;

import java.util.*;

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
public class CollectionObject {

  public static final Object VALUE_ONE = "One";
  public static final Object VALUE_TWO =
          new SimpleObject("element", "Two", "java.lang.String");
  public static final Object VALUE_THREE =
          new ComplexObject(new SimpleObject("element", "Three.One", "java.lang.String"),
                            new SimpleObject("element", "Three.Two", "java.lang.String"));

  public CollectionObject() {
  }

  public Vector getEmptyVector() {
    return new Vector();
  }

  public List getList() {
    ArrayList aList = new ArrayList();
    aList.add(VALUE_ONE);
    aList.add(null);
    aList.add(VALUE_TWO);
    aList.add(null);
    aList.add(VALUE_THREE);
    aList.add(null);
    return aList;
  }

  public Iterator getIterator() {
    return getList().iterator();
  }
}
