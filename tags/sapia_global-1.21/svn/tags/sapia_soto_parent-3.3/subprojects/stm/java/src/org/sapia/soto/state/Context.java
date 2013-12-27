package org.sapia.soto.state;

import java.util.Locale;
import java.util.Map;

/**
 * This interface models an execution context. An instance of this interface
 * keeps variables in <code>Scope</code> instances. Each scope is internally
 * bound under a given name; the scopes used to perform object lookups can be
 * explicitely specified to this instance, using the
 * <code>get(Object key, String[] scopeNames)</code> method.
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
public interface Context {
  /**
   * This constant specifies the character that corresponds to the "stack
   * scope". The character is: .
   */
  public static final char STACK_SCOPE = '.';

  // Scope-related methods //////////////////////////////////////

  /**
   * Returns the object corresponding to the given key.
   * 
   * @param key
   *          an <code>Object</code>.
   * @return the <code>Object</code> that matches the given key, or
   *         <code>null</code> if no corresponding object is found.
   */
  public Object get(Object key);

  /**
   * Returns the object corresponding to the given key; searches all the scopes
   * whose names are given.
   * 
   * @param key
   *          an <code>Object</code>.
   * @param scopes
   *          an array of scope names.
   * @return the <code>Object</code> that matches the given key, or
   *         <code>null</code> if no corresponding object is found.
   */
  public Object get(Object key, String[] scopes);

  /**
   * Returns the object corresponding to the given key; searches the scope whose
   * name is given.
   * <p>
   * If the scope name corresponds to the "." string, than this method will
   * return the object that is currently on stack, or <code>null</code> if no
   * object is on stack.
   * 
   * @see #currentObject()
   * 
   * @param key
   *          an <code>Object</code>.
   * @param scope
   *          the name of the scope in which to search.
   * @return the <code>Object</code> that matches the given key, or
   *         <code>null</code> if no corresponding object is found.
   */
  public Object get(Object key, String scope);
  

  /**
   * Puts the given value under the given key, in the specified scope.
   *
   * @param key the key under which the value should be bound.
   * @param value an arbitratry object.
   * @param scope the name of the scope under in which the value should be put.
   */
  public void put(Object key, Object value, String scope);
  

  /**
   * @return the <code>Scope</code> s that this instance contains in a
   *         <code>Map</code>, under their corresponding name (the name is
   *         the key under which each <code>Scope</code> is bound in the
   *         <code>Map</code>.
   */
  public Map getScopes();

  // Stack-related methods //////////////////////////////////////

  /**
   * Pushes the given object on the internal stack that this instance keeps.
   * 
   * @param obj
   *          an <code>Object</code>.
   */
  public void push(Object obj);
  
  /**
   * Pops the given object from the internal stack that this instance keeps.
   */
  public Object pop();

  /**
   * @return The current object on this instance's internal stack.
   * 
   * @throws IllegalStateException
   *           if this instance's internal stack is empty.
   */
  public Object currentObject() throws IllegalStateException;

  /**
   * @return <code>true</code> if at least one object is present on the
   *         context's stack.
   */
  public boolean hasCurrentObject();

  /**
   * Sets this instance's locale.
   * 
   * @param locale
   *          this instance's <code>Locale</code>.
   */
  public void setLocale(Locale locale);

  /**
   * @return this instance's <code>Locale</code>.
   */
  public Locale getLocale();

}
