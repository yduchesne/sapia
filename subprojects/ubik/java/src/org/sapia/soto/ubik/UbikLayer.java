package org.sapia.soto.ubik;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.Layer;
import org.sapia.soto.ServiceMetaData;
import org.sapia.soto.util.Param;
import org.sapia.ubik.rmi.server.Hub;
;

/**
 * This layer binds a service to a remote Ubik JNDI server, under a name that is
 * given through configuration. If no name is specified, the service's
 * identifier is used - implying that the service was indeed assigned an
 * identifier.
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
public class UbikLayer implements Layer, EnvAware{
  private NamingService _svc;
  private Env           _env;
  private String        _name;
  private List          _properties = new ArrayList();

  /**
   * Constructor for UbikLayer.
   */
  public UbikLayer() {
    super();
  }
  
  public void setEnv(Env env){
    _env = env;
  }
  
  /**
   * Sets the naming service instance that will be used to internally bind the
   * service passed to this layer.
   * 
   * @param svc
   *          a <code>NamingService</code>.
   */
  public void setNamingService(NamingService svc) {
    _svc = svc;
  }

  /**
   * @param name
   *          the name under which to bind the service that will be passed to
   *          this instance.
   */
  public void setJndiName(String name) {
    _name = name;
  }

  /**
   * @see org.sapia.soto.Layer#init(ServiceMetaData)
   */
  public void init(ServiceMetaData meta) throws Exception {
    if(_svc == null) {
      _svc = (NamingService)_env.lookup(NamingService.class);
    }
    
    Object toBind = meta.getService();
    
    if(_properties.size() > 0){
      Properties props = new Properties();
      for(int i = 0; i < _properties.size(); i++){
        Param p = (Param)_properties.get(i);
        if(p.getName() != null && 
           p.getValue() != null){
          props.setProperty(p.getName(), p.getValue().toString());
        }
      }
      toBind = Hub.exportObject(toBind, props);
    }
    
    if(_name == null) {
      if(meta.getServiceID() == null) {
        throw new org.sapia.soto.ConfigurationException(
            "No 'jndiName' specified under which to bind the service: "
                + meta.getService().getClass().getName());
      }
      _svc.bind(meta.getServiceID(), toBind);
    } else {
      _svc.bind(_name, toBind);
    }
  }

  /**
   * @see org.sapia.soto.Layer#start(org.sapia.soto.ServiceMetaData)
   */
  public void start(ServiceMetaData meta) throws Exception {
  }

  /**
   * @see org.sapia.soto.Layer#dispose()
   */
  public void dispose() {
  }
}
