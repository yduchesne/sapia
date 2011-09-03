package org.sapia.regis.bean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.sapia.regis.Node;
import org.sapia.regis.RegisSession;
import org.sapia.regis.Registry;
import org.sapia.regis.SessionUtil;
import org.sapia.regis.type.BuiltinType;
import org.sapia.regis.type.BuiltinTypes;

public class NodeInvocationHandler implements InvocationHandler{
  
  static final String REG_PROVIDER_METHOD = "getRegistry";
  
  private Registry _reg;
  private Node _delegate;
  private Map _methodsToProps = new HashMap();
  
  public NodeInvocationHandler(Registry reg, Node delegate, Class interf){
    _delegate = delegate;
    _reg      = reg;
    Method[] methods = interf.getMethods();
    for(int i = 0; i < methods.length; i++){
      Method m = methods[i];
      if(m.getDeclaringClass().equals(Object.class)){
        continue;
      }
      else{
        if(m.getName().startsWith("get") && 
            m.getName().length()>3 && 
            !m.getReturnType().equals(void.class)){
          String propName = propName(m.getName().substring(3));
          BuiltinType type = BuiltinTypes.getTypeFor(m.getReturnType());
          if(type == null){
            throw new InvalidReturnTypeException("Property " + propName +
                " does not correspond to a valid configuration type: " + m.getReturnType().getName());
          }
          PropertyConf conf = new PropertyConf();
          conf.name = propName;
          conf.type = type;
          _methodsToProps.put(m, conf);
        }
      }
    }
  }
  
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    
    PropertyConf conf = (PropertyConf)_methodsToProps.get(method);
    if(conf != null){
      if(SessionUtil.isJoined()){
        return conf.getProperty(_delegate);
      }
      else{
        RegisSession session = _reg.open();
        try{
          return conf.getProperty(_delegate);
        }finally{
          session.close();
        }
      }
    }
    else if(method.getName().equals(REG_PROVIDER_METHOD)){
      return _reg;
    }
    else{
      throw new InvalidPropertyException("No method corresponds to property " + conf.name + " on Node: " + _delegate.getAbsolutePath());
    }
  }
  
  public String propName(String name){
    StringBuffer buf = new StringBuffer(name);
    buf.setCharAt(0, Character.toLowerCase(name.charAt(0)));
    return buf.toString();
  }
  
  static class PropertyConf{
    private String name;
    private BuiltinType type;
    
    Object getProperty(Node delegate){
      return type.getProperty(name, delegate);
    }
  }
  
}
