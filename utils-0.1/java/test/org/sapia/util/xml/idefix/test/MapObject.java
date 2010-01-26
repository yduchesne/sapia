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
public class MapObject {

  public static final Object KEY_ONE = "One";
  public static final Object VALUE_ONE = "One";
  public static final Object KEY_TWO = new Integer(2);
  public static final Object VALUE_TWO =
          new SimpleObject("element", "Two", "java.lang.String");
  public static final Object KEY_THREE = new SimpleObject("key", "Three", "java.lang.String");
  public static final Object VALUE_THREE =
          new ComplexObject(new SimpleObject("element", "Three.One", "java.lang.String"),
                            new SimpleObject("element", "Three.Two", "java.lang.String"));

  public MapObject() {
  }

  public Hashtable getEmptyHashtable() {
    return new Hashtable();
  }

  public Map getMap() {
    HashMap aMap = new HashMap();
    aMap.put(KEY_ONE, VALUE_ONE);
    aMap.put("null1", null);
    aMap.put(KEY_TWO, VALUE_TWO);
    aMap.put("null2", null);
    aMap.put(KEY_THREE, VALUE_THREE);
    aMap.put("null3", null);
    return aMap;
  }
}
