package org.sapia.soto.osgi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.osgi.framework.ServiceRegistration;
import org.sapia.soto.Attribute;
import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.Layer;
import org.sapia.soto.ServiceMetaData;

/**
 * This layer publishes its corresponding Soto service as an OSGI service. The
 * OSGI layer must solely be used in the context of a <code>SotoBundleActivator</code>.
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2005 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class OSGILayer implements Layer, EnvAware{
  
  private Env _env;
  
  private List _interfaces = new ArrayList();
  private OSGICallback _callback;
  private ServiceRegistration _reg;
  
  /**
   * @param name adds the name of an interface under which
   * this instance should be published. 
   */
  public void addInterface(String name){
    _interfaces.add(name);
  }
  
  /**
   * @see org.sapia.soto.EnvAware#setEnv(org.sapia.soto.Env)
   */
  public void setEnv(Env env) {
    _env = env;
  }
  
  /**
   * @see org.sapia.soto.Layer#init(org.sapia.soto.ServiceMetaData)
   */
  public void init(ServiceMetaData meta) throws Exception {
    _callback = (OSGICallback)_env.lookup(OSGICallback.class);
  }
  
  /**
   * @see org.sapia.soto.Layer#start(org.sapia.soto.ServiceMetaData)
   */
  public void start(ServiceMetaData meta) throws Exception {
    if(_interfaces.size() == 0){
      throw new IllegalStateException("No published interface specified as part of OSGI layer");
    }
    String[] interfaceNames = (String[])_interfaces.toArray(new String[_interfaces.size()]);
    _reg = _callback.getBundleContext().registerService(interfaceNames, meta.getService(), toDictionary(meta.getAttributes()));
  }

  private Dictionary toDictionary(Collection attributes){
    Dictionary dict = new Hashtable();
    Iterator itr = attributes.iterator();
    while(itr.hasNext()){
      Attribute attr = (Attribute)itr.next();
      if(attr.getValue() != null){
       dict.put(attr.getName(), attr.getValue()); 
      }
    }
    return dict;
  }
  
  /**
   * @see org.sapia.soto.Layer#dispose()
   */
  public void dispose() {
    if(_reg != null)
      _reg.unregister();
  }

}
