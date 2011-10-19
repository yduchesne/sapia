package org.sapia.soto.config;

import org.sapia.resource.include.IncludeContext;
import org.sapia.resource.include.IncludeState;
import org.sapia.soto.Env;
import org.sapia.soto.SotoConsts;
import org.sapia.soto.util.Utils;
import org.sapia.util.text.SystemContext;
import org.sapia.util.text.TemplateContextIF;
import org.sapia.util.xml.confix.Dom4jProcessor;

/**
 * @author Yanick Duchesne
 */
public class SotoIncludeContext extends IncludeContext implements SotoConsts{

  TemplateContextIF  _ctx;
  Object             _lastObject;
  //Include            _include;
  Env                _env;

  SotoIncludeContext(TemplateContextIF ctx, Env env) {
    _ctx = ctx;
    _env = env;
  }
  
  public TemplateContextIF getTemplateContext(){
    return _ctx;
  }
  
  public static TemplateContextIF currentTemplateContext(){
    IncludeContext ctx = IncludeState.currentContext(SOTO_INCLUDE_KEY);
    if(ctx == null || !(ctx instanceof SotoIncludeContext)){
      return new SystemContext();
    }
    else{
      return ((SotoIncludeContext)ctx).getTemplateContext();
    }
  }
  
  public static Env currentEnv(){
    IncludeContext ctx = IncludeState.currentContext(SOTO_INCLUDE_KEY);
    if(ctx == null || !(ctx instanceof SotoIncludeContext)){
      return null;
    }
    else{
      return ((SotoIncludeContext)ctx)._env;
    }
  }  
  
  public static SotoIncludeContext currentContext(){
    return (SotoIncludeContext)IncludeState.currentContext(SOTO_INCLUDE_KEY);
  }  

  /**
  public static Include include(Env parentEnv,
                                String uri, 
                                TemplateContextIF ctx) 
                                throws ConfigurationException{
                                */
  protected Object doInclude(java.io.InputStream is, Object ctx) throws java.io.IOException ,Exception {
    Dom4jProcessor proc = new Dom4jProcessor(_env.getObjectFactory());
    return proc.process(Utils.replaceVars(ctx == null || !(ctx instanceof TemplateContextIF) ? 
        _ctx : 
        (TemplateContextIF)ctx, is, getUri()));
  }
  
   /*SotoIncludeContext parent = null;
    Stack states = stack();
    Env env;
    
    if(Debug.DEBUG){
      Debug.debug(SotoIncludeContext.class, "Including: " + uri);
    }
    
    if(states.size() > 0){
      parent = (SotoIncludeContext)states.peek();
      String baseUri = SotoIncludeContext.currentState().getInclude().getUri();
      if(Debug.DEBUG){
        Debug.debug(SotoIncludeContext.class, "Parent URI: " + baseUri);
      }
      baseUri = Utils.getRelativePath(baseUri, uri, true);
      env = new ServiceEnv(parent._env, baseUri);
    }
    else{
      if(Debug.DEBUG){
        Debug.debug(SotoIncludeContext.class, "No parent include");
      }
      env = parentEnv;
    }

    SotoIncludeContext state = new SotoIncludeContext(parent, ctx, env);
    state.setInclude(new Include(state, env, uri, ctx));
    return state.getInclude();
  }
  

  public Include getLastInclude(){
    if(_parent != null){
      return _parent.getInclude();
    }
    return null;
  }
  
  public Include getInclude(){
    return _include;
  }
  
  void setInclude(Include incl){
    _include = incl;
  }

  static void push(SotoIncludeContext st) {
    stack().push(st);
  }

  static void pop() {
    Stack stack = stack();

    if(stack.size() > 0) {
      stack().pop();
    }
  }

  TemplateContextIF getTemplateContext() {
    if(_ctx == null) {
      return new SystemContext();
    }

    return _ctx;
  }

  public static TemplateContextIF currentTemplateContext() {
    return currentState().getTemplateContext();
  }

  public static SotoIncludeContext currentState() {
    Stack st = stack();

    if(st.size() == 0) {
      return new SotoIncludeContext();
    }

    return (SotoIncludeContext) st.peek();
  }
  
  public Env getEnv(){
    return _env;
  }

  public void setLastObject(Object o) {
    _lastObject = o;
  }

  public Object getLastObject() {
    return _lastObject;
  }

  static Stack stack() {
    Stack st = (Stack) _state.get();

    if(st == null) {
      _state.set(st = new Stack());
    }

    return st;
  }*/
}
