package org.sapia.soto.state;

/**
 * Specifies basic variable binding behavior.
 * 
 * @see org.sapia.soto.state.ContextImpl
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
public interface Scope {
  /**
   * Returns the value that corresponds to the given key.
   * 
   * @param key
   *          an <code>Object</code>.
   * @return the <code>Object</code> that corresponds to the given key, or
   *         <code>null</code> if no object was found.
   */
  public Object getVal(Object key);

  /**
   * Puts the value in this scope, under the given key.
   * 
   * @param key
   *          an <code>Object</code>.
   * @param value
   *          an <code>Object</code> that is bound under the given key.
   */
  public void putVal(Object key, Object value);
}
