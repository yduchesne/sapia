package org.sapia.corus.client.rest;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.sapia.corus.client.ClusterInfo;
import org.sapia.corus.client.Result;
import org.sapia.corus.client.Results;
import org.sapia.corus.client.common.json.WriterJsonStream;
import org.sapia.corus.client.services.configurator.Tag;
import org.sapia.ubik.util.Collects;
import org.sapia.ubik.util.Func;

/**
 * A REST resources that gives access to tags.
 * 
 * @author yduchesne
 *
 */
public class TagResource {
  
  @Path({"/clusters/tags", "/clusters/{corus:cluster}/tags"})
  @HttpMethod(HttpMethod.GET)
  @Output(ContentTypes.APPLICATION_JSON)
  @Accepts({ContentTypes.APPLICATION_JSON, ContentTypes.ANY})
  public String getPropertiesForAllClusters(RequestContext context) {
    return doGetTags(context, ClusterInfo.clustered());
  }  
  
  // --------------------------------------------------------------------------
  
  @Path({"/clusters/hosts/tags", "/clusters/{corus:cluster}/hosts/tags"})
  @HttpMethod(HttpMethod.GET)
  @Output(ContentTypes.APPLICATION_JSON)
  @Accepts({ContentTypes.APPLICATION_JSON, ContentTypes.ANY})
  public String getDistributionsForAllClusters2(RequestContext context) {
    return getPropertiesForAllClusters(context);
  }  
  
  // --------------------------------------------------------------------------
  
  @Path({"/clusters/hosts/{corus:host}/tags", "/clusters/{corus:cluster}/hosts/{corus:host}/tags"})
  @HttpMethod(HttpMethod.GET)
  @Output(ContentTypes.APPLICATION_JSON)
  @Accepts({ContentTypes.APPLICATION_JSON, ContentTypes.ANY})
  public String getDistributionsForClusterAndHost(RequestContext context) {
    ClusterInfo cluster = ClusterInfo.fromLiteralForm(context.getRequest().getValue("corus:host").asString());
    return doGetTags(context, cluster);
  }
    
  // --------------------------------------------------------------------------
  // Restricted methods
  
  private String doGetTags(RequestContext context, ClusterInfo cluster) {
    Results<Set<Tag>> results = context.getConnector()
        .getConfigFacade().getTags(cluster);
    
    return doProcessResults(context, results);
  }
  
  private String doProcessResults(RequestContext context, Results<Set<Tag>> results) {
    StringWriter output = new StringWriter();
    WriterJsonStream stream = new WriterJsonStream(output);
    stream.beginArray();
    while (results.hasNext()) {
      Result<Set<Tag>> result = results.next();
      stream.beginObject()
        .field("cluster").value(context.getConnector().getContext().getDomain())
        .field("host").value(
            result.getOrigin().getEndpoint().getServerTcpAddress().getHost() + ":" +
            result.getOrigin().getEndpoint().getServerTcpAddress().getPort()
        )
        .field("data");

      List<Tag> sortedTags = new ArrayList<>(result.getData());
      Collections.sort(sortedTags);
      
      stream.strings(Collects.convertAsArray(sortedTags, String.class, new Func<String, Tag>() {
        public String call(Tag arg) {
          return arg.getValue();
        }
      }));
      stream.endObject();
    }
    stream.endArray();
    return output.toString();    
  }

} 
