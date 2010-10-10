package org.sapia.soto.state.web.simple;

import org.sapia.soto.state.Scope;

import simple.http.Request;
import simple.http.Response;

/**
 * Implements a scope over HTTP request/response headers.
 * 
 * @author Yanick Duchesne
 *
 */
public class HeaderScope implements Scope{
  
  private Request _req;
  private Response _res;
  
  HeaderScope(Request req, Response res){
    _req = req;
    _res = res;
  }
  
  public Object getVal(Object key) {
    return _req.getValue(key.toString());
  }
  
  public void putVal(Object key, Object value) {
    if(key != null && value != null){
      _res.set(key.toString(), value.toString());
    }
  }

}
