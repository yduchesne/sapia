package org.sapia.corus.client.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.sapia.corus.client.common.rest.RestRequest;
import org.sapia.corus.client.common.rest.Value;
import org.sapia.corus.client.facade.CorusConnector;

/**
 * Holds data corresponding to in incoming REST request.
 * 
 * @author yduchesne
 *
 */
public class RequestContext {

  private RestRequest    request;
  private CorusConnector connector;
  public RequestContext(RestRequest request, CorusConnector connector) {
    this.request = request;
    this.connector = connector;
  }
  
  /**
   * @return this instance's {@link RestRequest}.
   */
  public RestRequest getRequest() {
    return this.request;
  }
  
  /**
   * @param params the REST request's additional parameters (typically parsed out of the rest resource path).
   */
  public void addParams(Map<String, String> params) {
    ParamRequest paramReq;
    if (request instanceof ParamRequest) {
      paramReq = (ParamRequest) request;
    } else {
      paramReq = new ParamRequest(request);
      request = paramReq;
    }
    paramReq.params.putAll(params);
  }
  
  /**
   * @return this instance's {@link CorusConnector}.
   */
  public CorusConnector getConnector() {
    return connector;
  }
  
  // ==========================================================================
  // Inner class
  
  static class ParamRequest implements RestRequest {
    
    private RestRequest         delegate;
    private Map<String, String> params = new HashMap<String, String>();
    
    private ParamRequest(RestRequest delegate) {
      this.delegate = delegate;
    }
    
    @Override
    public Set<String> getAccepts() {
      return delegate.getAccepts();
    }
    
    @Override
    public String getContentType() {
      return delegate.getContentType();
    }
    
    @Override
    public String getMethod() {
      return delegate.getMethod();
    }
    
    @Override
    public String getPath() {
      return delegate.getPath();
    }
    
    @Override
    public Value getValue(String name) {
      String value = params.get(name);
      if (value != null) {
        return new Value(name, value);
      } else {
        return delegate.getValue(name);
      }
    }
    
    @Override
    public Value getValue(String name, String defaultVal) {
      return delegate.getValue(name, defaultVal);
    }
    
  }
}
