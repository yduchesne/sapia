package org.sapia.soto.state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;

/**
 * This class implements the <code>Context</code> interface. It internally has
 * a <code>ContextScope</code> available, under the <code>context</code>
 * name. In addition, client applications can add their own <code>Scope</code>
 * implementations.
 * 
 * @see org.sapia.soto.state.Scope
 * @see #addScope(String, Scope)
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
public class ContextImpl implements Context {

  public static final String SCOPE_CONTEXT = "context";

  private Map                _scopes       = new HashMap();
  private List               _scopeList    = new ArrayList();
  private Locale             _locale       = Locale.getDefault();
  protected Stack            _stack        = new Stack();

  public ContextImpl() {
    addScope(SCOPE_CONTEXT, new ContextScope(this));
  }

  /**
   * @param scope
   *          the name of the scope to which the given map is associated.
   * @param vals
   *          a <code>Map</code>.
   */
  public void addScope(String scope, Scope vals) throws IllegalStateException {
    if(_scopes.get(scope) != null) {
      throw new IllegalStateException("Scope already exists: " + scope);
    }

    _scopes.put(scope, vals);
    _scopeList.add(vals);
  }

  /**
   * Returns the map that corresponds to the given scope.
   * 
   * @param name
   *          the name of a scope.
   * @return the <code>Scope</code> that corresponds to the desired scope, or
   *         <code>null</code> if none was found for the given scope.
   */
  protected Scope getScope(String name) {
    return (Scope) _scopes.get(name);
  }
  
  /**
   * @see org.sapia.soto.state.Context#get(java.lang.Object)
   */
  public Object get(Object key) {
    Scope scope;
    Object toReturn = null;

    for(int i = 0; i < _scopeList.size(); i++) {
      scope = (Scope) _scopeList.get(i);

      if((toReturn = scope.getVal(key)) != null) {
        return toReturn;
      }
    }

    return toReturn;
  }
  
  /**
   * @see org.sapia.soto.state.Context#get(java.lang.Object,java.lang.Object,java.lang.String)
   */
  public void put(Object key, Object value, String scope){
    Scope sc = getScope(scope);
    if(sc != null){
      sc.putVal(key, value);
    }
  }

  /**
   * @see org.sapia.soto.state.Context#get(java.lang.Object, java.lang.String[])
   */
  public Object get(Object key, String[] scopes) {
    Scope scope;
    Object toReturn = null;

    for(int i = 0; i < scopes.length; i++) {
      scope = getScope(scopes[i]);

      if((scope != null) && ((toReturn = scope.getVal(key)) != null)) {
        return toReturn;
      }
    }

    return toReturn;
  }

  /**
   * @see org.sapia.soto.state.Context#get(java.lang.Object, java.lang.String)
   */
  public Object get(Object key, String scope) {

    Scope sc = getScope(scope);

    if(sc != null) {
      return sc.getVal(key);
    }

    return null;
  }

  /**
   * @see org.sapia.soto.state.Context#getScopes()
   */
  public Map getScopes() {
    return _scopes;
  }

  /**
   * @see org.sapia.soto.state.Context#push(java.lang.Object)
   */
  public void push(Object o) {
    _stack.push(o);
  }

  /**
   * @see org.sapia.soto.state.Context#pop()
   */
  public Object pop() {
    return _stack.pop();
  }

  /**
   * @see org.sapia.soto.state.Context#currentObject()
   */
  public Object currentObject() throws IllegalStateException {
    if(_stack.size() == 0) {
      throw new IllegalStateException("No object on context stack");
    }

    return _stack.peek();
  }

  /**
   * @see org.sapia.soto.state.Context#hasCurrentObject()
   */
  public boolean hasCurrentObject() {
    return _stack.size() > 0;
  }

  /**
   * @see org.sapia.soto.state.Context#setLocale(java.util.Locale)
   */
  public void setLocale(Locale locale) {
    _locale = locale;

  }

  /**
   * @see org.sapia.soto.state.Context#getLocale()
   */
  public Locale getLocale() {
    return _locale;
  }
}
