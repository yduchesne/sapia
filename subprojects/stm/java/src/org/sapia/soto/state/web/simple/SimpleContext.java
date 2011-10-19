package org.sapia.soto.state.web.simple;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.sapia.soto.Env;
import org.sapia.soto.state.ContextImpl;
import org.sapia.soto.state.Input;
import org.sapia.soto.state.MapScope;
import org.sapia.soto.state.Output;
import org.sapia.soto.state.web.EnvScope;
import org.sapia.soto.state.web.WebConsts;
import org.sapia.soto.state.xml.XMLContext;
import org.xml.sax.ContentHandler;

import simple.http.Request;
import simple.http.Response;
import simple.http.session.Session;

public class SimpleContext extends ContextImpl implements XMLContext, 
    Input, Output, WebConsts {
  private ContentHandler     _handler;
  private OutputStream       _output;
  private Request            _req;
  private Response           _res;
  
  static final String PATH = "/";
  
  public SimpleContext(Env env, ContentHandler handler, 
     Request req, Response res) {
    _handler = handler;
    _req = req;
    _res = res;

    super.addScope(HEADERS_SCOPE, new HeaderScope(req, res));
    super.addScope(SESSION_SCOPE, new SessionScope(req));
    super.addScope(REQUEST_SCOPE, new RequestScope(req));
    super.addScope(RESPONSE_SCOPE, new ResponseScope(res));
    super.addScope(REQ_PARAM_SCOPE, new ParamScope(req));
    super.addScope(ENV_SCOPE, new EnvScope(env));

    MapScope view = new MapScope();
    view.put(CONTEXT_PATH_KEY, PATH);
    view.put(CONTEXT_PATH_KEY2, PATH);
    super.addScope(VIEW_SCOPE, view);
  }

  /**
   * @see org.sapia.soto.state.xml.XMLContext#getContentHandler()
   */
  public ContentHandler getContentHandler() {
    return _handler;
  }

  /**
   * @see org.sapia.soto.state.xml.XMLContext#setContentHandler(org.xml.sax.ContentHandler)
   */
  public void setContentHandler(ContentHandler handler) {
    _handler = handler;
  }

  /**
   * @return the <code>Map</code> of values corresponding to the
   *         <code>view</code> scope.
   */
  public Map getViewParams() {
    return (MapScope) super.getScope(VIEW_SCOPE);
  }

  /*
  * @see org.sapia.soto.state.Input#getInputStream()
  */
 public InputStream getInputStream() throws IOException {
   return _req.getInputStream();
 }

 /**
  * @see org.sapia.soto.state.Output#getOutputStream()
  */
 public OutputStream getOutputStream() throws IOException {
   if(_output == null) {
     _output = _res.getOutputStream();
   }
   return _output;
 }

 /**
  * @see org.sapia.soto.state.Output#setOutputStream(java.io.OutputStream)
  */
 public void setOutputStream(OutputStream os) throws IOException {
   _output = os;
 }
 
 /**
  * Creates a session and returns it, or returns the one that already exists.
  *
  * @return a <code>Session</code>.
  */
 public Session createSession(){
   return getSession();
 }
 
 /**
  * @return the <code>Session</code> to which this instance is associated,
  * or <code>null</code> if no such session has yet been created - or if it
  * has been destroyed or timed-out.
  */
 public Session getSession(){
   return ((SessionScope)super.getScope(SESSION_SCOPE)).getSession();    
 }
 
 /**
  * Destroys the session to which this instance is associated.
  */
 public void destroySession(){
   Session sess = ((SessionScope)super.getScope(SESSION_SCOPE)).getSession();
   if(sess != null){
     sess.destroy();
   }
 }
 
 public String[] getParameterValues(String key){
   try{
     return _req.getParameters().getParameters(key);
   }catch(IOException e){
     throw new RuntimeException(e);
   }
 }
 
 public List getParameterValueList(String key){
   String[] values = getParameterValues(key);
   if(values == null) return null;
   List lst = new ArrayList();
   for(int i = 0; i < values.length; i++){
     lst.add(values[i]);
   }
   return lst;
 } 
 
 public Request getRequest(){
   return _req;
 }
 
 public Response getResponse(){
   return _res;
 }
}
