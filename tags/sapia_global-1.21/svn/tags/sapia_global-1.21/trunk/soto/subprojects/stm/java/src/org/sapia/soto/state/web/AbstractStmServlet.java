package org.sapia.soto.state.web;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sapia.soto.Debug;
import org.sapia.soto.NotFoundException;
import org.sapia.soto.SotoContainer;
import org.sapia.soto.state.Context;
import org.sapia.soto.state.dispatcher.Dispatcher;
import org.sapia.resource.Resource;

/**
 * This servlet must be inherited by classes that in turn must provide a <code>Context</code>
 * implementation every time a request is serviced. An instance of this class loads a
 * STM-based application and manages its lifecycle. A <code>SotoContainer</code>
 * is internally kept.
 * <p>
 * An instance of this class expects a <code>Dispatcher</code> to be present
 * in the container (it is thus up to the developer to provide a dispatcher
 * as part of his/her application). The requests are handed over to the dispatcher, 
 * which internally dispatches it to the proper state machine.
 * <p>
 * An instance of this class expects the following configuration parameters
 * as part of its <code>web.xml</code> descriptor:
 *
 * <ul>
 *  <li><code>soto-config-resource</code>: the path to the Soto configuration file that
 *  should be loaded within the internal SotoContainer (mandatory).
 *
 *  <li><code>soto-dispatcher-id</code>: the Soto service identifier of the dispatcher
 *  to use when handing over requests (mandatory).
 *
 *  <li><code>soto-servlet-synchronized</code>: if "true", this instance wills service
 *  requests in a synchronized block (optional).
 *
 *  <li><code>soto-error-state</code>: the STM state to trigger if an error occurs
 *  when servicing the request (optional).
 *
 * </ul>
 *
 * In addition, application specific parameters can be defined. All servlet parameters
 * are passed to the SotoContainer at load time (so that they can be recuperated
 * using the <code>${param_name}</code> notation from within the configuration).
 * <p>
 * Furthermore, as an additional initialization parameter that is passed to the
 * container, the servlet's "root" (the root directory under the "real path",
 * as returned by the <code>getRealPath()</code> method of this instance's
 * <code>ServletContext</code>) is also made available to the underlying
 * Soto container configuration, under the <code>RealPath</code> key.
 * <p>
 * Additionally, as a convenience, an instance of this class exports the following objects
 * to the <code>view</code> scope, under the names given below:
 * <ul>
 *   <li>HttpRequest: the current HttpServletRequest.
 *   <li>HttpResponse: the current HttpServletResponse.
 *   <li>HttpSession: the current HttpSession (if one exists at that time).
 *   <li>ServletContext: the ServletContext of this instance.
 * </ul>
 * <p>
 * Finally, note that an instance of this class enriches Soto's resource 
 * resolving mechanism (see Soto's docs for more on this) by adding a custom
 * resource handler to the <code>SotoContainer</code>. The handler resolves resources
 * through the <code>getRealPath()</code> method of this instance's <code>ServletContext</code>.
 *
 * @see org.sapia.soto.state.dispatcher.Dispatcher
 * @see WebContext
 * @see ServletResourceHandler
 * 
 * @author Yanick Duchesne
 *         <dl>
 *         <dt><b>Copyright: </b>
 *         <dd>Copyright &#169; 2002-2005 <a
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
public abstract class AbstractStmServlet extends HttpServlet {
  public static final String SOTO_CONFIG_PARAM               = "soto-config-resource";
  public static final String SOTO_SERVICE_ID_PARAM           = "soto-service-id";
  public static final String SOTO_DISPATCHER_ID_PARAM        = "soto-dispatcher-id";  
  public static final String SOTO_SERLVET_SYNCHRONIZED_PARAM = "soto-servlet-synchronized";  
  public static final String SOTO_ERROR_STATE_PARAM          = "soto-error-state";
  
  public static final String REAL_PATH_KEY = "RealPath";
  public static final String CONTEXT_PATH_KEY = "ContextPath";  
  public static final String SESSION_KEY = "HttpSession";
  public static final String REQUEST_KEY = "HttpRequest";
  public static final String RESPONSE_KEY = "HttpResponse";
  public static final String SERVLET_CONTEXT_KEY = "ServletContext";
  public static final String VIEW_SCOPE = "view";
  
  private SotoContainer      _cont                           = new SotoContainer();
  private Dispatcher         _dispatcher;
  protected ServletContext     _ctx;
  private Resource           _configRes;
  private Map                _params;
  private String             _dispatcherServiceId;
  private String             _errorState;
  private boolean            _synchronized;

  public AbstractStmServlet() {
  }

  /**
   * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
   */
  public void init(ServletConfig conf) throws ServletException {
    _ctx = conf.getServletContext();

    String config = conf.getInitParameter(SOTO_CONFIG_PARAM);
    String dispatcherServiceId = conf.getInitParameter(SOTO_SERVICE_ID_PARAM);
    if(dispatcherServiceId == null){
      dispatcherServiceId = conf.getInitParameter(SOTO_DISPATCHER_ID_PARAM);
    }

      _errorState = conf.getInitParameter(SOTO_ERROR_STATE_PARAM);
    if(Debug.DEBUG){
      Debug.debug(getClass(), "Error state: " + _errorState);
    }
    if(_errorState != null){
      _errorState = _errorState.replace('.', '/');
    }
    
    String synch = conf.getInitParameter(SOTO_SERLVET_SYNCHRONIZED_PARAM);
    
    if(synch != null){
      _synchronized = synch.equalsIgnoreCase("true");
    }
    if(Debug.DEBUG){
      Debug.debug(getClass(), "Synchronized : " + _errorState);
    }
    // Getting expected init. params
    if(config == null) {
      config = System.getProperty(SOTO_CONFIG_PARAM);
    }

    if(config == null) {
      throw new ServletException("Servlet init parameter not specified: "
          + SOTO_CONFIG_PARAM);
    }

    if(dispatcherServiceId == null) {
      dispatcherServiceId = System.getProperty(SOTO_SERVICE_ID_PARAM);
      if(dispatcherServiceId == null){
        dispatcherServiceId = System.getProperty(SOTO_DISPATCHER_ID_PARAM);
      }
    }

    _cont.getResourceHandlers().prepend(new ServletResourceHandler(_ctx));
  
    // Getting Soto config
    Resource configRes;

    try {
      configRes = _cont.getResourceHandlerFor(config).getResourceObject(config);
    } catch(IOException e) {
      throw new ServletException("Problem with resource: " + config, e);
    }

    // Passing init and context params to Soto

    Map params = new HashMap();
    
    Enumeration en = conf.getServletContext().getAttributeNames();
    while(en.hasMoreElements()) {
      String name = (String) en.nextElement();
      params.put(name, conf.getServletContext().getAttribute(name));
    }    
    
    en = conf.getInitParameterNames();
    while(en.hasMoreElements()) {
      String name = (String) en.nextElement();
      params.put(name, conf.getInitParameter(name));
    }
    
    init(configRes, dispatcherServiceId, params);
  }

  public void reload() throws ServletException {
    _cont.dispose();
    _cont = null;
    init(_configRes, _dispatcherServiceId, _params);
  }

  /**
   * @see javax.servlet.GenericServlet#destroy()
   */
  public void destroy() {
    if(_cont != null) {
      _cont.dispose();
    }
  }

  public SotoContainer getContainer() {
    return _cont;
  }

  void init(Resource configRes, String dispatcherServiceId, Map params)
      throws ServletException {
    
    if(Debug.DEBUG){
      Debug.debug(getClass(), "Initializing...");
    }
    try {
      if(_cont == null) {
        _cont = new SotoContainer();
      }

      if(Debug.DEBUG){
        Debug.debug(getClass(), "Loading soto configuration from " + configRes.getURI());
      }      
      
      params.put(REAL_PATH_KEY, _ctx.getRealPath(""));
      _cont.load(configRes.getURI(), params);
    } catch(Exception e) {
      if(Debug.DEBUG){
        Debug.debug(getClass(), "Exception caugth");
        Debug.debug(getClass(), e);
      }            
      logException(e);
      throw new ServletException(
          "Could not load Soto container configuration: "
              + configRes.toString(), e);
    }

    try {
      _cont.start();
    } catch(Exception e) {
      logException(e);
      throw new ServletException("Could not start Soto container", e);
    }
    
    if(dispatcherServiceId == null) {
      try{
        _dispatcher = (Dispatcher)_cont.lookup(Dispatcher.class);
      }catch(NotFoundException e){
        logException(e);
        throw new ServletException("Problem looking up dispatcher", e);
      }
    }
    else{
      try {
        _dispatcher = (Dispatcher) _cont.lookup(dispatcherServiceId);
      } catch(NotFoundException e) {
        logException(e);
        throw new ServletException("Problem looking up dispatcher: "
            + dispatcherServiceId, e);
      }
    }
    _configRes = configRes;
    _dispatcherServiceId = dispatcherServiceId;
    _params = params;
  }

  /**
   * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  protected void service(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException{
    if(_synchronized){
      if(Debug.DEBUG){
        Debug.debug(getClass(), "Performing synchronized request");
      }
      synchronized(this){
        doService(req, res);
      }
    }
    else{
      doService(req, res);
    }
  }
  
  /**
   * Template method that must be implemented by inheriting classes.
   * 
   * @param req the current <code>HttpServletRequest</code>
   * @param res the current <code>HttpServletResponse</code>
   * @return a <code>Context</code> instance.
   * @throws ServletException
   * @throws IOException
   */
  protected abstract Context createContext(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException;
  
  protected void doService(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    
    Context ctx = createContext(req, res);
    
    /*
     * The following is a hack in order to pass servlet context, session, request and response objects to 
     * eventual views. 
     */
    ctx.put(SESSION_KEY, req.getSession(false), VIEW_SCOPE);
    ctx.put(REQUEST_KEY, req, VIEW_SCOPE);
    ctx.put(RESPONSE_KEY, res, VIEW_SCOPE);
    ctx.put(SERVLET_CONTEXT_KEY, _ctx, VIEW_SCOPE);
    
    OutputStream out = null;
    try {
      
      out = res.getOutputStream();
      if(Debug.DEBUG) {
        Debug.debug(getClass(), "Dispatching request: " + req.getPathInfo());
      }
      if(!_dispatcher.dispatch(req.getPathInfo(), ctx)) {
        if(Debug.DEBUG){
          Debug.debug(getClass(), "Request not dispatched...");
        }
        res.setStatus(404);
        PrintWriter pw = new PrintWriter(res.getOutputStream());
        pw.println("<html><body><h1>Error 404</h1>Could not process resource: "
            + req.getPathInfo() + "</body></html>");
        pw.flush();
        pw.close();
        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return;
      } else {
        if(Debug.DEBUG){
          Debug.debug(getClass(), "Request dispatched...");
        }
      }

    } catch(Exception e) {
      logException(e);
      if(Debug.DEBUG){
        Debug.debug(getClass(), "Dispatching error to state: " + _errorState);
      }
      if(_errorState != null){
        try{        
          ctx.push(e);
          if(!_dispatcher.dispatch(_errorState, ctx)){
            if(Debug.DEBUG){
              Debug.debug("Error state not matched: " + _errorState);
            }
            throw new ServletException("Could not execute state: "
              + req.getPathInfo(), e);        
          }
        }catch(Exception e2){
          throw new ServletException("Could not execute state: "
              + req.getPathInfo(), e);        
        }
      }
      else{
        throw new ServletException("Could not execute state: "
            + req.getPathInfo(), e);
      }
    } finally {
      if(out != null){
        out.flush();
        out.close();
      }    
    }
  }

  private void logException(Exception e) {
    System.out
        .println("======================= ERROR OCCURED =======================");
    System.out.println(e.getMessage());
    e.printStackTrace(System.out);
    System.out
        .println("=============================================================");
  }
}
