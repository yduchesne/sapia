/*
 * ParamString.java
 *
 * Created on June 10, 2005, 9:08 AM
 */

package org.sapia.soto.state.util;

import org.sapia.soto.state.Context;
import org.sapia.soto.state.helpers.ScopeParser;
import org.sapia.util.text.TemplateContextIF;
import org.sapia.util.text.TemplateElementIF;
import org.sapia.util.text.TemplateException;
import org.sapia.util.text.TemplateFactory;

/**
 * This class implements a parametrized string, that takes an input
 * string holding variables, and that performs interpolation of these
 * variables using an STM <code>Context</code> instance. 
 * <p>
 * Input string must have a format similar to the following: 
 * <pre>
 *  the first param is {first}, the second param is {second}.
 * </pre>
 * <p>
 * In the above, the "{first}" and "{second}" correspond to keys of values
 * bound to the <code>Context</code>.
 *
 * @see Context
 *
 * @author yduchesne
 */
public class ParamString {
  
  private TemplateElementIF _template;
  
  private String[] _scopes;
  
  /** Creates a new instance of ParamString */
  public ParamString(String parametrized) {
    TemplateFactory fac = new TemplateFactory("{", "}");
    _template = fac.parse(parametrized);
  }
  
  /**
   * @param scopes the coma-delimited list of scopes that should be searched
   * within the context - this instances searches all scopes by default.
   */
  public void setScopes(String scopes){
    _scopes = ScopeParser.parse(scopes);
  }
  
  /**
   * Performs rendering of the internal parametrized string, given the
   * context.
   *
   * @param a <code>Context</code>
   */
  public String render(Context ctx) throws TemplateException{
    StmTemplateContext templateCtx = new StmTemplateContext(_scopes, ctx);
    return _template.render(templateCtx);
  }

  static class StmTemplateContext implements TemplateContextIF{
    
    private Context _ctx;
    private String[] _scopes;
    
    StmTemplateContext(String[] scopes, Context ctx){
      _ctx = ctx;
      _scopes = scopes;
    }
    
    public Object getValue(String str) {
      Object o = null;
      if(_scopes == null){
        o = _ctx.get(str);
      }
      else{
        o = _ctx.get(str, _scopes);
      }
      return o == null ? str : o;
    }

    public void put(String str, Object obj) {
    }
    
  }
}
