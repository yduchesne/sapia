package org.sapia.soto.state.control;

import org.apache.commons.jexl.Expression;
import org.apache.commons.jexl.ExpressionFactory;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;

import org.sapia.soto.state.Result;
import org.sapia.soto.state.ScopeMap;
import org.sapia.soto.state.Step;
import org.sapia.soto.state.helpers.CompositeStep;

import java.util.ArrayList;
import java.util.List;
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
public class Choose implements Step {
  private List      _cases = new ArrayList();
  private Otherwise _other;

  /**
   *  
   */
  public Choose() {
  }

  /**
   * @see org.sapia.soto.state.Step#getName()
   */
  public String getName() {
    return ClassUtils.getShortClassName(getClass());
  }

  public When createWhen() {
    When caseObj = new When();
    _cases.add(caseObj);

    return caseObj;
  }

  public Otherwise createOtherwise() {
    return _other = new Otherwise();
  }

  /**
   * @see org.sapia.soto.state.Executable#execute(Result)
   */
  public void execute(Result st) {
    When caseObj;

    for(int i = 0; i < _cases.size(); i++) {
      caseObj = (When) _cases.get(i);

      if(caseObj.isTrue(st)) {
        caseObj.execute(st);

        return;
      }
    }

    if(_other != null) {
      _other.execute(st);
    }
  }

  // INNER CLASSES //////////////////////////////////////////////////////////
  public static class Otherwise extends CompositeStep {
    /**
     * @see org.sapia.soto.state.Step#getName()
     */
    public String getName() {
      return ClassUtils.getShortClassName(getClass());
    }

    /**
     * @see org.sapia.soto.state.helpers.CompositeStep#doExecute(Result)
     */
    protected boolean doExecute(Result st) {
      return true;
    }
  }

  public static class When extends CompositeStep {
    private Expression _expr;
    private String[]   _scopes;

    public When() {
    }

    /**
     * @see org.sapia.soto.state.Step#getName()
     */
    public String getName() {
      return ClassUtils.getShortClassName(getClass());
    }

    public boolean isTrue(Result st) {
      Map scopes = st.getContext().getScopes();
      ScopeMap map = new ScopeMap(scopes, _scopes);
      Object evaled = null;

      try {
        evaled = _expr.evaluate(map);

        Boolean b = (Boolean) evaled;

        return b.booleanValue();
      } catch(ClassCastException e) {
        st.error("'when' must evaluate to a Boolean, not a " + evaled);

        return false;
      } catch(Exception e) {
        e.printStackTrace();
        st.error("Could not evaluate: '" + _expr.getExpression() + "' - "
            + e.getMessage());

        return false;
      }
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
      return true;
    }
  }
}
