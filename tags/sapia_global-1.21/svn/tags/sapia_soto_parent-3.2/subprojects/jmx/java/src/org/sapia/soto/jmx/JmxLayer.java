package org.sapia.soto.jmx;

import java.lang.ref.SoftReference;
import java.util.Hashtable;
import java.util.Iterator;

import javax.management.DynamicMBean;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.sapia.soto.Attribute;
import org.sapia.soto.Layer;
import org.sapia.soto.ServiceMetaData;
import org.sapia.soto.jmx.config.Attributes;
import org.sapia.soto.jmx.config.Operations;

/**
 * @author Yanick Duchesne 27-Aug-2003
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
public class JmxLayer implements Layer {
  private Attributes         _attributes    = new Attributes();
  private Operations         _operations    = new Operations();
  private DynamicMBean       _mbean;
  private ObjectName         _name;
  private String             _domain;
  private String             _serviceName;
  private String             _description;
  private SoftReference      _serverRef;

  /**
   * Constructor for JmxLayer.
   */
  public JmxLayer() {
    super();
  }

  public Operations createOperations() {
    return _operations;
  }

  public Attributes createAttributes() {
    return _attributes;
  }

  public void setDomain(String domain) {
    _domain = domain;
  }

  public void setDescription(String desc) {
    _description = desc;
  }

  public void setServiceName(String name) {
    _serviceName = name;
  }

  /**
   * @see org.sapia.soto.Layer#init(ServiceMetaData)
   */
  public void init(ServiceMetaData meta) throws Exception {
    MBeanDescriptor mbeanDesc = MBeanDescriptor.newInstanceFor(meta
        .getService());

    _attributes.init(mbeanDesc);
    _operations.init(mbeanDesc);

    mbeanDesc.setDescription(_description);
    mbeanDesc.init();

    _mbean = new DynamicMBeanAdaptor(mbeanDesc);

    if(_serviceName == null) {
      if(meta.getServiceID() == null) {
        // assigning a "default" service name.
        _serviceName = getClass().getName().replace('.', '/') + "_"
            + System.currentTimeMillis();
      } else {
        // using service ID...
        _serviceName = meta.getServiceID();
      }
    }
    
    MBeanServer server = MBeanServerHelper.findFor(_domain);
    System.out.println(server);
    _serverRef = new SoftReference(server);
    Hashtable props = new Hashtable();
    props.put("soto-service", _serviceName);
    
    Iterator attributes = meta.getAttributes().iterator();
    while(attributes.hasNext()){
      Attribute attribute = (Attribute)attributes.next();
      props.put(attribute.getName().replace(':', '-'),  attribute.getValue());
    }
    
    _name = new ObjectName(_domain == null ? server.getDefaultDomain() : _domain, props);

    server.registerMBean(_mbean, _name);
    
    // allow GC...
    _attributes = null;
    _operations = null;
    _mbean = null;
    _name = null;
  }

  /**
   * @see org.sapia.soto.Layer#start(org.sapia.soto.ServiceMetaData)
   */
  public void start(ServiceMetaData meta) throws Exception {
  }

  public void dispose() {
    MBeanServer server;
    if(_serverRef != null && (server = (MBeanServer)_serverRef.get()) != null){
      try{
        server.unregisterMBean(_name);
      }catch(Exception e){
        //noop
      }
    }
  }
}
