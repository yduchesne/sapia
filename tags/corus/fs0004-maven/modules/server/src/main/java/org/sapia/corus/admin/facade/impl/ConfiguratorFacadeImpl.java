package org.sapia.corus.admin.facade.impl;

import java.util.List;
import java.util.Set;

import org.sapia.corus.admin.Results;
import org.sapia.corus.admin.common.ArgFactory;
import org.sapia.corus.admin.common.NameValuePair;
import org.sapia.corus.admin.facade.ConfiguratorFacade;
import org.sapia.corus.admin.facade.CorusConnectionContext;
import org.sapia.corus.admin.services.configurator.Configurator;
import org.sapia.corus.admin.services.configurator.Configurator.PropertyScope;
import org.sapia.corus.core.ClusterInfo;

public class ConfiguratorFacadeImpl 
  extends FacadeHelper<Configurator> implements ConfiguratorFacade{
  
  public ConfiguratorFacadeImpl(CorusConnectionContext context) {
    super(context, Configurator.class);
  }
  
  @Override
  public Results<List<NameValuePair>> getProperties(PropertyScope scope,
      ClusterInfo cluster) {
    Results<List<NameValuePair>>  results = new Results<List<NameValuePair>>();
    proxy.getProperties(scope);
    invoker.invokeLenient(results, cluster);
    return results;   
  }
  
  @Override
  public Results<List<String>> getTags(ClusterInfo cluster) {
    Results<List<String>>  results = new Results<List<String>>();
    proxy.getTags();
    invoker.invokeLenient(results, cluster);
    return results;   
  }
  
  @Override
  public void addProperty(PropertyScope scope, String name, String value,
      ClusterInfo cluster) {
    proxy.addProperty(scope, name, value);
    invoker.invokeLenient(void.class, cluster); 
  }
  
  @Override
  public void addTag(String tag, ClusterInfo cluster) {
    proxy.addTag(tag);
    invoker.invokeLenient(void.class, cluster); 
  }
  
  @Override
  public void addTags(Set<String> tags, ClusterInfo cluster) {
    proxy.addTags(tags);
    invoker.invokeLenient(void.class, cluster); 
  }
  
  @Override
  public void removeProperty(PropertyScope scope, String name, ClusterInfo cluster) {
    proxy.removeProperty(scope, ArgFactory.parse(name));
    invoker.invokeLenient(void.class, cluster); 
  }
  
  @Override
  public void removeTag(String tag, ClusterInfo cluster) {
    proxy.removeTag(ArgFactory.parse(tag));
    invoker.invokeLenient(void.class, cluster); 
  }
}
