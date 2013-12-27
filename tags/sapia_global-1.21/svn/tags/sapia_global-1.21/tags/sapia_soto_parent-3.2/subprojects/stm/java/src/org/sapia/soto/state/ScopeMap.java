package org.sapia.soto.state;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.jexl.JexlContext;

/**
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
public class ScopeMap implements java.util.Map, JexlContext {
  private Map      _scopes;
  private String[] _scopeNames;

  public ScopeMap(Map scopes, String[] scopeNames) {
    _scopes = scopes;
    _scopeNames = scopeNames;
  }

  /**
   * @see org.apache.commons.jexl.JexlContext#getVars()
   */
  public Map getVars() {
    return this;
  }

  /**
   * @see org.apache.commons.jexl.JexlContext#setVars(java.util.Map)
   */
  public void setVars(Map arg0) {
  }

  /**
   * @see java.util.Map#clear()
   */
  public void clear() {
  }

  /**
   * @see java.util.Map#containsKey(java.lang.Object)
   */
  public boolean containsKey(Object key) {
    Scope sc;

    if(_scopeNames != null) {
      for(int i = 0; i < _scopeNames.length; i++) {
        sc = (Scope) _scopes.get(_scopeNames[i]);

        if(sc != null) {
          if(sc.getVal(key) != null) {
            return true;
          }
        }
      }
    } else {
      for(Iterator iter = _scopes.values().iterator(); iter.hasNext();) {
        sc = (Scope) iter.next();

        if(sc.getVal(key) != null) {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * @see java.util.Map#containsValue(java.lang.Object)
   */
  public boolean containsValue(Object arg0) {
    return false;
  }

  /**
   * @see java.util.Map#entrySet()
   */
  public Set entrySet() {
    return new HashSet();
  }

  /**
   * @see java.util.Map#get(java.lang.Object)
   */
  public Object get(Object key) {
    Scope sc;
    Object toReturn = null;

    if(_scopeNames != null) {
      for(int i = 0; i < _scopeNames.length; i++) {
        sc = (Scope) _scopes.get(_scopeNames[i]);

        if(sc != null) {
          if((toReturn = sc.getVal(key)) != null) {
            return toReturn;
          }
        }
      }
    } else {
      for(Iterator iter = _scopes.values().iterator(); iter.hasNext();) {
        sc = (Scope) iter.next();

        if((toReturn = sc.getVal(key)) != null) {
          return toReturn;
        }
      }
    }

    return toReturn;
  }

  /**
   * @see java.util.Map#isEmpty()
   */
  public boolean isEmpty() {
    return false;
  }

  /**
   * @see java.util.Map#keySet()
   */
  public Set keySet() {
    return new HashSet();
  }

  /**
   * @see java.util.Map#put(java.lang.Object, java.lang.Object)
   */
  public Object put(Object arg0, Object arg1) {
    throw new UnsupportedOperationException(
        "public Object put(Object arg0, Object arg1)");
  }

  /**
   * @see java.util.Map#putAll(java.util.Map)
   */
  public void putAll(Map arg0) {
    throw new UnsupportedOperationException("public void putAll(Map arg0)");
  }

  /**
   * @see java.util.Map#remove(java.lang.Object)
   */
  public Object remove(Object arg0) {
    throw new UnsupportedOperationException("public Object remove(Object arg0)");
  }

  /**
   * @see java.util.Map#size()
   */
  public int size() {
    return 0;
  }

  /**
   * @see java.util.Map#values()
   */
  public Collection values() {
    return new HashSet();
  }
}
