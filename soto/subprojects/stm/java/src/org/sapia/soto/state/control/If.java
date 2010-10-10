package org.sapia.soto.state.control;

import org.apache.commons.jexl.Expression;
import org.apache.commons.jexl.ExpressionFactory;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;

import org.sapia.soto.state.Result;
import org.sapia.soto.state.ScopeMap;
import org.sapia.soto.state.helpers.CompositeStep;

import java.util.Map;

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
public class If extends CompositeStep {
  private Expression _expr;
  private String[]   _scopes;

  public If() {
  }

  /**
   * @see org.sapia.soto.state.Step#getName()
   */
  public String getName() {
    return ClassUtils.getShortClassName(getClass());
  }

  public void setTest(String expr) throws Exception {
    _expr = ExpressionFactory.createExpression(expr);
  }

  public void setScopes(String scopes) {
    _scopes = StringUtils.split(scopes, ",");

    for(int i = 0; i < _scopes.length; i++) {
      _scopes[i] = _scopes[i].trim();
    }
  }

  /**
   * @see org.sapia.soto.state.helpers.CompositeStep#doExecute(Result)
   */
  protected boolean doExecute(Result st) {
    Map scopes = st.getContext().getScopes();
    ScopeMap map = new ScopeMap(scopes, _scopes);
    Object evaled = null;

    try {
      evaled = _expr.evaluate(map);

      Boolean b = (Boolean) evaled;

      return b.booleanValue();
    } catch(ClassCastException e) {
      st.error("'if' must evaluate to a Boolean, not a " + evaled);

      return false;
    } catch(Exception e) {
      e.printStackTrace();
      st.error("Could not evaluate: '" + _expr.getExpression() + "'", e);

      return false;
    }
  }
}
