package org.sapia.soto.jmx;

import java.lang.reflect.Method;

/**
 * Encapsulats information pertaining to a method.
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
public class MethodInfo {
  private String   _name;
  private String[] _params;
  private int      _hashCode;

  /**
   * Creates an instance of this class with the given method object.
   * 
   * @param method
   *          a <code>Method</code> instance.
   */
  public MethodInfo(Method method) {
    this(method.getName(), method.getParameterTypes());
  }

  /**
   * Creates an instance of this class corresponding to the method with the
   * given name, and the given signature - represented as an array of class
   * objects.
   * 
   * @param name
   *          a method name
   * @param params
   *          an array of <code>Class</code> objects corresponding to this
   *          instance's corresponding method signature.
   */
  public MethodInfo(String name, Class[] params) {
    _name = name;
    _hashCode = name.hashCode();
    _params = paramsAsString(params);
  }

  /**
   * Creates an instance of this class corresponding to the method with the
   * given name, and the given signature - represented as an array of class
   * names.
   * 
   * @param name
   *          a method name
   * @param sig
   *          an array of <code>String</code> s corresponding to this
   *          instance's corresponding method signature.
   */
  public MethodInfo(String name, String[] sig) {
    _name = name;
    _hashCode = name.hashCode();
    _params = sig;
  }

  public int hashCode() {
    return _hashCode;
  }

  public boolean equals(Object object) {
    try {
      MethodInfo info = (MethodInfo) object;

      return _name.equals(info._name)
          && (_params.length == info._params.length)
          && paramsEqual(info._params);
    } catch(ClassCastException e) {
      return false;
    }
  }

  static String[] paramsAsString(Class[] params) {
    String[] sig = new String[params.length];

    for(int i = 0; i < params.length; i++) {
      sig[i] = params[i].getName();
    }

    return sig;
  }

  private boolean paramsEqual(String[] params) {
    for(int i = 0; i < params.length; i++) {
      if(!_params[i].equals(params[i])) {
        return false;
      }
    }

    return true;
  }
}
