package org.sapia.soto.state.web.simple;

import org.sapia.soto.state.Scope;

import simple.http.Request;
import simple.http.session.Session;

public class SessionScope implements Scope{
  
  private Request _req;
  
  SessionScope(Request req){
    _req = req;
  }
  
  public Object getVal(Object key) {
    if(key instanceof String){
      _req.getSession().get((String)key);
    }
    return null;
  }
  
  public void putVal(Object key, Object value) {
    if(key != null && value != null){
      _req.getSession().put(key.toString(), value);
    }
  }
  
  public Session getSession(){
    return _req.getSession();
  }

}
