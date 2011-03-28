package org.sapia.soto.state.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.sapia.soto.Env;
import org.sapia.soto.state.ContextImpl;
import org.sapia.soto.state.Input;
import org.sapia.soto.state.MapScope;
import org.sapia.soto.state.Output;
import org.sapia.soto.state.xml.XMLContext;
import org.xml.sax.ContentHandler;

/**
 * Implements the <code>Context</code> interface on top of servlet-related objects
 * (HttpServletRequest, HttpServletResponse, HttpSession):
 * <ul>
 * <li>env : corresponds to the Soto container; used to acquire service
 * instances.
 * <li>session: corresponds to the servlet session (allows setting/getting session
 * values).
 * <li>request: corresponds to the servlet request (allows setting/getting request
 * attributes).
 * <li>response: corresponds to the servlet response (allows setting response
 * headers).
 * <li>headers: corresponds to the servlet response (allows getting request
 * headers and setting response headers).
 * <li>params : corresponds to the servlet request parameters (allows getting
 * request parameters).
 * <li>view : corresponds to the eventual view that is to be generated - the
 * values bound to the view are exported in to the target templating/generation
 * engine's execution variables.
 * <li>
 * </ul>
 * <p>
 * An instance of this class in addition holds the <code>ContentHandler</code>
 * implementation that will eventually receive SAX events from the eventual
 * view.
 * 
 * @author Yanick Duchesne
 *         <dl>
 *         <dt><b>Copyright: </b>
 *         <dd>Copyright &#169; 2002-2003 <a
 *         href="http://www.sapia-oss.org">Sapia Open Source Software </a>. All
 *         Rights Reserved.</dd>
 *         </dt>
 *         <dt><b>License: </b>
 *         <dd>Read the license.txt file of the jar or visit the <a
 *         href="http://www.sapia-oss.org/license.html">license page </a> at the
 *         Sapia OSS web site</dd>
 *         </dt>
 *         </dl>
 */
public class WebContext extends ContextImpl implements XMLContext, 
    Input, Output, WebConsts {
  private ContentHandler     _handler;
  private OutputStream       _output;

  public WebContext(Env env, ContentHandler handler, 
     HttpServletRequest req, HttpServletResponse res) {
    _handler = handler;

    super.addScope(HEADERS_SCOPE, new HeaderScope(req, res));
    super.addScope(SESSION_SCOPE, new SessionScope(req));
    super.addScope(REQUEST_SCOPE, new RequestScope(req));
    super.addScope(RESPONSE_SCOPE, new ResponseScope(res));
    super.addScope(REQ_PARAM_SCOPE, new ReqParamScope(req));
    super.addScope(ENV_SCOPE, new EnvScope(env));

    MapScope view = new MapScope();
    view.put(CONTEXT_PATH_KEY, req.getContextPath());
    view.put(CONTEXT_PATH_KEY2, req.getContextPath());
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

  /**
   * Sets the <code>ContentHandler</code> that this instance holds to
   * <code>null</code>.
   */
  public void disableOutput() {
    _handler = null;
  }

  /**
   * @return the <code>HttpSerlvetRequest</code> that this instance holds.
   */
  public HttpServletRequest getRequest() {
    return ((RequestScope) super.getScope(REQUEST_SCOPE)).getRequest();
  }

  /**
   * @return the <code>HttpServletResponse</code> that this instance holds.
   */
  public HttpServletResponse getResponse() {
    return ((ResponseScope) super.getScope(RESPONSE_SCOPE)).getResponse();
  }

  /**
   * @see org.sapia.soto.state.Input#getInputStream()
   */
  public InputStream getInputStream() throws IOException {
    return ((HttpServletRequest) getRequest()).getInputStream();
  }

  /**
   * @see org.sapia.soto.state.Output#getOutputStream()
   */
  public OutputStream getOutputStream() throws IOException {
    if(_output == null) {
      _output = ((HttpServletResponse) getResponse()).getOutputStream();
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
   * @return a <code>HttpSession</code>.
   */
  public HttpSession createSession(){
    return ((SessionScope)super.getScope(SESSION_SCOPE)).createSession();
  }
  
  /**
   * @return the <code>HttpSession</code> to which this instance is associated,
   * or <code>null</code> if no such session has yet been created - or if it
   * has been destroyed or timed-out.
   */
  public HttpSession getSession(){
    return ((SessionScope)super.getScope(SESSION_SCOPE)).getSession();    
  }
  
  /**
   * Destroys the session to which this instance is associated.
   */
  public void destroySession(){
    HttpSession sess = ((SessionScope)super.getScope(SESSION_SCOPE)).getSession();
    if(sess != null){
      sess.invalidate();
    }
  }
}
