package org.sapia.validator;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;

/**
 * This class models the execution context of <code>Validatable</code>
 * instances. An instance of this class is not thread-safe.
 *
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class ValidationContext {
  private Stack  _stack  = new Stack();
  private Vlad   _config;
  private Locale _locale;
  private Status _stat;
  private Map _globals;
  private Map _contextMap = new HashMap();
  /**
   * Constructor for ValidationContext.
   */
  public ValidationContext(Map globals, 
                           Object toValidate, 
                           Vlad cfg, 
                           Locale locale) {
    this(globals, toValidate, cfg, locale, null);
  }
  
  /**
   * Constructor for ValidationContext with contextual object map.
   */
  public ValidationContext(Map globals, 
                           Object toValidate, 
                           Vlad cfg, 
                           Locale locale,
                           Map contextMap) {
     _stack.push(toValidate);
     _config   = cfg;
     _stat     = new Status(this);
     _locale   = locale;
     _globals  = globals;
     if (contextMap != null) {
       _contextMap.putAll(contextMap);
     }
  } 

  /**
   * Returns the current object on this context's stack.
   *
   * @return an <code>Object</code>.
   */
  public Object get() {
    return _stack.peek();
  }

  /**
   * Pushes the given object on this context's stack.
   *
   * @param toValidate an <code>Object</code> to validate.
   */
  public void push(Object toValidate) {
    _stack.push(toValidate);
  }

  /**
   * Pops the current object from this context's stack.
   *
   * @return an <code>Object</code>.
   */
  public Object pop() {
    return _stack.pop();
  }
  
  /**
   * @param name the name of a global value.
   *
   * @see Vlad#getGlobal(String)
   */
  public Object getGlobal(String name){
    return _globals.get(name);
  }

  /**
   * This method first attempts looking up in the map of this instance; if no
   * object is found, it resorts to the global map.
   * 
   * @param name the name of the desired value.
   * @return the <code>Object</code> corresponding to the given name,
   * or <code>null</code> if no such object exists.
   */
  public Object get(String name){
    Object val = _contextMap.get(name);
    if(val == null){
      return getGlobal(name);
    }
    return val;
  }  

  /**
   * Returns this instance's validator.
   *
   * @return a <code>Vlad</code>.
   */
  public Vlad getConfig() {
    return _config;
  }

  /**
   * Returns this instance's <code>Locale</code>.
   *
   * @return a <code>Locale</code>.
   */
  public Locale getLocale() {
    return _locale;
  }

  /**
   * Returns this instance's execution status.
   *
   * @return a <code>Status</code>.
   */
  public Status getStatus() {
    return _stat;
  }
}
