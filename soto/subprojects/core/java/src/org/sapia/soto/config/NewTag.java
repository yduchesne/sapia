package org.sapia.soto.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.XmlAware;
import org.sapia.soto.util.TypeConverters;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;
import org.sapia.util.xml.confix.ObjectHandlerIF;
import org.sapia.util.xml.confix.ObjectWrapperIF;

public class NewTag implements ObjectWrapperIF, ObjectCreationCallback, EnvAware, XmlAware{

  private String _className;
  private Constructor _cons = new Constructor();
  private Object _obj;
  private String _name, _prefix, _uri;
  private Env _env;
  
  public void setClass(String className) throws ConfigurationException{
    if(_className != null){ throw new ConfigurationException("Class name already set");}
    _className = className;
  }
  
  public NewTag.Arg createArg() throws ConfigurationException{
    return _cons.createArg();
  }
  
  public void setEnv(Env env) {
    _env = env;
  }
  
  public void setNameInfo(String name, String prefix, String uri) {
    _name = name;
    _prefix = prefix;
    _uri = uri;
  }
 
  public Object getWrappedObject() {
    try{
      return onCreate();
    }catch(ConfigurationException e){
      throw new IllegalStateException("Could not acquire new object instance");
    }
  }
  
  public Object onCreate() throws ConfigurationException {
    if(_obj == null){
      if(_className == null){
        throw new IllegalStateException("Could not instantiate object; class not set");
      }
      try{
        _obj = _cons.newInstance(_className);
      }catch(Exception e){
        throw new IllegalStateException("Could not instantiate object " + _className, e);
      }
    }
    if(_obj instanceof XmlAware){
      ((XmlAware)_obj).setNameInfo(_name, _prefix, _uri);
    }
    if(_obj instanceof EnvAware){
      ((EnvAware)_obj).setEnv(_env);
    }
    return _obj;
  }
  
  public static final class Constructor{
    private List _args = new ArrayList(3);
    
    public Arg createArg(){
      Arg arg = new Arg();
      _args.add(arg);
      return arg;
    }
    
    public Object newInstance(String className) throws Exception{
      Class clazz = Class.forName(className);
      Class[] types = new Class[_args.size()];
      Object[] values = new Object[_args.size()];
      for(int i = 0; i < _args.size(); i++){
        Arg a = (Arg)_args.get(i);
        types[i] = a.getType();
        values[i] = a.coerce();
      }
      
      return clazz.getConstructor(types).newInstance(values);
    }
  }
  
  public static final class Arg implements ObjectHandlerIF{
    
    private static final Map PRIMITIVES = new HashMap();
    private static final Map PRIMITIVE_WRAPPERS = new HashMap();
    
    static{
      PRIMITIVES.put("boolean", boolean.class);
      PRIMITIVES.put("byte", byte.class);
      PRIMITIVES.put("short", short.class);
      PRIMITIVES.put("int", int.class);
      PRIMITIVES.put("long", long.class);
      PRIMITIVES.put("float", float.class);
      PRIMITIVES.put("double", double.class);
      
      PRIMITIVE_WRAPPERS.put(Boolean.class, boolean.class);
      PRIMITIVE_WRAPPERS.put(Byte.class, byte.class);
      PRIMITIVE_WRAPPERS.put(Short.class, short.class);
      PRIMITIVE_WRAPPERS.put(Integer.class, int.class);
      PRIMITIVE_WRAPPERS.put(Long.class, long.class);
      PRIMITIVE_WRAPPERS.put(Float.class, float.class);
      PRIMITIVE_WRAPPERS.put(Double.class, double.class);      
    }
    
    private Class _type;
    private Object _value;
    
    public void setType(String type) throws Exception{
      if(PRIMITIVES.containsKey(type)){
        _type = (Class)PRIMITIVES.get(type);
      }
      else{
        _type = Class.forName(type);
      }
    }
    
    public Class getType(){
      Class type = doGetType();
      if(type == null){
        throw new IllegalStateException("Argument type not set; could not deduct type from value: " + _value.getClass());
      }
      return type;
    }
    
    private Class doGetType(){
      if(_type == null){
        if(_value != null){
          Class type = (Class)PRIMITIVE_WRAPPERS.get(_value.getClass());
          if(type == null){
            return _value.getClass();
          }
          else{
            return type;
          }
        }
      }
      return _type;
    }
    
    public void setValue(Object value){
      _value = value;
    }
    
    Object getValue(){
      return _value;
    }
    
    Object coerce(){
      if(_value != null){
        if(_type != null){
          if(_value instanceof String){
            return TypeConverters.convert((String)_value, _type);
          }
          return _value;
        }
      }
      return _value;
    }
    
    public void handleObject(String name, Object obj) throws ConfigurationException {
      setValue(obj);
      
    }
  }
}

