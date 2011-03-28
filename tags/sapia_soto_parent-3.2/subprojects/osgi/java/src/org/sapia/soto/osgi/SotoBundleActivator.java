package org.sapia.soto.osgi;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.sapia.soto.Service;
import org.sapia.soto.ServiceMetaData;
import org.sapia.soto.SotoContainer;

/**
 * This class implements an OSGI <code>BundleActivator</code> that loads a 
 * <code>SotoContainer</code>; the services within the container can thereafter
 * be registered as OSGI services.
 * <p>
 * To make OSGI bundles with this class, you need an accompanying manifest that
 * respects the OSGI's specification. Your manifest should hold entries such as:
 * <pre>
 * Bundle-Activator: org.sapia.soto.osgi.SotoBundleActivator
 * Export-Package: org.myorganization.myapp
 * Bundle-Name: MyFirstBundle
 * Bundle-Description: A First Bundle
 * Bundle-Vendor: Sapia OSS
 * Bundle-Version: 1.0.0
 * Soto-Configuration: someResource.xml
 * </pre>
 * <p>
 * Note that the last entry in the above example is of course not specified
 * by OSGI. It is rather looked up and used by an instance of this class
 * to retrieve the proper Soto configuration file. An instance of this
 * class internally keeps a <code>SotoContainer</code> that it initializes
 * with the provided configuration. Note that the configuration 
 * is expected to be present as a classpath resource. If the <code>Soto-Configuration</code>
 * header is not present, then the <code>soto.xml</code> resource is searched (although this last
 * method should be avoided).
 * <p>
 * One can publish Soto services as OSGI services through the OSGI layer (see Soto's
 * documentation for more on the "layer" concept). Publishing a Soto service as an OSGI
 * services involves specifying the <code>osgi:publish</code> element (which corresponds
 * to the OSGI layer) has part of the service's configuration, as illustrated below:
 * <pre>
 * <![CDATA[
 * ...
 * <soto:service id="someSotoService>
 *   <myorg:myService>
 *     ...
 *   </myorg:myService>
 *   <osgi:publish>
 *     <interface>org.myorganization.myapp.MyService</interface>
 *   </osgi:publish>
 * </soto:service>
 * ...
 * ]]>
 * </pre>
 * <p>
 * Note that the <code>osgi:publish</code> element must provide <b>1 to many</b> interfaces
 * that are published by the service implemementation.
 * <p>
 * In addition, all services can acquire a reference on the OSGI runtime in the form of an
 * <code>OSGICallback</code> implementation. In application code, the OSGICallback can be
 * looked up through the Soto environment. Here's a service implementation that looks up
 * the OSGICallback:
 * 
 * <pre>
 * package org.myorganization.myapp;
 * import org.sapia.soto.Env;
 * import org.sapia.soto.osgi.OSGICallback;
 * 
 * public class MyService 
 *   implements 
 *     org.sapia.soto.Service, 
 *     org.sapia.soto.EnvAware, ...{
 *     
 *  private Env _env;
 *  private BundleContext _context;
 * 
 *  ...
 *  
 *  public void setEnv(Env env){
 *    _env = env;
 *  }
 *  
 *  public void init(){
 *    _context = ((OSGICallback)_env
 *               .lookup(OSGICallBack.class))
 *               .getBundleContext();
 *  }
 *  
 *  ...
 * 
 * }
 * </pre>
 * <p>
 * One common use of the BundleContext is to register for notifications about other services that
 * are published. This allows given services to use other services to fulfill their task. It also 
 * suggests a programming model analoguous to Jini.
 * <p>
 * Note that all headers of the manifest corresponding to the bundle, as well as all properties of 
 * the bundle itself, are passed to the Soto container that is internally created. The values of these 
 * headers and properties can thus be recuperated in the Soto configuration file(s) of your application, 
 * using the <code>${var_name}</code> notation. 
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2005 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class SotoBundleActivator implements BundleActivator, OSGICallback, Service{

  public static final String DEFAULT_CONTAINER_CONFIG = "soto.xml";
  public static final String SOTO_MANIFEST_ENTRY = "Soto-Configuration";
  public static final String OSGI_PUBLISH_ATTRIBUTE = "osgi:publish";
  
  private SotoContainer _container;
  private BundleContext _context;
 
  /**
   * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
   */
  public void start(BundleContext context) throws Exception {
    _container = new SotoContainer();
    ServiceMetaData meta = new ServiceMetaData(_container, getClass().getName(), this);
    _container.bind(meta);
    _context = context;
    String configPath = (String)context.getBundle().getHeaders().get(SOTO_MANIFEST_ENTRY);
    Dictionary dict = context.getBundle().getHeaders();
    Map vars = new BundleMap(_context);
    Enumeration keys = dict.keys();
    while(keys.hasMoreElements()){
      Object key = keys.nextElement();
      vars.put(key, dict.get(key));
    }
    if(configPath == null){
      loadContainer(DEFAULT_CONTAINER_CONFIG, vars);
    }
    else{
      loadContainer(configPath, vars);
    }
    _container.start();
  }
  
  /**
   * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
   */
  public void stop(BundleContext context) throws Exception {
    if(_container != null){
      _container.dispose();
      _container = null;
    }
  }
  
  /**
   * @see org.sapia.soto.osgi.OSGICallback#getBundleContext()
   */
  public BundleContext getBundleContext() {
    return _context;
  }
  
  /**
   * @see org.sapia.soto.Service#init()
   */
  public void init() throws Exception {}
  
  /**
   * @see org.sapia.soto.Service#start()
   */
  public void start() throws Exception {}
  
  /**
   * @see org.sapia.soto.Service#dispose()
   */
  public void dispose() {}

  private void loadContainer(String resource, Map vars) throws Exception{
    _container.load(resource, vars);
  }
  
  public static class BundleMap extends HashMap{
    private BundleContext _context;
    BundleMap(BundleContext context){
      _context = context;
    }
    
    /**
     * @see java.util.HashMap#get(java.lang.Object)
     */
    public Object get(Object key) {
      Object prop = _context.getProperty(key.toString());
      if(prop == null)
        return super.get(key);
      return prop;
    }
  }
   
}
