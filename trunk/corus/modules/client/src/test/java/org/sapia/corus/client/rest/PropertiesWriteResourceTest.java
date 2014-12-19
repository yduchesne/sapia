package org.sapia.corus.client.rest;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.sapia.corus.client.ClusterInfo;
import org.sapia.corus.client.common.Arg;
import org.sapia.corus.client.common.ArgFactory;
import org.sapia.corus.client.common.rest.RestRequest;
import org.sapia.corus.client.common.rest.Value;
import org.sapia.corus.client.facade.ConfiguratorFacade;
import org.sapia.corus.client.facade.CorusConnectionContext;
import org.sapia.corus.client.facade.CorusConnector;
import org.sapia.corus.client.services.configurator.Configurator.PropertyScope;
import org.sapia.ubik.util.Collects;

@RunWith(MockitoJUnitRunner.class)
public class PropertiesWriteResourceTest {
  
  @Mock
  private CorusConnector           connector;
  
  @Mock
  private CorusConnectionContext   connection;
  
  @Mock
  private RestRequest              request;
  
  @Mock
  private ConfiguratorFacade       confs;
  
  private RequestContext           context;
  
  private PropertiesWriteResource  resource;

  @Before
  public void setUp() throws Exception {
    resource = new PropertiesWriteResource();
    context  = new RequestContext(request, connector);

    when(connector.getConfigFacade()).thenReturn(confs);
    when(connector.getContext()).thenReturn(connection);
    when(request.getValue("corus:host")).thenReturn(new Value("corus:host", "localhost:33000"));
    when(request.getValue("corus:scope")).thenReturn(new Value("corus:scope", "process"));
    when(request.getValue("corus:propertyName")).thenReturn(new Value("corus:propertyName", "test"));
    when(request.getValue("corus:category")).thenReturn(new Value("corus:category", null));
    when(request.getValue("clearExisting", "false")).thenReturn(new Value("clearExisting", "false"));
    when(request.getValues()).thenReturn(Collects.arrayToList(new Value("a", "v1"), new Value("b", "v2")));
  }
  
  @Test
  public void testAddPropertiesForCluster() {
    resource.addPropertiesForCluster(context);
    verify(confs).addProperties(eq(PropertyScope.PROCESS), any(Properties.class), anySetOf(String.class),  eq(false), any(ClusterInfo.class));
  }
  
  @Test
  public void testAddPropertiesForCluster_clear_existing() {
    when(request.getValue("clearExisting", "false")).thenReturn(new Value("clearExisting", "true"));
    resource.addPropertiesForCluster(context);
    verify(confs).addProperties(eq(PropertyScope.PROCESS), any(Properties.class), anySetOf(String.class),  eq(true), any(ClusterInfo.class));
  }
  
  @Test
  public void testAddPropertiesForCluster_category() {
    when(request.getValue("corus:category")).thenReturn(new Value("corus:category", "test-cat"));
    resource.addPropertiesForCluster(context);
    verify(confs).addProperties(eq(PropertyScope.PROCESS), any(Properties.class), eq(Collects.arrayToSet("test-cat")),  eq(false), any(ClusterInfo.class));
  }

  @Test
  public void testAddPropertiesForHost() {
    resource.addPropertiesForHost(context);
    verify(confs).addProperties(eq(PropertyScope.PROCESS), any(Properties.class), anySetOf(String.class), eq(false), any(ClusterInfo.class));
  }
  
  @Test
  public void testAddPropertiesForHost_clear_existing() {
    when(request.getValue("clearExisting", "false")).thenReturn(new Value("clearExisting", "true"));
    resource.addPropertiesForHost(context);
    verify(confs).addProperties(eq(PropertyScope.PROCESS), any(Properties.class), anySetOf(String.class), eq(true), any(ClusterInfo.class));
  }
  
  @Test
  public void testAddPropertiesForHost_category() {
    when(request.getValue("corus:category")).thenReturn(new Value("corus:category", "test-cat"));
    resource.addPropertiesForHost(context);
    verify(confs).addProperties(eq(PropertyScope.PROCESS), any(Properties.class), eq(Collects.arrayToSet("test-cat")),  eq(false), any(ClusterInfo.class));
  }

  @Test
  public void testDeletePropertyForCluster() {
    resource.deletePropertyForCluster(context);
    verify(confs).removeProperty(eq(PropertyScope.PROCESS), eq(ArgFactory.parse("test")), anySetOf(Arg.class), any(ClusterInfo.class));
  }
  
  @Test
  public void testDeletePropertyForCluster_category() {
    when(request.getValue("corus:category")).thenReturn(new Value("corus:category", "test-cat"));
    resource.deletePropertyForCluster(context);
    verify(confs).removeProperty(eq(PropertyScope.PROCESS), eq(ArgFactory.parse("test")), eq(Collects.arrayToSet(ArgFactory.parse("test-cat"))), any(ClusterInfo.class));
  }

  @Test
  public void testDeletePropertyForHost() {
    resource.deletePropertyForHost(context);
    verify(confs).removeProperty(eq(PropertyScope.PROCESS), eq(ArgFactory.parse("test")), anySetOf(Arg.class), any(ClusterInfo.class));
  }

  @Test
  public void testDeletePropertyForHost_category() {
    when(request.getValue("corus:category")).thenReturn(new Value("corus:category", "test-cat"));
    resource.deletePropertyForHost(context);
    verify(confs).removeProperty(eq(PropertyScope.PROCESS), eq(ArgFactory.parse("test")), eq(Collects.arrayToSet(ArgFactory.parse("test-cat"))), any(ClusterInfo.class));
  }
}
