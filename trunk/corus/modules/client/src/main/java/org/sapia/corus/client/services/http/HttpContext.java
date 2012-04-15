package org.sapia.corus.client.services.http;

import simple.http.Request;
import simple.http.Response;

/**
 * Encapsulates HTTP request/response data.
 * 
 * @author yduchesne
 *
 */
public class HttpContext {

  private Request  request;
  private Response response;
  private String 	 contextPath, pathInfo;
  
  /**
   * @return this instance's <code>Request</code> object.
   */
  public Request getRequest() {
    return request;
  }
  
  public void setRequest(Request request) {
    this.request = request;
  }

  /**
   * @return this instance's <code>Response</code> object.
   */  
  public Response getResponse() {
    return response;
  }
  
  public void setResponse(Response response) {
    this.response = response;
  }

  /**
   * @return the context path of the request to which this
   * instance corresponds.
   */  
  public String getContextPath() {
    return contextPath;
  }

  public void setContextPath(String contextPath) {
    this.contextPath = contextPath;
  }

  /**
   * @return the path info of the request to which this
   * instance corresponds.
   */    
  public String getPathInfo() {
    return pathInfo;
  }

  public void setPathInfo(String pathInfo) {
    this.pathInfo = pathInfo;
  }
}