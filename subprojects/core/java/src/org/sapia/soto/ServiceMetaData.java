package org.sapia.soto;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sapia.soto.config.MethodConfig;
import org.sapia.soto.lifecycle.LifeCycleManager;
import org.sapia.util.CompositeRuntimeException;

/**
 * Holds meta information about a given service.
 * 
 * @see org.sapia.soto.Service
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
public class ServiceMetaData implements ServiceConfiguration{
  
  static int STATUS_NONE = 0;
  static int STATUS_INIT = 1;  
  static int STATUS_STARTED = 2;
  
  private int _status = STATUS_NONE;
  private String _id;
  private Object _service;
  private List   _layers;
  private Map _attributes = new HashMap();
  private MethodConfig _init, _start, _dispose;
  private String _type;
  private SotoContainer _container;

  public ServiceMetaData(SotoContainer container, String id, Object service) {
    this(container, id, service, null);
  }

  public ServiceMetaData(SotoContainer container, String id, Object service, List layers) {
    _container = container;
    _id = id;
    _service = service;
    _layers = layers;
  }

  /**
   * @param type the 'type' of service, in terms of its lifecycle.
   * 
   * @see LifeCycleManager
   * @see SotoContainer#registerLifeCycleManager(String, LifeCycleManager)
   */
  public void setType(String type){
    _type = type;
  }

  /**
   * @return the {@link LifeCycleManager} corresponding to this instance.
   * @see #setType(String)
   */
  public LifeCycleManager getLifeCycleManager(){
    if(_type == null){
      return _container.getDefaultLifeCycleManager();
    }
    else{
      return _container.getLifeCycleManagerFor(_type);
    }
  }
  
  /**
   * Returns the identifier of the service to which this instance corresponds.
   * 
   * @return a service identifier, or <code>null</code> if the service to
   *         which this instance corresponds is an "anonymous" service (it has
   *         no ID).
   */
  public String getServiceID() {
    return _id;
  }

  /**
   * Sets this instance's service.
   * <p>
   * <b>WARNING </b>: in general, this method should not be called by client
   * applications.
   * 
   * @param obj
   *           a service instance.
   * @see Service
   */
  public void setService(Object obj) {
    _service = obj;
  }

  /**
   * Returns this instance's associated service.
   * 
   * @return a service.
   */
  public Object getService() {
    return _service;
  }
  
  public Class getServiceClass() {
    return getLifeCycleManager().getServiceClass(_service);
  }

  /**
   * @return the <code>Collection</code> of <code>Attribute</code> of this instance.
   */
  public Collection getAttributes(){
    return _attributes.values();
  }
  
  /**
   * @param name the name of the desired attribute.
   * @return the <code>Attribute</code> corresponding to the given name, or 
   * <code>Null</code> if no such attribute exists.
   */
  public Attribute getAttribute(String name){
    return (Attribute)_attributes.get(name);
  }

  /**
   * @return the <code>List</code> of <code>Layers</code> that this instance
   *         holds, or <code>null</code> if this instance has no layers
   *         specified.
   */
  public List getLayers() {
    if(_layers == null) return null;
    else{
      return Collections.unmodifiableList(_layers);
    }
  }
  
  /**
   * @param attr an <code>Attribute</code>.
   */
  public void addAttribute(Attribute attr){
    if(attr.getName() == null){
      throw new IllegalArgumentException("Attribute name not specified");
    }
    _attributes.put(attr.getName(), attr);
  }
  
  /**
   * @return <code>true</code> if the service corresponding
   * to this instance has had its <code>init()</code> method called.
   */  
  public boolean isInit(){
    return _status >= STATUS_INIT;
  }
  
  /**
   * @return <code>true</code> if the service corresponding
   * to this instance has had its <code>start()</code> method called.
   */
  public boolean isStarted(){
    return _status == STATUS_STARTED;
  }

  /**
   * Calls the <code>init()</code> method of this instance's service.
   * 
   * @throws Exception if a problem occurs performing this instance's
   * service initialization.
   */
  public void init() throws Exception {
    if(_status != STATUS_NONE){
      throw new IllegalStateException("Service already initialized");
    }
    
    LifeCycleManager lcm = getLifeCycleManager();

    lcm.initService(this);

    if(_layers == null){
      _layers = new ArrayList(0);
    }
    for(int i = 0; i < _layers.size(); i++) {
      ((Layer) _layers.get(i)).init(this);
    }
    _status = STATUS_INIT;
  }
  
  public void invokeInitMethod() throws Exception{
    if(_init != null){
      try{
        _init.invoke(_service);
      }catch(InvocationTargetException e){
        Throwable te = e.getTargetException();
        te.fillInStackTrace();
        throw new ConfigurationException("Could not initialize service", te);
      }catch(Exception e){
        throw new ConfigurationException("Could not initialize service: " + _service.getClass().getName(), e);
      }    
    }
  }
  
  public void invokeStartMethod() throws Exception{
    if(_start != null){
      try{
        _start.invoke(_service);
      }catch(InvocationTargetException e){
        Throwable te = e.getTargetException();
        te.fillInStackTrace();
        throw new ConfigurationException("Could not start service", te);
      }catch(Exception e){
        throw new ConfigurationException("Could not start service: " + _service.getClass().getName(), e);
      }
    }
  }
  
  public void invokeDisposeMethod(){
    if(_dispose != null){
      try{
        _dispose.invoke(_service);
      }catch(InvocationTargetException e){
        throw new CompositeRuntimeException("Could not dispose of service: " + _service.getClass().getName(), e.getTargetException());
      }catch(Exception e){
        throw new CompositeRuntimeException("Could not dispose of service: " + _service.getClass().getName(), e);
      }
    }      
  }   
  
  void start() throws Exception {
    if(_status != STATUS_INIT){
      throw new IllegalStateException("Service not initialized; cannot be started");
    }
    _attributes = Collections.unmodifiableMap(_attributes);
    
    getLifeCycleManager().startService(this);

    for(int i = 0; i < _layers.size(); i++) {
      ((Layer) _layers.get(i)).start(this);
    }
    _status = STATUS_STARTED;    
  }

  void dispose() {
    if(_status >= STATUS_INIT){
      for(int i = 0; i < _layers.size(); i++) {
        ((Layer) _layers.get(i)).dispose();
      }
      try{
        getLifeCycleManager().disposeService(this);
      }catch(Exception e){
        e.printStackTrace();
      }
    }
  }
  
  public void setInitMethod(MethodConfig init){
    _init = init;
  }
  
  public void setStartMethod(MethodConfig start){
    _start = start;
  }  
  
  public void setDisposeMethod(MethodConfig dispose){
    _dispose = dispose;
  }
}
