package org.sapia.corus.configurator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.sapia.corus.client.common.Arg;
import org.sapia.corus.client.common.NameValuePair;
import org.sapia.corus.client.services.configurator.Configurator;
import org.sapia.corus.client.services.configurator.InternalConfigurator;

public class TestConfigurator implements Configurator, InternalConfigurator{

  public String getRoleName() {
    return Configurator.ROLE;
  }

  @Override
  public void addProperty(PropertyScope scope, String name, String value) {
  }
  
  @Override
  public void addProperties(PropertyScope scope, Properties props,
      boolean clearExisting) {
  }

  @Override
  public void addTag(String tag) {
  }

  @Override
  public void addTags(Set<String> tags) {
  }

  @Override
  public void clearTags() {
  }

  @Override
  public Properties getProperties(PropertyScope scope) {
    return new Properties();
  }
  
  @Override
  public Properties getInternalProperties(PropertyScope scope) {
    return getProperties(scope);
  }

  @Override
  public List<NameValuePair> getPropertiesAsNameValuePairs(PropertyScope scope) {
    return new ArrayList<NameValuePair>();
  }
  
  @Override
  public List<NameValuePair> getInternalPropertiesAsNameValuePairs(
      PropertyScope scope) {
    return getPropertiesAsNameValuePairs(scope);
  }

  @Override
  public String getProperty(String name) {
    return null;
  }
  
  @Override
  public String getInternalProperty(String name) {
    return getProperty(name);
  }

  @Override
  public Set<String> getTags() {
    return new HashSet<String>();
  }

  @Override
  public void removeProperty(PropertyScope scope, Arg name) {
  }

  @Override
  public void removeTag(Arg tag) {
  }

  @Override
  public void removeTag(String tag) {
  }
}