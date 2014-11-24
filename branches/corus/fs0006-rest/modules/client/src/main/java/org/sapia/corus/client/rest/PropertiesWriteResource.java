package org.sapia.corus.client.rest;

import java.util.Properties;

import org.sapia.corus.client.ClusterInfo;
import org.sapia.corus.client.common.rest.Value;
import org.sapia.corus.client.services.configurator.Configurator.PropertyScope;

/**
 * Handles the addition/deletion of properties.
 * 
 * @author yduchesne
 *
 */
public class PropertiesWriteResource {

  private static final String SCOPE_SERVER  = "server";
  private static final String SCOPE_PROCESS = "process";
  
  @Path({
    "/clusters/properties/{corus:scope}", 
    "/clusters/{corus:cluster}/properties/{corus:scope}",
    "/clusters/hosts/properties/{corus:scope}", 
    "/clusters/{corus:cluster}/hosts/properties/{corus:scope}"
  })
  @HttpMethod(HttpMethod.PUT)
  @Output(ContentTypes.APPLICATION_JSON)
  @Accepts({ContentTypes.APPLICATION_JSON, ContentTypes.ANY})
  public void addPropertiesForCluster(RequestContext context) {
    doAddProperties(context, ClusterInfo.clustered());
  }
  
  // --------------------------------------------------------------------------
  
  @Path({
    "/clusters/hosts/{corus:host}/properties/{corus:scope}", 
    "/clusters/{corus:cluster}/hosts/{corus:host}/properties/{corus:scope}"
  })
  @HttpMethod(HttpMethod.PUT)
  @Output(ContentTypes.APPLICATION_JSON)
  @Accepts({ContentTypes.APPLICATION_JSON, ContentTypes.ANY})
  public void addPropertiesForHost(RequestContext context) {
    ClusterInfo cluster = ClusterInfo.fromLiteralForm(context.getRequest().getValue("corus:host").asString());
    doAddProperties(context, cluster);
  }
  
  // --------------------------------------------------------------------------

  @Path({
    "/clusters/properties/{corus:scope}/{corus:propertyName}", 
    "/clusters/{corus:cluster}/properties/{corus:scope}/{corus:propertyName}",
    "/clusters/hosts/properties/{corus:scope}/{corus:propertyName}", 
    "/clusters/{corus:cluster}/hosts/properties/{corus:scope}/{corus:propertyName}"
  })
  @HttpMethod(HttpMethod.DELETE)
  @Output(ContentTypes.APPLICATION_JSON)
  @Accepts({ContentTypes.APPLICATION_JSON, ContentTypes.ANY})
  public void deletePropertyForCluster(RequestContext context) {
    doDeleteProperty(context, ClusterInfo.clustered());
  }
  
  // --------------------------------------------------------------------------
  
  @Path({
    "/clusters/hosts/{corus:host}/properties/{corus:scope}/{corus:propertyName}", 
    "/clusters/{corus:cluster}/hosts/{corus:host}/properties/{corus:scope}/{corus:propertyName}"
  })
  @HttpMethod(HttpMethod.DELETE)
  @Output(ContentTypes.APPLICATION_JSON)
  @Accepts({ContentTypes.APPLICATION_JSON, ContentTypes.ANY})
  public void deletePropertyForHost(RequestContext context) {
    ClusterInfo cluster = ClusterInfo.fromLiteralForm(context.getRequest().getValue("corus:host").asString());
    doDeleteProperty(context, cluster);
  }
  
  // --------------------------------------------------------------------------
  // Restricted
  
  private void doAddProperties(RequestContext context, ClusterInfo cluster) {
    Properties props = new Properties();
    for (Value v : context.getRequest().getValues()) {
      props.setProperty(v.getName(), v.asString());
    }    
    context.getConnector().getConfigFacade().addProperties(getScope(context), props, false, cluster);
  }
  
  private void doDeleteProperty(RequestContext context, ClusterInfo cluster) {
    context.getConnector().getConfigFacade().removeProperty(
        getScope(context), 
        context.getRequest().getValue("corus:propertyName").asString(), 
        cluster
    );
  }
  
  private PropertyScope getScope(RequestContext context) {
    String scopeValue = context.getRequest().getValue("corus:scope").asString();
    PropertyScope scope;
    if (scopeValue.equals(SCOPE_PROCESS)) {
      scope = PropertyScope.PROCESS;
    } else if (scopeValue.equals(SCOPE_SERVER)) {
      scope = PropertyScope.SERVER;
    } else {
      throw new IllegalArgumentException(String.format("Invalid scope %s. Use one of the supported scopes: [process, server]", scopeValue));
    }
    return scope;
  }
}
