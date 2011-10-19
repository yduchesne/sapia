package org.sapia.soto.state.web.simple;

import org.sapia.soto.state.Scope;

import simple.http.Request;

public class RequestScope implements Scope{

  private Request _request;
  
  RequestScope(Request req){
    _request = req;
  }
  
  public Request getRequest(){
    return _request;
  }
  
  public Object getVal(Object key) {
    return _request.getAttributes().getAttribute(key.toString());
  }
  
  public void putVal(Object key, Object value) {
    if(key != null && value != null){
      _request.getAttributes().setAttribute(key.toString(), value);
    }
  }
}
