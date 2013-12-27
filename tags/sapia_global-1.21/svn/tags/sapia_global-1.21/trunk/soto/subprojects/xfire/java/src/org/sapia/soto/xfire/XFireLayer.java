package org.sapia.soto.xfire;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;

import org.codehaus.xfire.aegis.AegisBindingProvider;
import org.codehaus.xfire.annotations.AnnotationServiceFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.codehaus.xfire.service.invoker.BeanInvoker;
import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.Layer;
import org.sapia.soto.ServiceMetaData;
import org.sapia.soto.util.Param;
import org.sapia.util.xml.confix.ObjectWrapperIF;

/**
 * An instance of this wrap an XFire {@link org.codehaus.xfire.service.binding.ObjectServiceFactory}
 * instance (allowing invocation of its methods through IOC).
 * <p>
 * An instance of this layer is responsible for registering its corresponding Soto service
 * as a Web service.
 * <p>
 * To that end, it indirectly interacts with a {@link org.sapia.soto.xfire.SotoXFireServlet}
 * instance.
 * <p>
 * This layer in addition detects the use of JSR 181 annotations on the classes of exported services.
 * In these cases, XFire's support for JSR 181 is used (the <code>ObjectServiceFactory</code> instance
 * that is used is internally switched to an {@link AnnotationServiceFactory}).
 * 
 * @author yduchesne
 *
 */
public class XFireLayer implements Layer, EnvAware, ObjectWrapperIF{
  
  private Class _interface;
  private String _name, _namespace;
  private List<Param> _properties = new ArrayList<Param>(3);
  private Env _env;
  private ObjectServiceFactory _factory = new ObjectServiceFactory();
  
  ///// Instance methods
  
  /**
   * @param intf the interface for which a web service will be created (by default, 
   * all methods of that interface will be published and made available for dynamic
   * invocations through SOAP).
   */
  public void setInterface(Class intf){
    _interface = intf;
  }
  
  /**
   * @see #setInterface(Class)
   */
  public void setInterface(String intfName) throws Exception{
    _interface = Class.forName(intfName);
  }  
  
  /**
   * @param name sets the name of the web service that will be exported (appended at the
   * end of the web service's URI). If <code>null</code>, the non-qualified name of the published 
   * interface will be used.
   * 
   * @see #setInterface(Class)
   */
  public void setName(String name){
    _name = name;
  }

  /**
   * @param name sets the namespace of the web service that will be exported. If <code>null</code>, 
   * the package name of the published interface will be used.
   * 
   * @see ObjectServiceFactory#addIgnoredMethods(java.lang.String)
   */  
  public void setNameSpace(String namespace){
    _namespace = namespace;
  }  
  
  /**
   * Allows injecting properties to the internal {@link Service} instance that is created.
   * @see Service
   * @return
   */
  public Param createProperty(){
    Param p = new Param();
    _properties.add(p);
    return p;
  }
  
  /**
   * Sets the <code>ObjectServiceFactory</code> to use.
   * 
   * @param factory an <code>ObjectServiceFactory</code>.
   */
  public void setServiceFactory(ObjectServiceFactory factory){
    _factory = factory;
  }
  
  ///// ObjectWrapperIF interface method
  
  public Object getWrappedObject() {
    return _factory;
  }
  
  ///// EnvAware interface method
  
  public void setEnv(Env env) {
    _env = env;
  }
  
  ///// Layer interface methods
  
  public void init(ServiceMetaData meta) throws Exception {
    WebService ws = (WebService)meta.getService().getClass().getAnnotation(WebService.class);
    
    if(_factory.getBindingProvider() == null){
      _factory.setBindingProvider(new AegisBindingProvider());
    }
    WebServiceContext ctx = (WebServiceContext)_env.lookup(WebServiceContext.class);
    _factory.setTransportManager(ctx.getTransportManager());
    Service service = null;
    if(ws == null){
      if(_interface == null){
        throw new IllegalStateException("Web service interface not set");
      }
      service =  createWebService(meta.getService());
    }
    else{
      if(!(_factory instanceof AnnotationServiceFactory)){
        AnnotationServiceFactory fac = new AnnotationServiceFactory();
        replace(fac);
      }
      service =  createJsr181WebService(meta.getService(), ws);
    }
    ctx.register(service);
  }
  
  public void start(ServiceMetaData meta) throws Exception {
  }
  
  public void dispose() {
  }
  
  ///// Restricted methods
  
  private Service createWebService(Object instance) throws Exception{
    Service service = _factory.create(_interface, _name != null ? _name : _interface.getSimpleName(), _namespace, null);
    setServiceState(service, instance);    
    return service;
  }
  
  private Service createJsr181WebService(Object instance, WebService ws) throws Exception{
    Service service = _factory.create(instance.getClass());
    setServiceState(service, instance);
    return service;
  }
  
  private void replace(ObjectServiceFactory newFactory){
    newFactory.setTransportManager(_factory.getTransportManager());
    _factory = newFactory;
  }
  
  private void setServiceState(Service service, Object instance){
    service.setInvoker(new BeanInvoker(instance));
    for(int i = 0; i < _properties.size(); i++){
      Param p = _properties.get(i);
      if(p.getName() != null && p.getValue() != null){
        service.setProperty(p.getName(), p.getValue());
      }
    }
  }
}
