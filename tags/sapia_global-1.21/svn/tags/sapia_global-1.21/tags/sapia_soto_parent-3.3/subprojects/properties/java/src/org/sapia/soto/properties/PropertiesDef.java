package org.sapia.soto.properties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.sapia.soto.Debug;
import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.config.NullObjectImpl;
import org.sapia.soto.util.PropertiesContext;
import org.sapia.soto.util.Utils;
import org.sapia.util.text.TemplateContextIF;
import org.sapia.util.text.TemplateException;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

public class PropertiesDef 
  implements ObjectCreationCallback, EnvAware{
  
  private String[] _depends;
  private String _name;
  private PropertyServiceImpl _parent;
  private Properties _props = new Properties(); 
  private List _ancestors = new ArrayList();
  private String _uri;
  private Env _env;
  private boolean _rendered;
  
  PropertiesDef(PropertyServiceImpl parent){
    _parent = parent;
  }

  public void setUri(String uri){
    _uri = uri;
  }
  
  public void setName(String name){
    _name = name;
  }
  
  public void setDepends(String depends){
    _depends = Utils.split(depends, ',', true);
  }
  
  public void setEnv(Env env) {
    _env = env;
  }
  
  public Object onCreate() throws ConfigurationException{
    if(_name == null){
      throw new ConfigurationException("name not set");
    }
    if(_uri != null){
      try{
        _props.load(_env.resolveStream(_uri));
        if(Debug.DEBUG){
          _props.list(System.out);
        }
      }catch(IOException e){
        throw new ConfigurationException("Could not load properties '" + _name + "' from " + _uri);
      }
    }
    _parent.addProperties(this);
    return new NullObjectImpl();
  }

  String getName(){
    return _name;
  }

  Properties render(Set visited, TemplateContextIF parent) throws ConfigurationException{
    if(!_rendered){      
      _rendered = true;
      if(_ancestors.size() == 0){
        try{
          PropertiesContext ctx = new PropertiesContext(parent, false);
          ctx.addProperties(_props);
          _props = Utils.replaceVars(ctx, _props);
        }catch(TemplateException e){
          ConfigurationException e2 = new ConfigurationException("Could not process properties '" + _name 
              + "' for uri: " + _uri, e);
          throw e2;
        }
        return _props;
      }
      else{
        if(visited.contains(_name)){
          throw new IllegalStateException("Circular reference detected while including properties '" + _name + "'");
        }       
        visited.add(_name);
        PropertiesContext ctx = new PropertiesContext(parent, false);
        for(int i = 0; i < _ancestors.size(); i++){
          PropertiesDef def = (PropertiesDef)_ancestors.get(i);
          ctx.addProperties(def.render(visited, parent));
        }
        try{
          _props = Utils.replaceVars(ctx, _props);
        }catch(TemplateException e){
          ConfigurationException e2 = new ConfigurationException("Could not process properties '" + _name 
              + "' for uri: " + _uri, e);
          throw e2;
        }
        return _props;
      } 
    }
    else{
      if(visited.contains(_name)){
        throw new IllegalStateException("Circular reference detected while including properties '" + _name + "'");
      }       
      return _props;
    }
    
  }
  
  void resolveInheritance(Map propsMap){
    Properties result = new Properties();
    for(int i = 0; i < _ancestors.size(); i++){
      PropertiesDef def = (PropertiesDef)_ancestors.get(i);
      copy(def.getProperties(), result);
    }
    copy(_props, result);
    _props = result;
  }
  
  void visit(Set visited, Map propsMap){
    if(!visited.contains(_name)){
      visited.add(_name);
      if(_depends != null){
        for(int i = 0; i < _depends.length; i++){
          PropertiesDef props = (PropertiesDef)propsMap.get(_depends[i]);
          if(props != null && !_ancestors.contains(props)){
            _ancestors.add(props);
            props.visit(visited, propsMap);
          }
        }
      }
    }
  }

  Properties getProperties(){
    return _props;
  }
  
  private void copy(Properties from, Properties to){
    Enumeration names = from.propertyNames();
    while(names.hasMoreElements()){
      String name = (String)names.nextElement();
      to.setProperty(name, from.getProperty(name));
    }
  }
  
}
