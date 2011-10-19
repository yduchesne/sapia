package org.sapia.soto.ubik.monitor.impl;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.sapia.corus.interop.Context;
import org.sapia.corus.interop.Param;
import org.sapia.corus.interop.Status;
import org.sapia.corus.interop.api.InteropLink;
import org.sapia.corus.interop.api.StatusRequestListener;
import org.sapia.soto.ConfigurationException;
import org.sapia.soto.Debug;
import org.sapia.soto.Service;
import org.sapia.soto.util.matcher.UriPattern;

/**
 *
 * @author Jean-Cédric Desrochers
 */
public class JmxMonitorService implements Service {

  public static final String JMX_MONITOR_ERROR       = "soto.jmxMonitor.error";
  public static final String JMX_MONITOR_ERROR_CLASS = "soto.jmxMonitor.error.class";  
  public static final String JMX_MONITOR_ERROR_MSG   = "soto.jmxMonitor.error.msg";  
  
  /** Determine if Sun's JMX runtime is available. */
  private static boolean _isJmxCapable;
  static {
    try {
      Class.forName("java.lang.management.ManagementFactory");
      _isJmxCapable = true;
    } catch(Exception e) {
      _isJmxCapable = false;
    }
  }

  /** Determine if the Corus runtime is available. */
  private static boolean _isCorusCapable;  
  static {
    try {
      Class.forName("org.sapia.corus.interop.api.InteropLink");
      _isCorusCapable = true;
    } catch(Exception e) {
      _isCorusCapable = false;
    }
  }
  
  private boolean _appendMBeanInfo = false; 
  
  /** The MBean domain for which to monitor. */ 
  private String _domain;
  
  /** The list pattern to include MBeans. */
  private ArrayList<UriPattern> _includes;
  
  /** The list pattern to exclude MBeans. */
  private ArrayList<UriPattern> _excludes;
  
  /**
   * Creates a new JmxMonitorService instance.
   */
  public JmxMonitorService() {
    _includes = new ArrayList<UriPattern>();
    _excludes = new ArrayList<UriPattern>();
  }
  
  /**
   * Returns the MBean domain to monitor.
   * 
   * @return The MBean domain to monitor.
   */
  public String getDomain() {
    return _domain;
  }
  
  /**
   * Changes the MBean domain to monitor.
   * 
   * @param aDomain The new domain.
   */
  public void setDomain(String aDomain) {
    _domain = aDomain;
  }
  
  public boolean getAppendMbeanInfo() {
    return _appendMBeanInfo;
  }
  
  public void setAppendMbeanInfo(boolean aValue) {
    _appendMBeanInfo = aValue;
  }
  
  public void addInclude(String aPattern) {
    if (aPattern != null) {
      _includes.add(UriPattern.parse(aPattern));
    }
  }
  
  public void addExclude(String aPattern) {
    if (aPattern != null) {
      _excludes.add(UriPattern.parse(aPattern));
    }
  }
  
  public synchronized boolean selectMBean(String aName) {
    boolean isSelected = false;
    
    // First pass with the includes (if specified)
    if (!_includes.isEmpty()) {
      for (Iterator<UriPattern> it = _includes.iterator(); it.hasNext(); ) {
        UriPattern pattern = it.next();
        if (pattern.matches(aName)) {
          isSelected = true;
        }
      }
    } else {
      isSelected = true;
    }
      
    // Second pass with the exlcudes (if specified)
    if (isSelected && !_excludes.isEmpty()) {
      for (Iterator<UriPattern> it = _excludes.iterator(); isSelected && it.hasNext(); ) {
        UriPattern pattern = it.next();
        if (pattern.matches(aName)) {
          isSelected = false;
        }
      }
    }

    return isSelected;
  }
  
