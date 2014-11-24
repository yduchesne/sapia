package org.sapia.corus.client.rest;

import org.sapia.corus.client.ClusterInfo;
import org.sapia.corus.client.exceptions.port.PortActiveException;
import org.sapia.corus.client.exceptions.port.PortRangeConflictException;
import org.sapia.corus.client.exceptions.port.PortRangeInvalidException;

/**
 * Handles addition/deletion of port ranges.
 * 
 * @author yduchesne
 *
 */
public class PortWriteResource {

  // --------------------------------------------------------------------------
  //  add
  
  @Path({
    "/clusters/ports/ranges/{corus:rangeName}", 
    "/clusters/{corus:cluster}/ports/ranges/{corus:rangeName}",
    "/clusters/hosts/ports/ranges/{corus:rangeName}", 
    "/clusters/{corus:cluster}/hosts/ports/ranges/{corus:rangeName}"
  })
  @HttpMethod(HttpMethod.PUT)
  @Output(ContentTypes.APPLICATION_JSON)
  @Accepts({ContentTypes.APPLICATION_JSON, ContentTypes.ANY})
  public void addPortRangeForCluster(RequestContext context)
      throws PortRangeInvalidException, PortRangeConflictException {
    doAddPortRange(context, ClusterInfo.clustered());
  }
  
  @Path({
    "/clusters/hosts/{corus:host}/ports/ranges/{corus:rangeName}", 
    "/clusters/{corus:cluster}/hosts/{corus:host}/ports/ranges/{corus:rangeName}"
  })
  @HttpMethod(HttpMethod.PUT)
  @Output(ContentTypes.APPLICATION_JSON)
  @Accepts({ContentTypes.APPLICATION_JSON, ContentTypes.ANY})
  public void addPortRangeForHost(RequestContext context)
      throws PortRangeInvalidException, PortRangeConflictException {
    ClusterInfo cluster = ClusterInfo.fromLiteralForm(context.getRequest().getValue("corus:host").asString());
    doAddPortRange(context, cluster);
  }
  
  // --------------------------------------------------------------------------
  //  delete
  
  @Path({
    "/clusters/ports/ranges/{corus:rangeName}", 
    "/clusters/{corus:cluster}/ports/ranges/{corus:rangeName}",
    "/clusters/hosts/ports/ranges/{corus:rangeName}", 
    "/clusters/{corus:cluster}/hosts/ports/ranges/{corus:rangeName}"
  })
  @HttpMethod(HttpMethod.DELETE)
  @Output(ContentTypes.APPLICATION_JSON)
  @Accepts({ContentTypes.APPLICATION_JSON, ContentTypes.ANY})
  public void deletePortRangeForCluster(RequestContext context)
      throws PortActiveException {
    doDeletePortRange(context, ClusterInfo.clustered());
  }
  
  @Path({
    "/clusters/hosts/{corus:host}/ports/ranges/{corus:rangeName}", 
    "/clusters/{corus:cluster}/hosts/{corus:host}/ports/ranges/{corus:rangeName}"
  })
  @HttpMethod(HttpMethod.DELETE)
  @Output(ContentTypes.APPLICATION_JSON)
  @Accepts({ContentTypes.APPLICATION_JSON, ContentTypes.ANY})
  public void deletePortRangeForHost(RequestContext context)
      throws PortActiveException {
    ClusterInfo cluster = ClusterInfo.fromLiteralForm(context.getRequest().getValue("corus:host").asString());
    doDeletePortRange(context, cluster);
  }
  
  // --------------------------------------------------------------------------
  // release 
  
  @Path({
    "/clusters/ports/ranges/{corus:rangeName}", 
    "/clusters/{corus:cluster}/ports/ranges/{corus:rangeName}",
    "/clusters/hosts/ports/ranges/{corus:rangeName}", 
    "/clusters/{corus:cluster}/hosts/ports/ranges/{corus:rangeName}"
  })
  @HttpMethod(HttpMethod.POST)
  @Output(ContentTypes.APPLICATION_JSON)
  @Accepts({ContentTypes.APPLICATION_JSON, ContentTypes.ANY})
  public void releasePortRangeForCluster(RequestContext context) {
    doReleasePortRange(context, ClusterInfo.clustered());
  }
  
  @Path({
    "/clusters/hosts/{corus:host}/ports/ranges/{corus:rangeName}", 
    "/clusters/{corus:cluster}/hosts/{corus:host}/ports/ranges/{corus:rangeName}"
  })
  @HttpMethod(HttpMethod.POST)
  @Output(ContentTypes.APPLICATION_JSON)
  @Accepts({ContentTypes.APPLICATION_JSON, ContentTypes.ANY})
  public void releasePortRangeForHost(RequestContext context) {
    ClusterInfo cluster = ClusterInfo.fromLiteralForm(context.getRequest().getValue("corus:host").asString());
    doReleasePortRange(context, cluster);
  }
  
  // --------------------------------------------------------------------------
  // Restricted methods
  
  private void doAddPortRange(RequestContext context, ClusterInfo cluster) 
      throws PortRangeInvalidException, PortRangeConflictException {
    String name = context.getRequest().getValue("corus:rangeName").asString();
    int    min  = context.getRequest().getValue("min").asInt();
    int    max  = context.getRequest().getValue("max").asInt();
    context.getConnector().getPortManagementFacade().addPortRange(name, min, max, cluster);
  }
  
  private void doDeletePortRange(RequestContext context, ClusterInfo cluster) 
      throws PortActiveException {
    String name   = context.getRequest().getValue("corus:rangeName").asString();
    boolean force = context.getRequest().getValue("force", "false").asBoolean();
    context.getConnector().getPortManagementFacade().removePortRange(name, force, cluster);
  }
  
  private void doReleasePortRange(RequestContext context, ClusterInfo cluster) {
    String name   = context.getRequest().getValue("corus:rangeName").asString();
    context.getConnector().getPortManagementFacade().releasePortRange(name, cluster);
  }
}