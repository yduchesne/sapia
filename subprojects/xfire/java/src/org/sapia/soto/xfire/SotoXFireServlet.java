package org.sapia.soto.xfire;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.transport.TransportManager;
import org.codehaus.xfire.transport.http.XFireServlet;
import org.sapia.resource.Resource;
import org.sapia.soto.ServiceMetaData;
import org.sapia.soto.SotoContainer;

/**
 * An instance of this servlet loads a Soto configuration file into a <code>SotoContainer</code>
 * that it keeps internally.
 * <p>
 * This servlet takes the following intialization parameter: <b>soto-config-resource</b>. The
 * parameter consists of an URI corresponding to the location of the Soto configuration to load.
 * A non-absolute URI will be evaluated relatively to the <code>WEB-INF</code> directory, whereas
 * an absolute URI will be resolved according to Soto's normal resource resolving scheme. Here
 * are some valid URIs:
 * <ul>
 *   <li><code>resource:/org/acme/web/services/myapp.xml</code> (searched in the classpath)
 *   <li><code>soto/myapp.xml</code> (searched under <code>WEB-INF</code>)
 * </u>
 * 
 * <p>
 * Instances of the {@link org.sapia.soto.xfire.XFireLayer} classes that are loaded into the 
 * internal Soto container indirectly interact with the servlet. The layers in fact register their
 * corresponding Soto service with the servlet as XFire web service objects.
 * 
 * @author yduchesne
 *
 */
public class SotoXFireServlet extends XFireServlet implements WebServiceContext{
  
  SotoContainer _container;
  
  public static final String SOTO_CONFIG_RESOURCE = "soto-config-resource";
  
  public void init(ServletConfig conf) throws ServletException {
    super.init(conf);
    _container = new SotoContainer();
    ServiceMetaData meta = new ServiceMetaData(_container, null, this);
    try{
      _container.bind(meta);
    }catch(Exception e){
      throw new ServletException("Could not initialize", e);
    }
    _container.getResourceHandlers().prepend(new ServletResourceHandler(conf.getServletContext()));
    Map<String, String> params = new HashMap<String, String>();
    Enumeration names = conf.getInitParameterNames();
    while(names.hasMoreElements()){
      String name = (String)names.nextElement();
      params.put(name, conf.getInitParameter(name));
    }
    
    String resource = conf.getInitParameter(SOTO_CONFIG_RESOURCE);
    if(resource == null){
      throw new IllegalStateException(SOTO_CONFIG_RESOURCE + " parameter not set");
    }
    try{
      Resource res = _container.getResourceHandlerFor(resource).getResourceObject(resource);
      _container.load(res.getURI(), params);
      _container.start();
    }catch(Exception e){
      e.printStackTrace(System.out);
      throw new ServletException("Could not initialize servlet", e);
    }
  }
  
  public void destroy() {
    super.destroy();
    _container.dispose();
  }

  public TransportManager getTransportManager() throws Exception{
    return getXFire().getTransportManager();
  }
  
  public void register(Service service) throws Exception{
    getController().getServiceRegistry().register(service);
  }  
  
  protected SotoContainer getContainer(){
    return this._container;
  }
  
}
