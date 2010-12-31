package org.sapia.regis.loader;

import java.util.HashMap;
import java.util.Map;

import org.sapia.regis.util.VarContext;
import org.sapia.util.text.TemplateContextIF;
import org.sapia.util.xml.confix.CreationStatus;
import org.sapia.util.xml.confix.ObjectCreationException;
import org.sapia.util.xml.confix.ReflectionFactory;

public class ConfigObjectFactory extends ReflectionFactory{

  static final String REGISTRY = "registry";
  
  static final Map CLASSES = new HashMap();
  
  static{
    
    CLASSES.put("choose",   ChooseTag.class);
    CLASSES.put("if",       IfTag.class);      
    CLASSES.put("include",  IncludeTag.class);
    CLASSES.put("link",     LinkTag.class);
    CLASSES.put("node",     NodeTag.class);    
    CLASSES.put("paramRef", ParamRefTag.class);
    CLASSES.put("property", PropertyTag.class);
    CLASSES.put("registry", RegistryTag.class);      
    CLASSES.put("unless",   UnlessTag.class); 
    CLASSES.put("staticInclude",  StaticIncludeTag.class);    
  }
  
  //private TemplateContextIF _ctx;
  
  public ConfigObjectFactory(/*TemplateContextIF ctx*/){
    super(new String[0]);
    //_ctx = ctx;
  }
  
  public CreationStatus newObjectFor(String prefix, String uri, String name, Object parent) throws ObjectCreationException {
    Class clazz = (Class)CLASSES.get(name);
    if(clazz == null){
      return super.newObjectFor(prefix, uri, name, parent);        
    }
    else{
      try{
        Object instance = clazz.newInstance();
        if(instance instanceof TagFactory){
          instance = ((TagFactory)instance).create(VarContext.currentVars(), this);
        }
        if(instance instanceof TagNameAware){
          ((TagNameAware)instance).setTagName(name);
        }
        return CreationStatus.create(instance);          
      }catch(Exception e){
        throw new ObjectCreationException("Could not create object for: " + name, e);
      }
    }
  }  
}
