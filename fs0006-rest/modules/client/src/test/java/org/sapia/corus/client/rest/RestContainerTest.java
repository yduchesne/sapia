package org.sapia.corus.client.rest;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.sapia.corus.client.common.rest.RestRequest;
import org.sapia.corus.client.facade.CorusConnector;
import org.sapia.ubik.util.Collects;

@RunWith(MockitoJUnitRunner.class)
public class RestContainerTest {
  
  @Mock
  private RestRequest request;
  
  @Mock
  private RestResponseFacade response;
  
  @Mock
  private CorusConnector connector;
  
  private RestContainer container;
  private TestResource resource;

  @Before
  public void setUp() {
    resource  = new TestResource();
    container = RestContainer.Builder.newInstance().resource(resource).build();
    when(request.getAccepts()).thenReturn(Collects.arrayToSet(ContentTypes.APPLICATION_JSON));
    when(request.getMethod()).thenReturn(HttpMethod.GET);
  }
  
  @Test
  public void testInvoke() throws Throwable {
    when(request.getPath()).thenReturn("/test/method1");
    container.invoke(new RequestContext(request, connector), response);
    assertTrue(resource.method1Invoked);
    
    when(request.getPath()).thenReturn("/test/method2");
    container.invoke(new RequestContext(request, connector), response);
    assertTrue(resource.method2Invoked);
  }

  
  public static class TestResource {
    
    private boolean method1Invoked, method2Invoked;
    
    @Path("/test/method1")
    @HttpMethod(HttpMethod.GET)
    @Output(ContentTypes.APPLICATION_JSON)
    public String invokeMethod1(RequestContext request) {
      method1Invoked = true;
      return "method1";
    }
    
    @Path("/test/method1")
    @HttpMethod(HttpMethod.POST)
    @Output(ContentTypes.APPLICATION_JSON)
    public String invokeMethod1_port(RequestContext request) {
      return "method1";
    }
    
    @Path("/test/method1")
    @HttpMethod(HttpMethod.POST)
    @Output(ContentTypes.TEXT_HTML)
    public String invokeMethod1_html(RequestContext request) {
      return "method1";
    }
    
    @Path("/test/method2")
    @HttpMethod(HttpMethod.GET)
    @Output(ContentTypes.APPLICATION_JSON)
    public String invokeMethod2(RequestContext request) {
      method2Invoked = true;
      return "method2";
    }
    
  }
}
