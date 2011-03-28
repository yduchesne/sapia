package org.sapia.soto.jmx;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * A utility class that performs various operations using the Java reflection
 * API.
 * 
 * @author Yanick Duchesne
 *         <dl>
 *         <dt><b>Copyright: </b>
 *         <dd>Copyright &#169; 2002-2003 <a
 *         href="http://www.sapia-oss.org">Sapia Open Source Software </a>. All
 *         Rights Reserved.</dd>
 *         </dt>
 *         <dt><b>License: </b>
 *         <dd>Read the license.txt file of the jar or visit the <a
 *         href="http://www.sapia-oss.org/license.html">license page </a> at the
 *         Sapia OSS web site</dd>
 *         </dt>
 *         </dl>
 */
public class MethodUtils {
  public static final String SET_PREFIX = "set";
  public static final String GET_PREFIX = "get";

  /**
   * Returns the name of the MBean attribute corresponding to the given method
   * object - chops the given prefix from the method name and returns the
   * result.
   * 
   * @param method
   *          a <code>Method</code> object.
   * @param removePrefix
   *          the prefix to remove from the method's name - to result in a MBean
   *          attribute name.
   * @return a MBean attribute name.
   */
  public static String getAttributeName(Method method, String removePrefix) {
    return method.getName().substring(removePrefix.length());
  }

  /**
   * Returns <code>true</code> if the given method object corresponds to a
   * setter. The method must start with a "set" prefix and be non-static,
   * public, and take a single parameter.
   * 
   * @param method
   *          a <code>Method</code> object.
   * @return <code>true</code> if the given instance corresponds to a setter.
   */
  public static boolean isSetter(Method method) {
    return method.getName().startsWith("set")
        && Modifier.isPublic(method.getModifiers())
        && !Modifier.isStatic(method.getModifiers())
        && (method.getParameterTypes().length == 1);
  }
  
  
  /**
   * @return <code>true</code> if all the <code>Class</code> instances passed
   * in represent primitive types, <code>false</code> otherwise.
   */
  public static boolean isPrimitives(Class[] params){
    for(int i = 0; i < params.length; i++){
      if(!params[i].isPrimitive()){
        return false;
      }
    }
    return true;
  }

  /**
   * Returns <code>true</code> if the given method object corresponds to a
   * getter. The method must start with a "get" prefix and be non-static,
   * public, take no parameter, and have a return type.
   * 
   * @param method
   *          a <code>Method</code> object.
   * @return <code>true</code> if the given instance corresponds to a getter.
   */
  public static boolean isGetter(Method method) {
    return method.getName().startsWith("get")
        && Modifier.isPublic(method.getModifiers())
        && !Modifier.isStatic(method.getModifiers())
        && (method.getParameterTypes().length == 0)
        && (method.getReturnType() != null)
        && !method.getReturnType().equals(void.class);
  }

  /**
   * Returns <code>true</code> if the given method object corresponds to a
   * "is". The method must start with a "is" prefix and be non-static, public,
   * take no parameter, and have a boolean return type.
   * 
   * @param method
   *          a <code>Method</code> object.
   * @return <code>true</code> if the given instance corresponds to a "is".
   */
  public static boolean isBoolean(Method method) {
    return method.getName().startsWith("is")
        && Modifier.isPublic(method.getModifiers())
        && !Modifier.isStatic(method.getModifiers())
        && (method.getParameterTypes().length == 0)
        && (method.getReturnType() != null)
        && !method.getReturnType().equals(void.class);
  }

  /**
   * Returns <code>true</code> if the given method object is not a
   * getter/setter/is, and if it is public and non-static.
   * 
   * @param method
   *          a <code>method</code>.
   * @return <code>true</code> if the given method corresponds to a MBean
   *         operation.
   */
  public static boolean isOperation(Method method) {
    return Modifier.isPublic(method.getModifiers())
        && !Modifier.isStatic(method.getModifiers()) 
        && !isSetter(method)
        && !isGetter(method) 
        && !isBoolean(method);
  }

  /**
   * Returns the name of the given class, minus the package name.
   * 
   * @param clazz <code>Class</code> object.
   */
  public static String getShortClassName(Class clazz) {
    int idx = clazz.getName().lastIndexOf('.');

    if(idx < 0) {
      return clazz.getName();
    } else {
      return clazz.getName().substring(idx + 1);
    }
  }

  /**
   * Returns the "pretty" name for the given attribute/operation.
   * 
   * @param name
   *          the attribute name/operation to format.
   * @return the formatted operation/attribute name.
   */
  public static String pretty(String name) {
    return name;
  }
}