  /* (non-Javadoc)
   * @see org.sapia.soto.Service#init()
   */
  public void init() throws Exception {
    if (_domain == null) {
      throw new ConfigurationException("The MBean domain to monitor is not set");
    }
    
    if (_isJmxCapable && _isCorusCapable) {
      Debug.debug(JmxMonitorService.class, "MBean server and Corus runtime found, starting JMX monitoring of domain " + _domain);
      StatusListener listener = new StatusListener(this);
      InteropLink.getImpl().addStatusRequestListener(listener);
    } else {
      Debug.debug(JmxMonitorService.class, "Required runtimes are not found for JMX monitoring of domain " + _domain +
              " [isJmxCapable=" + _isJmxCapable + " isCorusCapable=" + _isCorusCapable + "]");
    }
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.Service#start()
   */
  public void start() throws Exception {
  }

  /* (non-Javadoc)
   * @see org.sapia.soto.Service#dispose()
   */
  public void dispose() {
  }
  
  /**
   * Inner class that implements the Corus status request listener. 
   *
   * @author Jean-Cédric Desrochers
   */
  public static class StatusListener implements StatusRequestListener {

    /** The parent JMX monitor service of this listener. */
    private JmxMonitorService _parent;
    
    /**
     * Creates a new StatusListener instance.
     * 
     * @param aParent The parent service.
     */
    public StatusListener(JmxMonitorService aParent) {
      _parent = aParent;
    }
    
    /* (non-Javadoc)
     * @see org.sapia.corus.interop.api.StatusRequestListener#onStatus(org.sapia.corus.interop.Status)
     */
    public void onStatus(Status aStatus) {
      Context context = null;
      try {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        List<ObjectName> mbeanNames = getMbeansForDomain(server, _parent.getDomain());
        
        for (Iterator<ObjectName> it = mbeanNames.iterator(); it.hasNext(); ) {
            ObjectName mbeanName = it.next();
            try {
  
            // Create the context for the mbean name
            context = new Context();
            aStatus.addContext(context);
            context.setName(generatedContextNameFor(mbeanName));
            
            MBeanInfo mbeanInfo = server.getMBeanInfo(mbeanName);
            MBeanAttributeInfo[] mbeanAttributes = mbeanInfo.getAttributes();
            
            if (_parent.getAppendMbeanInfo()) {
              context.addParam(new Param("jmx.mbean.domain", mbeanName.getDomain()));
              context.addParam(new Param("jmx.mbean.properties", mbeanName.getKeyPropertyListString()));
              context.addParam(new Param("jmx.mbean.type", mbeanInfo.getClassName()));
            }
  
            for (int i = 0; i < mbeanAttributes.length; i++) {
              Object value = server.getAttribute(mbeanName, mbeanAttributes[i].getName());
              
              if (value != null && value.getClass().isArray()) {
                Object[] valueArray = (Object[]) value;
                String arrayClassName = valueArray.getClass().getComponentType().getCanonicalName();
                value = arrayClassName + "[" + valueArray.length + "]"; 
              }
  
              context.addParam(new Param(mbeanAttributes[i].getName(), (value == null? "": value.toString())));
            }
            
          } catch (Exception e) {
            if (context == null) {
              context = new Context();
              context.setName("JMX:" + _parent.getDomain() + ": " + mbeanName.getCanonicalKeyPropertyListString());
              aStatus.addContext(context);
            }
            
            context.addParam(new Param(JMX_MONITOR_ERROR, "true"));
            context.addParam(new Param(JMX_MONITOR_ERROR_CLASS, e.getClass().getName()));
            context.addParam(new Param(JMX_MONITOR_ERROR_MSG, e.getLocalizedMessage()));
          }
        }
        
      } catch (Exception e) {
        if (context == null) {
          context = new Context();
          context.setName("JMX:" + _parent.getDomain());
          aStatus.addContext(context);
        }
        
        context.addParam(new Param(JMX_MONITOR_ERROR, "true"));
        context.addParam(new Param(JMX_MONITOR_ERROR_CLASS, e.getClass().getName()));
        context.addParam(new Param(JMX_MONITOR_ERROR_MSG, e.getLocalizedMessage()));
      }
    }
    
    /**
     * Utility method that returns the names of the mbean associated with the domain name provided.
     * 
     * @param aServer The mbean server for which to extract the names.
     * @param aDomainName The target domain name.
     * @return A list of the mbean names sorted in the way they should be presented.
     */
    protected List<ObjectName> getMbeansForDomain(MBeanServer aServer, String aDomainName) {
      Set<ObjectName> names = aServer.queryNames(null, null);
      TreeMap<String, ObjectName> sortedNames = new TreeMap<String, ObjectName>();
      
      // Get the mbean names for the target domain and sort them
      for (Iterator<ObjectName> it = names.iterator(); it.hasNext(); ) {
        ObjectName mbeanName = it.next();
        if (aDomainName.equals(mbeanName.getDomain()) && _parent.selectMBean(mbeanName.getCanonicalName())) {
          sortedNames.put(mbeanName.getKeyPropertyListString(), mbeanName);
        }
      }
      
      // Created the result sorted list
      ArrayList<ObjectName> result = new ArrayList<ObjectName>(sortedNames.size());
      for (Iterator<String> it = sortedNames.keySet().iterator();  it.hasNext(); ) {
        result.add(sortedNames.get(it.next()));
      }
      
      return result;
    }
    
    /**
     * Utility method that generated a context name based on the MBean object name passed in.
     * 
     * @param aMbeanName The MBean name from which to create a name.
     * @return The generated context name.
     */
    protected String generatedContextNameFor(ObjectName aMbeanName) {
      StringBuffer buffer = new StringBuffer();
      buffer.append("JMX:").append(aMbeanName.getDomain()).append(":");

      String keyPropertyList = aMbeanName.getKeyPropertyListString();
      int count = 0;
      for (StringTokenizer tokenizer = new StringTokenizer(keyPropertyList, ",", false); tokenizer.hasMoreTokens(); ) {
        String token = tokenizer.nextToken();
        int index = token.indexOf("=");
        
        if (index > 0) {
          if (count > 0) {
            buffer.append("/");
          }
          buffer.append(token.substring(index+1));
          count++;
        }
      }
      
      return buffer.toString();
    }
     
  }
}
