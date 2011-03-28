package org.sapia.soto.state.web.simple;

import org.sapia.soto.state.Scope;

import simple.http.Response;

/** 
 * Implements a scope over a Simple <code>Response</code>.
 * @author Yanick Duchesne
 */
public class ResponseScope implements Scope{
  
  private Response _response;

  ResponseScope(Response res){
    _response = res;
  }
  
  public Response getResponse(){
    return _response;
  }
  
  public Object getVal(Object key) {
    return _response.getValue(key.toString());
  }
  
  public void putVal(Object key, Object value) {
    if(key != null && value != null){
      _response.add(key.toString(), value.toString());
    }
  }
}
