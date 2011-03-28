package org.sapia.soto.state.cocoon;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.sapia.resource.Resource;
import org.sapia.resource.ResourceHandler;
import org.sapia.soto.Env;
import org.sapia.soto.NotFoundException;
import org.sapia.soto.SotoContainer;
import org.sapia.soto.state.dispatcher.Dispatcher;
import org.sapia.soto.util.Utils;
import org.xml.sax.ContentHandler;

/**
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class CocoonDispatcher implements 
  LogEnabled, Initializable, Disposable,
  Composable, ThreadSafe, Parameterizable, Component{

  public static final String ROLE = CocoonDispatcher.class.getName();
  
  public static final String SOTO_CONFIG           = "soto-config-resource";
  public static final String DISPATCHER_SERVICE_ID = "soto-dispatcher-id";
  protected Logger           _log;
  protected ComponentManager _manager;
  private String             _sotoConfigResource;
  private Resource           _toWatch;
  private long               _lastModified;
  private String             _dispatcherServiceId;
  private SotoContainer      _container;
  private Dispatcher         _dispatcher;
  private Env                _env;
  /**
   * @see org.apache.avalon.framework.logger.LogEnabled#enableLogging(Logger)
   */
  public void enableLogging(Logger logger) {
    _log = logger.getChildLogger(this.getClass().getName());
  }
  
  /**
   * @see org.apache.avalon.framework.activity.Initializable#initialize()
   */
  public void initialize() throws Exception {
    _container = new SotoContainer();
    _container.getResourceHandlers().prepend(new CocoonResourceHandler(this));
    _container.load(_sotoConfigResource);
    _toWatch =_container.getResourceHandlerFor(_sotoConfigResource).getResourceObject(_sotoConfigResource);
    _lastModified = _toWatch.lastModified();
    _container.start();    
    _env = _container.toEnv();
  }
  
  /**
   * @see org.apache.avalon.framework.parameters.Parameterizable#parameterize(org.apache.avalon.framework.parameters.Parameters)
   */
  public void parameterize(Parameters params) throws ParameterException {
    _sotoConfigResource = params.getParameter(SOTO_CONFIG);
  
    if(_sotoConfigResource == null) {
      throw new ParameterException("Missing configuration parameter: "
          + SOTO_CONFIG);
    }
  
    _dispatcherServiceId = params.getParameter(DISPATCHER_SERVICE_ID);
    try{
      doLookupDispatcher();
    }catch(NotFoundException e){
      throw new ParameterException("Could not lookup dispatcher", e);
    }
  }
  
  /**
   * @see org.apache.avalon.framework.activity.Disposable#dispose()
   */
  public void dispose() {
    _container.dispose();
  }
  
  /**
   * @see org.apache.avalon.framework.component.Composable#compose(ComponentManager)
   */
  public void compose(ComponentManager manager) throws ComponentException {
    _manager = manager;
  }

  public boolean dispatch(String uri, ContentHandler handler, Map objectModel) throws Exception {
    // The following code checks if the soto configuration has changed. It is 
    // not intented for prod: the "old" container might be disposed while threads are 
    // currently using it. Thus, the config should not be changed on the server at runtime
    // when in prod. For dev purposes though, it is tolerable and greatly increases 
    // development speed.     
    if(_lastModified != _toWatch.lastModified()){
      synchronized(this){
        if(_lastModified != _toWatch.lastModified()){
          logger().debug("Change detected in soto configuration; reloading " + _sotoConfigResource);          
          SotoContainer oldContainer = _container;
          SotoContainer container = new SotoContainer();
          container.getResourceHandlers().prepend(new CocoonResourceHandler(this));
          container.load(_sotoConfigResource);
          container.start();
          _env = container.toEnv();
          _container = container;
          doLookupDispatcher();
          oldContainer.dispose();
          _lastModified = _toWatch.lastModified();
        }
      }
    }
    return _dispatcher.dispatch(uri, new CocoonContext(_env, handler, objectModel));
  }
  
  private void doLookupDispatcher() throws NotFoundException{
    if(_dispatcherServiceId == null) {
      _dispatcher = (Dispatcher)_container.lookup(Dispatcher.class);
    }
    else{
      _dispatcher = (Dispatcher)_container.lookup(_dispatcherServiceId);
    }
  }

  protected Logger logger() {
    return _log;
  }

  static final class CocoonResourceHandler implements ResourceHandler {
  
    public static final String      CONTEXT_SCHEME = "context";
    public static final String      COCOON_SCHEME  = "cocoon";
    private CocoonDispatcher _owner;
  
    CocoonResourceHandler(CocoonDispatcher owner) {
      _owner = owner;
    }
  
    /**
     * @see org.sapia.soto.util.ResourceHandler#getResource(java.lang.String)
     */
    public InputStream getResource(String uri) throws IOException {
      return getResourceObject(uri).getInputStream();
    }
  
    /**
     * @see org.sapia.soto.util.ResourceHandler#getResourceObject(java.lang.String)
     */
    public Resource getResourceObject(String uri) throws IOException {
      SourceResolver res = null;
  
      try {
        res = (SourceResolver) _owner._manager.lookup(SourceResolver.ROLE);
        String toResolve = Utils.chopScheme(uri);
        toResolve = CONTEXT_SCHEME + "://" + Utils.chopScheme(uri);
        return new CocoonResource(res.resolveURI(toResolve));
      } catch(ComponentException e) {
        throw new IOException(e.getMessage());
      } finally {
        if(res != null) {
          _owner._manager.release(res);
        }
      }
    }
  
    /**
     * @see org.sapia.soto.util.ResourceHandler#accepts(java.lang.String)
     */
    public boolean accepts(String uri) {
      return true;
    }
    
    public boolean accepts(URI uri) {
      return true;
    }
  
  }
  
  public static final class CocoonResource implements Resource{
    
    private Source _src;
    
    CocoonResource(Source src){
      _src = src;
    }
    
    /**
     * @see org.sapia.soto.util.Resource#getInputStream()
     */
    public InputStream getInputStream() throws IOException {
      return _src.getInputStream();
    }
    
    /**
     * @see org.sapia.soto.util.Resource#getURI()
     */
    public String getURI() {
      return _src.getURI();
    }
    
    /**
     * @see org.sapia.soto.util.Resource#lastModified()
     */
    public long lastModified() {
      return _src.getLastModified();
    }
    
    public Resource getRelative(String uri) throws IOException {
      throw new UnsupportedOperationException();
    }
  }

}
