package org.sapia.soto;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.sapia.soto.config.SotoIncludeContext;
import org.sapia.soto.config.SotoIncludeContextFactory;
import org.sapia.soto.lifecycle.DefaultLifeCycleManager;
import org.sapia.soto.lifecycle.LifeCycleManager;
import org.sapia.resource.ClasspathResourceHandler;
import org.sapia.resource.FileResourceHandler;
import org.sapia.resource.Resource;
import org.sapia.resource.ResourceHandler;
import org.sapia.resource.ResourceNotFoundException;
import org.sapia.resource.UrlResourceHandler;
import org.sapia.resource.include.IncludeConfig;
import org.sapia.resource.include.IncludeState;
import org.sapia.soto.util.NestedIOException;
import org.sapia.soto.util.SotoResourceHandlerChain;
import org.sapia.soto.util.Utils;
import org.sapia.util.text.MapContext;
import org.sapia.util.text.SystemContext;
import org.sapia.util.text.TemplateContextIF;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.Dom4jProcessor;
import org.sapia.util.xml.confix.ObjectFactoryIF;

/**
 * An intance of this class contains service instances and manages their
 * life-cycle.
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
public class SotoContainer implements SotoConsts{

  private SotoResourceHandlerChain   _resourceHandlers = new SotoResourceHandlerChain();
  private Map                        _services           = new HashMap();
  private Map                        _objects            = Collections.synchronizedMap(new HashMap());
  private Map                        _lifeCycleManagers  = Collections.synchronizedMap(new HashMap());  
  private List                       _toStart            = new ArrayList();
  private List                       _metaData           = new ArrayList();
  private SotoApplicationFactory     _fac;
  private boolean                    _started;
  private boolean                    _bootstrapLoaded;
  private Env                        _env;
  private Settings                   _settings         = new Settings();

  /**
   * Constructor for SotoContainer.
   */
  public SotoContainer() {
    _fac = new SotoApplicationFactory(this);
    _resourceHandlers.append(new FileResourceHandler());
    _resourceHandlers.append(new ClasspathResourceHandler());
    _resourceHandlers.append(new UrlResourceHandler());
    _lifeCycleManagers.put(SOTO_LIFE_CYCLE, new DefaultLifeCycleManager());
    _env = new EnvImpl(this);
  }
  
  /**
   * @return this instance's settings.
   */
  public Settings getSettings(){
    return _settings;
  }

  /**
   * Returns the list of services held within this instance.
   * 
   * @return a <code>List</code> of <code>ServiceTag</code>.
   */
  List getServiceList() {
    return _toStart;
  }

  /**
   * Binds the given object to this instance, using the specified identifier.
   * 
   * @param meta
   *          a <code>ServiceMetaData</code>.
   */
  public synchronized void bind(ServiceMetaData meta) throws DuplicateException, Exception {
    if(meta.getServiceID() != null
        && _services.get(meta.getServiceID()) != null) {
      throw new DuplicateException("A service already exists for ID: "
          + meta.getServiceID());
    }
    if(!meta.isInit()){
      meta.init();
    }
    synchronized(this){
      _services.put(meta.getServiceID(), meta);
      if(!_started) {
        _toStart.add(meta);
      } else {
        _metaData.add(meta);
      }
    }
  }
  
  /**
   * Binds an object under a given identifier. That object can be retrieved
   * later on through configuration by using its identifier.
   *
   * @param id an arbitrary identifier.
   * @param ref an <code>Object</code>
   */
  public synchronized void bind(String id, Object ref){
    _objects.put(id, ref);
  }
  
  /**  
   * @param id the identifier under which the expected object was registered.
   * @return the <code>Object</code> corresponding to the given identifier.
   *
   * @see #register(java.lang.String, java.lang.Object)
   */
  public Object resolveRef(String id) throws NotFoundException{
    Object o = _objects.get(id);
    if(o == null){
      throw new NotFoundException("No object found for reference: " + id);
    }
    return o;
  }

  /**
   * Looks up for the object identified by the given ID and returns it.
   * 
   * @throws NotFoundException
   *           if no object could be found.
   */
  public Object lookup(String id) throws NotFoundException {
    //checkStarted();
    ServiceMetaData service = (ServiceMetaData) _services.get(id);

    if(service == null) {
      throw new NotFoundException(id);
    }
    
    LifeCycleManager lcm = service.getLifeCycleManager();
    return lcm.lookupService(id, service.getService());
  }

  /**
   * Looks up for the object that is an instance of the specified interface.
   * <p/>This method is suboptimal: all services are traversed and tested
   * against the specified interface; therefore, this method should not be used
   * to perform repetitive lookups, but to lookup services at initialization time.
   * 
   * @param instanceOf
   *          an interface, specified as a <code>Class</code> instance.
   * @throws NotFoundException
   *           if no object could be found.
   */
  public Object lookup(Class instanceOf) throws NotFoundException {
    if(!instanceOf.isInterface()) {
      throw new IllegalArgumentException(
          "Lookup performed against interfaces only; this is a class: "
              + instanceOf.getName());
    }
    List services = lookup(new InstanceOfSelector(instanceOf), true);
    if(services.size() > 1) {
      throw new IllegalStateException("More than one match for: "
          + instanceOf.getName());
    } else if(services.size() == 0) {
      throw new NotFoundException("No service implements: "
          + instanceOf.getName());
    }
    ServiceMetaData meta = (ServiceMetaData)services.get(0);
    LifeCycleManager lcm = meta.getLifeCycleManager();
    return lcm.lookupService(meta.getServiceID(), meta.getService());    
  }

  /**
   * Internally adds the services that have been accepted by the passed in
   * selector and returns them in a list to the caller.
   * 
   * @param selector
   *          a <code>ServiceSelector</code>.
   * @param returnMetadata if <code>true</code>, this instance will return
   * <code>ServiceMetadata</code> instances. Else, it will return <code>Service</code>
   * instances.
   * @return a <code>List</code> of <code>Service</code> or 
   *          <code>ServiceMetadata</code> instances.
   */
  public List lookup(ServiceSelector selector, boolean returnMetadata) {
    List toReturn = new ArrayList();
    ServiceMetaData meta;

    if(_toStart.size() > 0) {
      for(int i = 0; i < _toStart.size(); i++) {
        meta = (ServiceMetaData) _toStart.get(i);
        if(selector.accepts(meta)) {
          if(returnMetadata){
            toReturn.add(meta);
          }
          else{
            LifeCycleManager lcm = meta.getLifeCycleManager();
            toReturn.add(lcm.lookupService(meta.getServiceID(), meta.getService()));
          }
        }
      }
    }
    if(_metaData.size() > 0) {
      for(int i = 0; i < _metaData.size(); i++) {
        meta = (ServiceMetaData) _metaData.get(i);
        if(selector.accepts(meta)) {
          if(returnMetadata){
            toReturn.add(meta);
          }
          else{
            LifeCycleManager lcm = meta.getLifeCycleManager();
            toReturn.add(lcm.lookupService(meta.getServiceID(), meta.getService()));
          }
        }
      }
    }
    return toReturn;
  }

  /**
   * Loads the Soto XML descriptor whose stream is given.
   * 
   * @param is
   *          an <code>InputStream</code>.
   * @param vars 
   *          a <code>Map</code> of name/value pairs that can be recuperated in
   *          the configuration using the ${varname} notation.
   * @return this instance.
   */
  public SotoContainer load(InputStream is, Map vars) throws Exception {
    if(vars == null) {
      vars = new HashMap();
    }

    TemplateContextIF ctx = new MapContext(vars, new SystemContext(), true);

    if(_fac == null) {
      _fac = new SotoApplicationFactory(this);
    }

    _fac.setContext(ctx);

    loadBootStrap(ctx);
    
    try {
      Dom4jProcessor proc = new Dom4jProcessor(_env.getObjectFactory());
      proc.process(Utils.replaceVars(ctx, is, "null"));
    } finally{
      is.close();
    }
    
    return this;
  }

  /**
   * Loads the Soto XML descriptor whose stream is given.
   * 
   * @param is
   *          an <code>InputStream</code>.
   * @return this instance.
   */
  public SotoContainer load(InputStream is) throws Exception {
    return load(is, null);
  }

  /**
   * Loads the Soto XML descriptor whose resource is given.
   * 
   * @param resourceName
   *          the name of the classpath resource that contains the
   *          configuration.
   * @param vars
   *          a <code>Map</code> of name/value pairs that can be recuperated in
   *          the configuration using the ${varname} notation.
   * @throws Exception
   * @return this instance.
   */
  public SotoContainer load(String resourceName, Map vars) throws Exception {
    if(Utils.hasScheme(resourceName)){
      return include(resourceName, vars);
    }
    else{
      return include("resource:/" + resourceName, vars);
    }
  }

  /**
   * Loads the Soto XML descriptor whose resource is given.
   * 
   * @param resourceName
   *          the name of the classpath resource that contains the
   *          configuration.
   * @throws Exception
   * @return this instance.
   */
  public SotoContainer load(String resourceName) throws Exception {
    return load(resourceName, null);
  }

  /**
   * Loads the Soto XML descriptor whose file is given.
   * 
   * @param f
   *          a <code>File</code>.
   * @param map 
   *          a <code>Map</code> of name/value pairs that can be recuperated in
   *          the configuration using the ${varname} notation.
   * @return this instance
   */
  public SotoContainer load(File f, Map map) throws Exception {
    String path = f.getAbsolutePath().trim();

    if(path.charAt(0) == '/') {
      return include("file:" + f.getAbsolutePath(), map);
    } else {
      return include("file:/" + f.getAbsolutePath(), map);
    }
  }

  /**
   * Loads the Soto XML descriptor whose URL is given.
   * 
   * @param url
   *          an <code>URL</code>.
   * @param map 
   *          a <code>Map</code> of name/value pairs that can be recuperated in
   *          the configuration using the ${varname} notation.
   * @return this instance
   */
  public SotoContainer load(URL url, Map map) throws Exception {
    return include(url.toExternalForm(), map);
  }

  /**
   * Loads the Soto XML descriptor whose file is given.
   * 
   * @param f
   *          a <code>File</code>.
   * @return this instance.
   */
  public SotoContainer load(File f) throws Exception {
    return load(f, null);
  }

  /**
   * Internally starts the services bound to this container.
   * 
   * @see Service#start()
   */
  public void start() throws Exception {
    ServiceMetaData meta;

    for(int i = 0; i < _toStart.size(); i++) {
      meta = (ServiceMetaData) _toStart.get(i);
      meta.start();
      _metaData.add(meta);
    }

    _started = true;
    _toStart.clear();
    
    Iterator lcms = _lifeCycleManagers.values().iterator();
    while(lcms.hasNext()){
      LifeCycleManager lcm = (LifeCycleManager)lcms.next();
      lcm.postInit(toEnv());
    }
  }

  /**
   * Internally shuts down all services and layers.
   * 
   * @see Service#dispose()
   * @see Layer#dispose()
   */
  public void dispose() {
    for(int i = _metaData.size() - 1; i >= 0; i--) {
      ((ServiceMetaData) _metaData.get(i)).dispose();
    }
    Iterator lcms = _lifeCycleManagers.values().iterator();
    while(lcms.hasNext()){
      LifeCycleManager lcm = (LifeCycleManager)lcms.next();
      lcm.postInit(toEnv());
    }    
  }

  /**
   * Returns the resource handlers held by this instance. New handlers can be
   * added by client applications.
   * 
   * @return the <code>ResourceHandlerChain</code> that this instance holds.
   */
  public SotoResourceHandlerChain getResourceHandlers() {
    return _resourceHandlers;
  }

  /**
   * Returns the resource handler that corresponds to the given scheme.
   *
   * @return a <code>ResourceHander</code>.
   */
  public ResourceHandler getResourceHandlerFor(String uri) {
    ResourceHandler handler = _resourceHandlers.select(uri);

    if(handler == null) {
      throw new IllegalArgumentException("No resource handler defined for: "
          + uri);
    }

    return handler;
  }
  
  /**
   * Registers the given life-cycle manager with this instance.
   * 
   * @param name the unique name under which to bind the given {@link LifeCycleManager} in
   * this container.
   * @param manager a {@link LifeCycleManager}
   */
  public void registerLifeCycleManager(String name, LifeCycleManager manager){
    if(_lifeCycleManagers.get(name) != null){
      throw new IllegalArgumentException("LifeCycleManager already registered for: " + name);
    }
    _lifeCycleManagers.put(name, manager);
    manager.init(toEnv());
  }
  
  /**
   * @param name the name of the {@link LifeCycleManager} to return.
   * @return the {@link LifeCycleManager} corresponding to the given name.
   * @throws IllegalArgumentException if no such instance could be found.
   */
  public LifeCycleManager getLifeCycleManagerFor(String name) throws IllegalArgumentException{
    LifeCycleManager lcm = (LifeCycleManager)_lifeCycleManagers.get(name);
    if(lcm == null){
      throw new IllegalArgumentException("No LifeCycleManager found for: " + name);
    }
    return lcm;
  }

  /**
   * @return the default life-cycle manager of this container.
   */
  public LifeCycleManager getDefaultLifeCycleManager(){
    return getLifeCycleManagerFor(SOTO_LIFE_CYCLE);
  }  

  /**
   * @return the <code>SotoApplicationFactory</code> that is used to create
   *         objects from an XML configuration.
   */
  public SotoApplicationFactory getApplicationFactory() {
    return _fac;
  }

  private void checkStarted() {
    if(!_started) {
      throw new IllegalStateException(
          "Soto container must be started prior to operation being performed");
    }
  }

  private SotoContainer include(String uri, Map vars) throws Exception {
    

    TemplateContextIF ctx;
    
    if(vars == null) {
      vars = new HashMap();
    }

    ctx = new MapContext(vars, new SystemContext(), true);

    if(_fac == null) {
      _fac = new SotoApplicationFactory(this);
    }
    
    _fac.setContext(ctx);

    loadBootStrap(ctx);   
    
    Debug.debug(getClass(), "Loading configuration: " + uri);
    SotoIncludeContextFactory fac = new SotoIncludeContextFactory(toEnv(), ctx);
    IncludeConfig conf = IncludeState.createConfig(SOTO_INCLUDE_KEY, fac, this._resourceHandlers);
    IncludeState.createContext(uri, conf).include(ctx);
    return this;
  }
  
  private void loadBootStrap(TemplateContextIF vars) throws Exception{
    if(!_bootstrapLoaded){
      String bootstrapProp = (String)vars.getValue(SotoConsts.SOTO_BOOTSTRAP);
      if(bootstrapProp != null){
        String[] uris = Utils.split(bootstrapProp, ';', true);
        SotoIncludeContextFactory fac = new SotoIncludeContextFactory(toEnv(), vars);
        IncludeConfig conf = IncludeState.createConfig(SOTO_INCLUDE_KEY, fac, this._resourceHandlers);
        
        for(int i = 0; i < uris.length; i++){
          IncludeState.createContext(uris[i], conf).include(vars);
        }
        _bootstrapLoaded = true;
      } 
    }
  }

  /**
   * @return the <code>Env</code> instance that corresponds to this container.
   */
  public Env toEnv() {
    return _env;
  }

  // INNER CLASSES ////////////////////////////////////////////////////
  public static final class EnvImpl implements Env {
    SotoContainer _cont;

    EnvImpl(SotoContainer cont) {
      _cont = cont;
    }
    
    /**
     * @see SotoContainer#getSettings()
     */
    public Settings getSettings() {
      return _cont.getSettings();
    }
    
    /**
     * @see org.sapia.soto.Env#getResourceHandlerFor(java.lang.String)
     */
    public ResourceHandler getResourceHandlerFor(String uri) {
      return _cont.getResourceHandlerFor(uri);
    }

    /**
     * @see org.sapia.soto.Env#lookup(java.lang.String)
     */
    public Object lookup(String serviceId) throws NotFoundException {
      return _cont.lookup(serviceId);
    }

    /**
     * @see org.sapia.soto.Env#lookup(java.lang.Class)
     */
    public Object lookup(Class instanceOf) throws NotFoundException {
      return _cont.lookup(instanceOf);
    }

    /**
     * @see org.sapia.soto.Env#lookup(org.sapia.soto.ServiceSelector, boolean)
     */
    public List lookup(ServiceSelector selector, boolean returnMetadata) {
      return _cont.lookup(selector, returnMetadata);
    }

    /**
     * @see org.sapia.soto.Env#getResourceHandlers()
     */
    public SotoResourceHandlerChain getResourceHandlers() {
      return _cont.getResourceHandlers();
    }

    /**
     * @see org.sapia.soto.Env#resolveStream(java.lang.String)
     */
    public InputStream resolveStream(String uri) throws IOException {
      try{
        SotoIncludeContextFactory fac = new SotoIncludeContextFactory(this, SotoIncludeContext.currentTemplateContext());
        IncludeConfig conf = IncludeState.createConfig(SOTO_INCLUDE_KEY, fac, _cont._resourceHandlers);
        return IncludeState.createContext(uri, conf).includeResource().getInputStream();
      }catch(ResourceNotFoundException e){
        try{
          return _cont.getResourceHandlers().resolveResource(uri).getInputStream();
        }catch(ResourceNotFoundException e2){
          throw e;
        }
      }catch(Exception e){
        throw new NestedIOException("Could not load resource for URI " + uri, e);
      }
    }

    /**
     * @see org.sapia.soto.Env#resolveResource(java.lang.String)
     */
    public Resource resolveResource(String uri) throws IOException {
      try{
        SotoIncludeContextFactory fac = new SotoIncludeContextFactory(this, SotoIncludeContext.currentTemplateContext());
        IncludeConfig conf = IncludeState.createConfig(SOTO_INCLUDE_KEY, fac, _cont._resourceHandlers);
        return IncludeState.createContext(uri, conf).includeResource();
      }catch(ResourceNotFoundException e){
        try{
          return _cont.getResourceHandlers().resolveResource(uri);          
        }catch(ResourceNotFoundException e2){
          throw e;
        }
      }catch(Exception e){
        throw new NestedIOException("Could not load resource for URI " + uri, e);
      }
      
    }

    /**
     * @see org.sapia.soto.Env#getObjectFactory()
     */
    public ObjectFactoryIF getObjectFactory() {
      return _cont._fac;
    }
    
    public void bind(String id, Object o){
      _cont.bind(id, o);
    }
    
    public Object resolveRef(String id) throws NotFoundException{
      return _cont.resolveRef(id);
    }
    
    public Object include(String uri, Map params) throws ConfigurationException{
      if(params == null) params = new HashMap();
      try{
        TemplateContextIF templateContext = new MapContext(params, SotoIncludeContext.currentTemplateContext(), false);
        SotoIncludeContextFactory fac = new SotoIncludeContextFactory(this, templateContext);
        IncludeConfig conf = IncludeState.createConfig(SOTO_INCLUDE_KEY, fac, _cont._resourceHandlers);
        return IncludeState.createContext(uri, conf).include(templateContext);
      }catch(Exception e){
        throwExc("Could not include " + uri, e);
        return null;
      }
    }
    
    public Object include(String uri, TemplateContextIF context) throws ConfigurationException{
      try{
        SotoIncludeContextFactory fac = new SotoIncludeContextFactory(this, context);
        IncludeConfig conf = IncludeState.createConfig(SOTO_INCLUDE_KEY, fac, _cont._resourceHandlers);
        return IncludeState.createContext(uri, conf).include(context);
      }catch(Exception e){
        throwExc("Could not include " + uri, e);
        return null;
      }
    }    
    
    public Resource includeResource(String uri, Map params) throws ConfigurationException{
      if(params == null) params = new HashMap();      
      try{
        TemplateContextIF templateContext = new MapContext(params, SotoIncludeContext.currentTemplateContext(), false);        
        SotoIncludeContextFactory fac = new SotoIncludeContextFactory(this, templateContext);
        IncludeConfig conf = IncludeState.createConfig(SOTO_INCLUDE_KEY, fac, _cont._resourceHandlers);
        return IncludeState.createContext(uri, conf).includeResource(/*new MapContext(params, SotoIncludeContext.currentTemplateContext(), false)*/);
      }catch(Exception e){
        throwExc("Could not include " + uri, e);
        return null;
      }

    }    
    
    public SotoContainer getContainer() {
      return _cont;
    }
  }
  
  private static void throwExc(String msg, Exception e) throws ConfigurationException{
    if(e instanceof ConfigurationException){
      throw (ConfigurationException)e;
    }
    else{
      throw new ConfigurationException(msg, e);
    }
  }
}
