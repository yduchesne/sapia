package org.sapia.soto.config;

import java.util.ArrayList;
import java.util.List;

import org.sapia.soto.Attribute;
import org.sapia.soto.Debug;
import org.sapia.soto.Layer;
import org.sapia.soto.Service;
import org.sapia.soto.ServiceMetaData;
import org.sapia.soto.SotoContainer;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;
import org.sapia.util.xml.confix.ObjectHandlerIF;

/**
 * This class implements the <code>soto:service</code> tag.
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
public class ServiceTag implements ObjectHandlerIF, ObjectCreationCallback {
  private Object          _service;
  private ServiceMetaData _meta;
  private String          _id;
  private SotoContainer   _cont;
  private List            _layers     = new ArrayList();
  private List            _attributes = new ArrayList();  
  private MethodConfig       _initMethod, _startMethod, _disposeMethod;
  private String _type;

  /**
   * Constructor for ServiceTag.
   */
  public ServiceTag(SotoContainer cont) {
    _cont = cont;
  }

  public ServiceTag(ServiceMetaData meta) {
    _meta = meta;
  }
  
  public void setType(String type){
    _type = type;
  }

  public Object getService() {
    return _service;
  }

  public void setId(String id) {
    _id = id;
  }

  public String getId() {
    return _id;
  }
  
  public MethodConfig createInitMethod(){
    return _initMethod == null ? _initMethod = new MethodConfig() : _initMethod;
  }
  
  public MethodConfig createStartMethod(){
    return _startMethod == null ? _startMethod = new MethodConfig() : _startMethod;
  }  
  
  public MethodConfig createDisposeMethod(){
    return _disposeMethod == null ? _disposeMethod = new MethodConfig() : _disposeMethod;
  }    

  /**
   * @see org.sapia.util.xml.confix.ObjectCreationCallback#onCreate()
   */
  public Object onCreate() throws ConfigurationException {
    if(_service == null) {
      Debug.debug(getClass(), "Service instance not specified for: " + _id);
      return new NullObjectImpl();
    }

    try {
      _meta = new ServiceMetaData(_cont, _id, _service, _layers);
      _meta.setType(_type);
      _meta.setInitMethod(_initMethod);
      _meta.setStartMethod(_startMethod);
      _meta.setDisposeMethod(_disposeMethod);
      for(int i = 0; i < _attributes.size(); i++){
        Attribute attr = (Attribute)_attributes.get(i);
        if(attr.getName() != null && attr.getValue() != null){
          _meta.addAttribute(attr);
        }
      }
      _meta.init();
      _cont.bind(_meta);
      return _meta.getLifeCycleManager().lookupService(_meta.getServiceID(), _meta.getService());
    } catch(Exception e) {
      throw new ConfigurationException("Could not initialize service: " + _service.getClass().getName() + " (" + e.getMessage() + ")", e);
    }
  }

  public ServiceMetaData getServiceMetaData() {
    return _meta;
  }
  
  public Attribute createAttribute(){
    Attribute attr = new Attribute();
    _attributes.add(attr);
    return attr;
  }

  /**
   * @see org.sapia.util.xml.confix.ObjectHandlerIF#handleObject(String, Object)
   */
  public void handleObject(String name, Object obj)
      throws ConfigurationException {
    if(obj == null){
      Debug.debug(getClass(), "Got null for " + name);
    }
    if(obj instanceof Service) {
      if(_service != null) {
        throw new ConfigurationException("Service already defined");
      }

      _service = (Service) obj;
    } else if(obj instanceof Layer) {
      if(_service == null) {
        throw new ConfigurationException(
            "Service instance must be declared before layer");
      }

      _layers.add(obj);
    } else {
      if(obj.getClass().isPrimitive()){
        throw new ConfigurationException(
           "Misconfiguration: element " + name + " evaluates to a primitive; " +
           " should be a service or layer instance");
      }
      else if(obj instanceof String){
        throw new ConfigurationException(
           "Misconfiguration: element " + name + " evaluates to a string; " +
           " should be a service or layer instance");        
      }
      _service = obj;
    }
  }
}
