package org.sapia.soto.state.web.simple;

import java.io.IOException;

import org.sapia.soto.state.Scope;

import simple.http.Request;

public class ParamScope implements Scope{

  private Request _req;
  
  ParamScope(Request req){
    _req = req;
  }
  
  public Object getVal(Object key) {
    if(key instanceof String){
      try{
        return (String)_req.getParameter((String)key);
      }catch(IOException e){
        throw new RuntimeException("Could not acquire parameter: " + key, e);
      }
    }
    return null;
  }
  
  public void putVal(Object key, Object value) {}
}
