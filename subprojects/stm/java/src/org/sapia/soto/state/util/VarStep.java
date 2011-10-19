package org.sapia.soto.state.util;

import org.apache.commons.lang.ClassUtils;

import org.sapia.soto.state.Result;
import org.sapia.soto.state.Scope;
import org.sapia.soto.state.Step;

/**
 * @author Yanick Duchesne
 * 
 * <dl>
 * <dt><b>Copyright: </b>
 * <dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open
 * Source Software </a>. All Rights Reserved.</dd>
 * </dt>
 * <dt><b>License: </b>
 * <dd>Read the license.txt file of the jar or visit the <a
 * href="http://www.sapia-oss.org/license.html">license page </a> at the Sapia
 * OSS web site</dd>
 * </dt>
 * </dl>
 */
public class VarStep implements Step {
  private String _key;
  private Object _value;
  private String _scope;

  public void setKey(String key) {
    _key = key;
  }

  public void setValue(Object value) {
    _value = value;
  }

  public void setScope(String scope) {
    _scope = scope;
  }

  /**
   * @see org.sapia.soto.state.Step#getName()
   */
  public String getName() {
    return ClassUtils.getShortClassName(getClass());
  }

  /**
   * @see org.sapia.soto.state.Executable#execute(org.sapia.soto.state.Result)
   */
  public void execute(Result st) {
    if(_key == null) {
      throw new IllegalStateException("Variable key not specified");
    }

    if(_scope == null) {
      throw new IllegalStateException("Variable scope not specified");
    }

    if(_value == null) {
      throw new IllegalStateException("Variable value not specified");
    }

    Scope scope = (Scope) st.getContext().getScopes().get(_scope);

    if(scope == null) {
      throw new IllegalArgumentException("Scope not found: " + _scope);
    }

    scope.putVal(_key, _value);
  }
}
