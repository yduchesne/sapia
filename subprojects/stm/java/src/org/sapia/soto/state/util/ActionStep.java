package org.sapia.soto.state.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.ConvertUtils;
import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.state.AbstractStep;
import org.sapia.soto.state.Context;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.StmKey;
import org.sapia.soto.util.Utils;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

public class ActionStep extends AbstractStep
  implements ObjectCreationCallback, EnvAware{
  
  private MethodDescriptor _method;
  private Class _class;
  private Env _env;
  
  public void setEnv(Env env) {
    _env = env;
  }
 
  /**
   * @return the <code>Env</code> instance that was assigned to this
   * instance.
   */
  protected Env env(){
    return _env;
  }
  
  public MethodDescriptor createMethod(){
    _method = new MethodDescriptor();
    return _method;
  }
  
  /**
   * This method should be overridden by inheriting classes, if
   * required.
   * 
   * @throws ConfigurationException
   */
  public void init() throws Exception{}
  
  public void setClass(String className) throws Exception{
    _class = Class.forName(className);
  }
  
  public Object onCreate() throws ConfigurationException {
    if(_method == null){
      throw new ConfigurationException("Method configuration not set");
    }
    _method.init(_class != null ? _class : getClass());
    try{
      init();
    }catch(Exception e){
      if(e instanceof ConfigurationException){
        throw (ConfigurationException)e;
      }
      else{
        throw new ConfigurationException("Could not perform initialization", e);
      }
    }
    
    return this;
  }

  public void execute(Result res) {
    
    try{
      Object target = this;
      if(_class != null){
        try{
          target = _class.newInstance();
        }catch(InstantiationException e){
          throw e;
        }
      }
      Object val = _method.invoke(res, target);
      if(_method._push && val != null){
        res.getContext().push(val);
      }
    }catch(Exception e){
      Throwable err = e;
      if(e instanceof InvocationTargetException){
        err = ((InvocationTargetException)e).getTargetException();
      }
      res.error(err);
    }
  }
  
  public static class MethodDescriptor{
  
    static final Object[] EMPTY_PARAMS = new Object[0];
    static final int UNDEFINED = -1;
    private int _resultParamIndex = UNDEFINED;
    private int _contexParamIndex = UNDEFINED;
    private Class[] _sig;
    private String  _name;
    private List _params = new ArrayList();
    private Method _method;
    private boolean _push = true;
    
    public void setName(String name){
      _name = name;
    }
    
    public void setPush(boolean push){
      _push = push;
    }
    
    public void setSig(String sig) throws Exception{
      String[] classNames = Utils.split(sig, ',', true);
      _sig = new Class[classNames.length];
      for(int i = 0; i < classNames.length; i++){
        _sig[i] = Class.forName(classNames[i]);
      }
    }
    
    public void addParam(Object param){
      _params.add(param);
    }
  
    public void init(Class target) throws ConfigurationException {
      if(_name == null){
        throw new IllegalStateException("Method name not set");
      }
      try{
        findMethod(target);
      }catch(Exception e){
        throw new ConfigurationException("Problem trying to resolve method: " +
            _name + " on " + this, e);
      }
      if(_push && _method.getReturnType().equals(void.class)){
        _push = !_push;
      }
    }
    
    Method getMethod(){
      return _method;
    }
    
    Object invoke(Result res, Object target) throws Exception{
      Object[] params = _sig == null ? EMPTY_PARAMS : new Object[_sig.length];
      for(int i = 0; i < params.length ; i++){
        if(i == _resultParamIndex){
          params[i] = res;
        }
        else if (i == _contexParamIndex){
          params[i] = res.getContext();          
        }
        else{
          Object param = (Object)_params.get(i);
          Object val = null;
          if(param instanceof StmKey){
            val = ((StmKey)param).lookup(res);
          }
          else{
            val = param;
          }
          if(val != null && val instanceof String){
            val = ConvertUtils.convert((String)val, _sig[i]);
          }
          params[i] = val;
        }
      }
      return _method.invoke(target, params);
    }
    
    private void findMethod(Class target) throws Exception{
      if(_sig == null){
        Method[] meths = target.getMethods();
        for(int i = 0; i < meths.length; i++){
          if(meths[i].getName().equals(_name)){
            _method = meths[i];
            _sig = meths[i].getParameterTypes();
            break;
          }
        }
        if(_method == null){
          throw new NoSuchMethodException("Method " + _name + " not found on: " + target);
        }
      }
      else{
        _method = target.getMethod(_name, _sig);
      }
      _resultParamIndex = findParam(Result.class);
      _contexParamIndex = findParam(Context.class);
    }
    
    private int findParam(Class paramType){
      Class[] params = _method.getParameterTypes();
      for(int i = 0; i < params.length; i++){
        if(params[i].isAssignableFrom(paramType)){
          return i;
        } 
      }
      return UNDEFINED;
    }
  }

}
