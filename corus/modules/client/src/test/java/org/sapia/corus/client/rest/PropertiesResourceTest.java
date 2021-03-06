package org.sapia.corus.client.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.sapia.corus.client.ClusterInfo;
import org.sapia.corus.client.Result;
import org.sapia.corus.client.Results;
import org.sapia.corus.client.common.rest.RestRequest;
import org.sapia.corus.client.common.rest.Value;
import org.sapia.corus.client.facade.ConfiguratorFacade;
import org.sapia.corus.client.facade.CorusConnectionContext;
import org.sapia.corus.client.facade.CorusConnector;
import org.sapia.corus.client.services.cluster.CorusHost;
import org.sapia.corus.client.services.cluster.CorusHost.RepoRole;
import org.sapia.corus.client.services.cluster.Endpoint;
import org.sapia.corus.client.services.configurator.Configurator.PropertyScope;
import org.sapia.corus.client.services.configurator.Property;
import org.sapia.ubik.net.ServerAddress;
import org.sapia.ubik.net.TCPAddress;

@RunWith(MockitoJUnitRunner.class)
public class PropertiesResourceTest {
  
  @Mock
  private CorusConnector           connector;
  
  @Mock
  private CorusConnectionContext   connection;
  
  @Mock
  private RestRequest              request;
  
  @Mock
  private ConfiguratorFacade       confs;
  
  private PropertiesResource       resource;
  private Results<List<Property>>  results;
  
  @Before
  public void setUp() throws Exception {
    resource = new PropertiesResource();
    
    results = new Results<List<Property>>();
    int count = 0;
    for (int i = 0; i < 5; i++) {
      CorusHost host = CorusHost.newInstance(
          new Endpoint(new TCPAddress("test", "host-" + i, i), mock(ServerAddress.class)), 
          "os-" + i, 
          "jvm-" + i
      );
      host.setHostName("hostname-" + i);
      host.setRepoRole(RepoRole.CLIENT);
      List<Property> props = new ArrayList<Property>();
      for (int j = 0; j < 5; j++) {
        Property p = new Property("prop-key-" + count, "prop-value-" + count, "cat-" + j);
        props.add(p);
        count++;
      }
      Result<List<Property>> result = new Result<List<Property>>(host, props, Result.Type.COLLECTION);
      results.addResult(result);
    }
    
    when(connection.getDomain()).thenReturn("test-cluster");
    when(connection.getVersion()).thenReturn("test-version");
    when(connector.getConfigFacade()).thenReturn(confs);
    when(confs.getAllProperties(any(PropertyScope.class), any(ClusterInfo.class)))
      .thenReturn(results);
    when(connector.getContext()).thenReturn(connection);
    when(request.getValue("corus:host")).thenReturn(new Value("corus:host", "localhost:33000"));
    when(request.getValue("corus:scope")).thenReturn(new Value("corus:scope", "process"));
    when(request.getValue("corus:category")).thenReturn(new Value("corus:category", null));
    when(request.getValue("p", "*")).thenReturn(new Value("p", "*"));
  }
  
  @Test
  public void testGetPropertiesForCluster() {
    String response = resource.getPropertiesForCluster(new RequestContext(request, connector));
    JSONArray json = JSONArray.fromObject(response);
    int count = 0;
    for (int i = 0; i < json.size(); i++) {
      JSONArray properties = json.getJSONObject(i).getJSONArray("data");
      for (int j = 0; j < properties.size(); j++) {
        doCheckProperty(properties.getJSONObject(j), count++);
      }
    }
  }
  
  @Test
  public void testGetPropertiesForCluster_category() {
    when(request.getValue("corus:category")).thenReturn(new Value("corus:category", "cat-0"));
    String response = resource.getPropertiesForCluster(new RequestContext(request, connector));
    JSONArray json = JSONArray.fromObject(response);
    for (int i = 0; i < json.size(); i++) {
      JSONArray properties = json.getJSONObject(i).getJSONArray("data");
      assertEquals(1, properties.size());
      assertEquals("cat-0", properties.getJSONObject(0).getString("category"));
    }
  }

  @Test
  public void testGetPropertiesForHost() {
    String response = resource.getPropertiesForHost(new RequestContext(request, connector));
    JSONArray json = JSONArray.fromObject(response);
    int count = 0;
    for (int i = 0; i < json.size(); i++) {
      JSONArray properties = json.getJSONObject(i).getJSONArray("data");
      for (int j = 0; j < properties.size(); j++) {
        doCheckProperty(properties.getJSONObject(j), count++);
      }
    }
  }
  
  @Test
  public void testGetPropertiesForHost_category() {
    when(request.getValue("corus:category")).thenReturn(new Value("corus:category", "cat-0"));
    String response = resource.getPropertiesForHost(new RequestContext(request, connector));
    JSONArray json = JSONArray.fromObject(response);
    for (int i = 0; i < json.size(); i++) {
      JSONArray properties = json.getJSONObject(i).getJSONArray("data");
      assertEquals(1, properties.size());
      assertEquals("cat-0", properties.getJSONObject(0).getString("category"));
    }
  }
  
  private void doCheckProperty(JSONObject property, int i) {
    assertEquals("prop-key-" + i, property.getString("name"));
    assertEquals("prop-value-" + i, property.getString("value"));
    assertNotNull(property.getString("category"));
  }  

}
