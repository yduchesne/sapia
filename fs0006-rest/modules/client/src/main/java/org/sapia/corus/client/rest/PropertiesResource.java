package org.sapia.corus.client.rest;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.sapia.corus.client.ClusterInfo;
import org.sapia.corus.client.Result;
import org.sapia.corus.client.Results;
import org.sapia.corus.client.common.Arg;
import org.sapia.corus.client.common.ArgFactory;
import org.sapia.corus.client.common.NameValuePair;
import org.sapia.corus.client.common.json.WriterJsonStream;
import org.sapia.corus.client.services.configurator.Configurator.PropertyScope;
import org.sapia.ubik.util.Func;

/**
 * A REST resources that gives access to process and server properties.
 * 
 * @author yduchesne
 *
 */
public class PropertiesResource {
  
  private static final String SCOPE_SERVER  = "server";
  private static final String SCOPE_PROCESS = "process";
  
  private static final String PASSWORD_SUBSTR = "password";
  private static final String OBFUSCATION     = "********";

  @Path({
    "/clusters/properties/{corus:scope}", 
    "/clusters/{corus:cluster}/properties/{corus:scope}",
    "/clusters/hosts/properties/{corus:scope}", 
    "/clusters/{corus:cluster}/hosts/properties/{corus:scope}"
  })
  @HttpMethod(HttpMethod.GET)
  @Output(ContentTypes.APPLICATION_JSON)
  @Accepts({ContentTypes.APPLICATION_JSON, ContentTypes.ANY})
  public String getPropertiesForCluster(RequestContext context) {
    return doGetProperties(context, ClusterInfo.clustered());
  }
  
  // --------------------------------------------------------------------------
  
  @Path({
    "/clusters/hosts/{corus:host}/properties/{corus:scope}", 
    "/clusters/{corus:cluster}/hosts/{corus:host}/properties/{corus:scope}"
  })
  @HttpMethod(HttpMethod.GET)
  @Output(ContentTypes.APPLICATION_JSON)
  @Accepts({ContentTypes.APPLICATION_JSON, ContentTypes.ANY})
  public String getPropertiesForHost(RequestContext context) {
    ClusterInfo cluster = ClusterInfo.fromLiteralForm(context.getRequest().getValue("corus:host").asString());
    return doGetProperties(context, cluster);
  }
    
  // --------------------------------------------------------------------------
  // Restricted methods
  
  private String doGetProperties(RequestContext context, ClusterInfo cluster) {
    String scopeValue = context.getRequest().getValue("corus:scope").asString();
    PropertyScope scope;
    if (scopeValue.equals(SCOPE_PROCESS)) {
      scope = PropertyScope.PROCESS;
    } else if (scopeValue.equals(SCOPE_SERVER)) {
      scope = PropertyScope.SERVER;
    } else {
      throw new IllegalArgumentException(String.format("Invalid scope %s. Use one of the supported scopes: [process, server]", scopeValue));
    }
    
    final Arg filter = ArgFactory.parse(context.getRequest().getValue("p", "*").asString());
    
    Results<List<NameValuePair>> results = context.getConnector()
        .getConfigFacade().getProperties(scope, cluster);
    results = results.filter(new Func<List<NameValuePair>, List<NameValuePair>>() {
      @Override
      public List<NameValuePair> call(List<NameValuePair> toFilter) {
        List<NameValuePair> toReturn = new ArrayList<>();
        for (NameValuePair nvp: toFilter) {
          if (filter.matches(nvp.getName())) {
            toReturn.add(nvp);
          }
        }
        return toReturn;
      }
    });
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
