package org.sapia.soto.properties;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.Service;
import org.sapia.soto.config.SotoIncludeContext;
import org.sapia.soto.util.CompositeProperties;
import org.sapia.resource.ResourceHandler;

public class PropertyServiceImpl 
  implements PropertyService, Service, EnvAware{
  
  private Map _propsMap = new HashMap();
  
  public void setEnv(Env env){
    ResourceHandler handler = env.getResourceHandlerFor(PropertyResourceHandler.SCHEME);
    if(handler == null || !(handler instanceof PropertyResourceHandler)){
      env.getResourceHandlers().prepend(new PropertyResourceHandler(env));
    }
  }
  
  public Properties getProperties(String name){
    Properties props = (Properties)_propsMap.get(name);
    if(props == null){
      return new Properties();
    }
    return new Properties(props);
  }
  
  public InputStream getPropertiesStream(String name) throws IOException {
    Properties props = getProperties(name);
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    props.store(bos, "");
    return new ByteArrayInputStream(bos.toByteArray());
  }
  
  public Properties getProperties(String[] names){
    CompositeProperties toReturn = new CompositeProperties();
    for(int i = 0; i < names.length; i++){
      Properties props = (Properties)_propsMap.get(names[i]);
      if(props != null){
        toReturn.addProperties(props);
      }
    }
    return toReturn;
  }
  
  public InputStream getPropertiesStream(String[] names) throws IOException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();    
    for(int i = 0; i < names.length; i++){
      Properties props = (Properties)_propsMap.get(names[i]);
      if(props != null){
        props.store(bos, "");
      }
    }
    return new ByteArrayInputStream(bos.toByteArray());
  }
  
  public PropertiesDef createProperties(){
    PropertiesDef def = new PropertiesDef(this);
    return def;
  }
  
  public void init() throws Exception{
    //first pass: visiting to resolve dependencies
    Iterator defs = _propsMap.values().iterator();
    Set visited = new HashSet();    
    while(defs.hasNext()){
      PropertiesDef def = (PropertiesDef)defs.next();
      def.visit(visited, _propsMap);
      visited.clear();
    }
    
    //second pass: rendering
    defs = _propsMap.values().iterator();
    while(defs.hasNext()){
      PropertiesDef def = (PropertiesDef)defs.next();
      def.render(visited, SotoIncludeContext.currentTemplateContext());
      visited.clear();
    }
    
    //third pass: inheritance
    defs = _propsMap.values().iterator();
    while(defs.hasNext()){
      PropertiesDef def = (PropertiesDef)defs.next();
      def.resolveInheritance(_propsMap);
    }    
    
    //fourth pass: substitution
    defs = _propsMap.entrySet().iterator();
    Map propsMap = new HashMap();
    while(defs.hasNext()){
      Map.Entry entry = (Map.Entry)defs.next();
      PropertiesDef def = (PropertiesDef)entry.getValue();
      propsMap.put(entry.getKey(), def.getProperties());
    }
    
    _propsMap = propsMap;
  }
  
  public void start() throws Exception {
  }
  
  public void dispose() {   
  }
  
  void addProperties(PropertiesDef def){
    _propsMap.put(def.getName(), def);
  }
  

}
