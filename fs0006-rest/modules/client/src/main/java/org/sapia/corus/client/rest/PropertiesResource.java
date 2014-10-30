package org.sapia.corus.client.rest;

import java.io.StringWriter;
import java.util.List;

import org.sapia.corus.client.ClusterInfo;
import org.sapia.corus.client.Result;
import org.sapia.corus.client.Results;
import org.sapia.corus.client.common.NameValuePair;
import org.sapia.corus.client.common.json.WriterJsonStream;
import org.sapia.corus.client.services.configurator.Configurator.PropertyScope;

/**
 * A REST resources that gives access to process and server properties.
 * 
 * @author yduchesne
 *
 */
public class PropertiesResource {
  
  private static final String PASSWORD_SUBSTR = "password";
  private static final String OBFUSCATION     = "********";

  @Path({"/clusters/properties", "/clusters/{corus:cluster}/properties"})
  @HttpMethod(HttpMethod.GET)
  @Output(ContentTypes.APPLICATION_JSON)
  @Accepts({ContentTypes.APPLICATION_JSON, ContentTypes.ANY})
  public String getPropertiesForAllClusters(RequestContext context) {
    return doGetProperties(context, ClusterInfo.clustered());
  }
  
  // --------------------------------------------------------------------------
  
  @Path({"/clusters/hosts/properties", "/clusters/{corus:cluster}/hosts/properties"})
  @HttpMethod(HttpMethod.GET)
  @Output(ContentTypes.APPLICATION_JSON)
  @Accepts({ContentTypes.APPLICATION_JSON, ContentTypes.ANY})
  public String getPropertiesForAllClusters2(RequestContext context) {
    return getPropertiesForAllClusters(context);
  }  
  
  // --------------------------------------------------------------------------
  
  @Path({"/clusters/hosts/{corus:host}/properties", "/clusters/{corus:cluster}/hosts/{corus:host}/properties"})
  @HttpMethod(HttpMethod.GET)
  @Output(ContentTypes.APPLICATION_JSON)
  @Accepts({ContentTypes.APPLICATION_JSON, ContentTypes.ANY})
  public String getPropertiesForClusterAndHost(RequestContext context) {
    ClusterInfo cluster = ClusterInfo.fromLiteralForm(context.getRequest().getValue("corus:host").asString());
    return doGetProperties(context, cluster);
  }
    
  // --------------------------------------------------------------------------
  // Restricted methods
  
  private String doGetProperties(RequestContext context, ClusterInfo cluster) {
    PropertyScope scope = PropertyScope.forName(context.getRequest().getValue("s", "p").asString());
    Results<List<NameValuePair>> results = context.getConnector()
        .getConfigFacade().getProperties(scope, cluster);
    
    return doProcessResults(context, results);
  }
  
  private String doProcessResults(RequestContext context, Results<List<NameValuePair>> results) {
    StringWriter output = new StringWriter();
    WriterJsonStream stream = new WriterJsonStream(output);
    stream.beginArray();
    while (results.hasNext()) {
      Result<List<NameValuePair>> result = results.next();
      stream.beginObject()
        .field("cluster").value(context.getConnector().getContext().getDomain())
        .field("host").value(
            result.getOrigin().getEndpoint().getServerTcpAddress().getHost() + ":" +
            result.getOrigin().getEndpoint().getServerTcpAddress().getPort()
        )
        .field("data")
        .beginArray();

      for (NameValuePair np : result.getData()) {
        stream.beginObject()
          .field("name").value(np.getName())
          .field("value").value(obfuscate(np.getName(), np.getValue()))
        .endObject();
      }
      stream.endArray().endObject();
    }
    stream.endArray();
    return output.toString();    
  }
  
  private String obfuscate(String propertyName, String propertyValue) {
    if (propertyName.toLowerCase().contains(PASSWORD_SUBSTR)) {
      return OBFUSCATION;
    } else {
      return propertyValue;
    }
  }
} 
