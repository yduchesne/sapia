package org.sapia.soto.util;

import junit.framework.TestCase;

/**
 * @author Yanick Duchesne
 * 
 * <dl>
 * <dt><b>Copyright: </b>
 * <dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open
 * Source Software </a>. All Rights Reserved.</dd>
 * </dt>
 * <dt><b>License: </b>
 * <dd>Read the license.txt file of the jar or visit the <a
 * href="http://www.sapia-oss.org/license.html">license page </a> at the Sapia
 * OSS web site</dd>
 * </dt>
 * </dl>
 */
public class TypeTest extends TestCase {
  public TypeTest(String arg0) {
    super(arg0);
  }

  public void testBoolean() {
    super.assertTrue(Type.BOOLEAN.equals(Type.getTypeForName("boolean")));
    super.assertTrue(Type.BOOLEAN.equals(Type.getTypeForTypeName(boolean.class
        .getName())));
  }

  public void testByte() {
    super.assertTrue(Type.BYTE.equals(Type.getTypeForName("byte")));
    super.assertTrue(Type.BYTE.equals(Type.getTypeForTypeName(byte.class
        .getName())));
  }

  public void testShort() {
    super.assertTrue(Type.SHORT.equals(Type.getTypeForName("short")));
    super.assertTrue(Type.SHORT.equals(Type.getTypeForTypeName(short.class
        .getName())));
  }

  public void testInt() {
    super.assertTrue(Type.INT.equals(Type.getTypeForName("int")));
    super.assertTrue(Type.INT.equals(Type.getTypeForTypeName(int.class
        .getName())));
  }

  public void testLong() {
    super.assertTrue(Type.LONG.equals(Type.getTypeForName("long")));
    super.assertTrue(Type.LONG.equals(Type.getTypeForTypeName(long.class
        .getName())));
  }

  public void testFloat() {
    super.assertTrue(Type.FLOAT.equals(Type.getTypeForName("float")));
    super.assertTrue(Type.FLOAT.equals(Type.getTypeForTypeName(float.class
        .getName())));
  }

  public void testDouble() {
    super.assertTrue(Type.DOUBLE.equals(Type.getTypeForName("double")));
    super.assertTrue(Type.DOUBLE.equals(Type.getTypeForTypeName(double.class
        .getName())));
  }
}
