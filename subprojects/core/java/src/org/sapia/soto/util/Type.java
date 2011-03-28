package org.sapia.soto.util;

import java.util.HashMap;
import java.util.Map;

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
public class Type {
  public static Type BOOLEAN         = new Type("boolean", boolean.class
                                         .getName());
  public static Type BYTE            = new Type("byte", byte.class.getName());
  public static Type SHORT           = new Type("short", short.class.getName());
  public static Type INT             = new Type("int", int.class.getName());
  public static Type LONG            = new Type("long", long.class.getName());
  public static Type FLOAT           = new Type("float", float.class.getName());
  public static Type DOUBLE          = new Type("double", double.class
                                         .getName());
  private static Map _nameToType     = new HashMap();
  private static Map _typeNameToType = new HashMap();

  static {
    _nameToType.put("boolean", BOOLEAN);
    _nameToType.put("byte", BYTE);
    _nameToType.put("short", SHORT);
    _nameToType.put("int", INT);
    _nameToType.put("long", LONG);
    _nameToType.put("float", FLOAT);
    _nameToType.put("double", DOUBLE);

    _typeNameToType.put(boolean.class.getName(), BOOLEAN);
    _typeNameToType.put(byte.class.getName(), BYTE);
    _typeNameToType.put(short.class.getName(), SHORT);
    _typeNameToType.put(int.class.getName(), INT);
    _typeNameToType.put(long.class.getName(), LONG);
    _typeNameToType.put(float.class.getName(), FLOAT);
    _typeNameToType.put(double.class.getName(), DOUBLE);
  }

  private String     _name;
  private String     _className;

  Type(String name, String className) {
    _name = name;
    _className = className;
  }

  public String getName() {
    return _name;
  }

  public String getClassName() {
    return _className;
  }

  public boolean equals(Object o) {
    try {
      return ((Type) o).getClassName().equals(_className)
          && ((Type) o).getName().equals(_name);
    } catch(ClassCastException e) {
      return false;
    }
  }

  public static boolean hasTypeForTypeName(String typeName) {
    return _typeNameToType.get(typeName) != null;
  }

  public static boolean hasTypeForName(String name) {
    return _nameToType.get(name) != null;
  }

  public static Type getTypeForTypeName(String typeName) {
    Type t = (Type) _typeNameToType.get(typeName);

    if(t == null) {
      throw new IllegalArgumentException("Unkown type: " + typeName);
    }

    return t;
  }

  public static Type getTypeForName(String name) {
    Type t = (Type) _nameToType.get(name);

    if(t == null) {
      throw new IllegalArgumentException("Unkown type: " + name);
    }

    return t;
  }
}
