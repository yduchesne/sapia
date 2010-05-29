package org.sapia.corus.configurator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.sapia.corus.admin.Arg;
import org.sapia.corus.admin.services.configurator.Configurator;
import org.sapia.corus.annotations.Bind;
import org.sapia.corus.core.InitContext;
import org.sapia.corus.core.ModuleHelper;
import org.sapia.corus.core.PropertyContainer;
import org.sapia.corus.db.DbMap;
import org.sapia.corus.db.DbModule;
import org.sapia.corus.property.PropertyProvider;
import org.sapia.corus.util.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;

@Bind(moduleInterface=Configurator.class)
public class ConfiguratorImpl extends ModuleHelper implements Configurator{
  
  public static final String PROP_SERVER_NAME = "corus.server.name";

  @Autowired
  private PropertyProvider propertyProvider;
  
  @Autowired
  private DbModule db;
  
  private PropertyStore processProperties, serverProperties, internalProperties;
  private DbMap<String, ConfigProperty> tags;
  
  public String getRoleName() {
    return Configurator.ROLE;
  }
  
  @Override
  public void init() throws Exception {
    processProperties   = new PropertyStore(db.getDbMap(String.class, ConfigProperty.class, "configurator.properties.process"));
    serverProperties    = new PropertyStore(db.getDbMap(String.class, ConfigProperty.class, "configurator.properties.server"));
    internalProperties  = new PropertyStore(db.getDbMap(String.class, ConfigProperty.class, "configurator.properties.internal"));

    tags       = db.getDbMap(String.class, ConfigProperty.class, "configurator.tags");
    propertyProvider.overrideInitProperties(new ConfigPropertyContainer(InitContext.get().getProperties()));

    String serverName = internalProperties.getProperty(PROP_SERVER_NAME);
    if(serverName == null){
      internalProperties.addProperty(PROP_SERVER_NAME, serverContext().getServerName());
    }
    else{
      serverContext().setServerName(serverName);
    }
  }
  
  public void dispose() {}

  public void addProperty(PropertyScope scope, String name, String value) {
    store(scope).addProperty(name, value);
  }
  
  public String getProperty(String name) {
    String value = serverProperties.getProperty(name);
    if(value == null){
      value = processProperties.getProperty(name);
    }
    return value;
  }
  
  public void removeProperty(PropertyScope scope, Arg name) {
    store(scope).removeProperty(name);
  }
  
  public Properties getProperties(PropertyScope scope) {
    return store(scope).getProperties();
  }
  
  public List<NameValuePair> getPropertiesAsNameValuePairs(PropertyScope scope) {
    Properties props = store(scope).getProperties();
    List<NameValuePair> toReturn = new ArrayList<NameValuePair>(props.size());
    Enumeration<?> keys = props.propertyNames();
    while(keys.hasMoreElements()){
      String key = (String)keys.nextElement();
      NameValuePair pair = new NameValuePair(key, props.getProperty(key));
      toReturn.add(pair);
    }
    Collections.sort(toReturn);
    return toReturn;
  }
  
  public void addTag(String tag) {
    tags.put(tag, new ConfigProperty(tag, tag));
  }
  
  public void clearTags() {
    tags.clear();
  }
  
  public Set<String> getTags() {
    Iterator<String> names = tags.keys();
    Set<String> tags = new TreeSet<String>();
    while(names.hasNext()){
      String name = names.next();
      tags.add(name);
    }
    return tags;
  }
  
  public void removeTag(String tag) {
    tags.remove(tag);
  }
  
  public void removeTag(Arg tag) {
    for(String t:getTags()){
      if(tag.matches(t)){
        removeTag(t);
      }
    }
  }
  
  public void addTags(Set<String> tags) {
    for(String t:tags){
      if(t != null){
        addTag(t);
      }
    }
  }
  
  private PropertyStore store(PropertyScope scope){
    if(scope == PropertyScope.SERVER){
      return serverProperties;
    }
    else{
      return processProperties;
    }
  }
  
  class ConfigPropertyContainer implements PropertyContainer{
    private PropertyContainer nested;
    public ConfigPropertyContainer(PropertyContainer nested) {
      this.nested = nested;
    }
    
    public String getProperty(String name) {
      String value = store(PropertyScope.SERVER).getProperty(name);
      if(value == null){
        value = nested.getProperty(name);
      }
      return value;
    }
  }
}
