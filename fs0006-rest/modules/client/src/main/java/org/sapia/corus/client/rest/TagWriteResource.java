package org.sapia.corus.client.rest;

import org.sapia.corus.client.ClusterInfo;

/**
 * Handles addition/deletion of tags.
 * 
 * @author yduchesne
 *
 */
public class TagWriteResource {
  
  // --------------------------------------------------------------------------
  //  add

  @Path({
    "/clusters/tags", 
    "/clusters/{corus:cluster}/tags",
    "/clusters/hosts/tags", 
    "/clusters/{corus:cluster}/hosts/tags"
  })
  @HttpMethod(HttpMethod.PUT)
  @Output(ContentTypes.APPLICATION_JSON)
  @Accepts({ContentTypes.APPLICATION_JSON, ContentTypes.ANY})
  public void addTagsForCluster(RequestContext context) {
    doAddTags(context, ClusterInfo.clustered());
  }  
  
  @Path({
    "/clusters/hosts/{corus:host}/tags", 
    "/clusters/{corus:cluster}/hosts/{corus:host}/tags"
  })
  @HttpMethod(HttpMethod.PUT)
  @Output(ContentTypes.APPLICATION_JSON)
  @Accepts({ContentTypes.APPLICATION_JSON, ContentTypes.ANY})
  public void addTagsForHost(RequestContext context) {
    ClusterInfo cluster = ClusterInfo.fromLiteralForm(context.getRequest().getValue("corus:host").asString());
    doAddTags(context, cluster);
  }
  
  // --------------------------------------------------------------------------
  // delete

  @Path({
    "/clusters/tags/{corus:tag}", 
    "/clusters/{corus:cluster}/tags/{corus:tag}",
    "/clusters/hosts/tags/{corus:tag}", 
    "/clusters/{corus:cluster}/hosts/tags/{corus:tag}"
  })
  @HttpMethod(HttpMethod.DELETE)
  @Output(ContentTypes.APPLICATION_JSON)
  @Accepts({ContentTypes.APPLICATION_JSON, ContentTypes.ANY})
  public void deleteTagForCluster(RequestContext context) {
    doDeleteTag(context, ClusterInfo.clustered());
  }  
  
  @Path({
    "/clusters/hosts/{corus:host}/tags/{corus:tag}", 
    "/clusters/{corus:cluster}/hosts/{corus:host}/tags/{corus:tag}"
  })
  @HttpMethod(HttpMethod.DELETE)
  @Output(ContentTypes.APPLICATION_JSON)
  @Accepts({ContentTypes.APPLICATION_JSON, ContentTypes.ANY})
  public void deleteTagForHost(RequestContext context) {
    ClusterInfo cluster = ClusterInfo.fromLiteralForm(context.getRequest().getValue("corus:host").asString());
    doDeleteTag(context, cluster);
  }
  
  // --------------------------------------------------------------------------
  // Restricted methods
  
  private void doAddTags(RequestContext context, ClusterInfo cluster) {
    context.getConnector().getConfigFacade().addTags(
        context.getRequest().getValue("values").asSet(), 
        cluster
    );
  }
  
  private void doDeleteTag(RequestContext context, ClusterInfo cluster) {
    context.getConnector().getConfigFacade().removeTag(
        context.getRequest().getValue("corus:tag").asString(), 
        cluster
    );
  }
}
