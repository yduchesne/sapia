package org.sapia.soto.jmx.mx4j;

import java.util.Properties;

import javax.management.MBeanServer;

/**
 * @author Yanick Duchesne 17-Nov-2003
 */
public interface JRMPAdaptorExMBean {
  /**
   * @see mx4j.adaptor.rmi.jrmp.JRMPAdaptorMBean#getPort()
   */
  public int getPort();

  /**
   * @see mx4j.adaptor.rmi.RMIAdaptorMBean#getProtocol()
   */
  public String getProtocol();

  /**
   * @see mx4j.adaptor.rmi.jrmp.JRMPAdaptorMBean#getSSLFactory()
   */
  public String getSSLFactory();

  /**
   * @see mx4j.adaptor.rmi.jrmp.JRMPAdaptorMBean#setPort(int)
   */
  public void setPort(int arg0);

  /**
   * @see mx4j.adaptor.rmi.jrmp.JRMPAdaptorMBean#setSSLFactory(String)
   */
  public void setSSLFactory(String arg0);

  /**
   * @see mx4j.adaptor.rmi.RMIAdaptorMBean#clearJNDIProperties()
   */
  public void clearJNDIProperties();

  /**
   * @see mx4j.adaptor.rmi.RMIAdaptorMBean#clearNamingProperties()
   */
  public void clearNamingProperties();

  /**
   * @see mx4j.adaptor.rmi.RMIAdaptorMBean#getHostAddress()
   */
  public String getHostAddress();

  /**
   * @see mx4j.adaptor.rmi.RMIAdaptorMBean#getHostName()
   */
  public String getHostName();

  /**
   * @see mx4j.adaptor.rmi.RMIAdaptorMBean#getJNDIName()
   */
  public String getJNDIName();

  /**
   * @see mx4j.adaptor.rmi.RMIAdaptorMBean#getJNDIProperties()
   */
  public Properties getJNDIProperties();

  /**
   * @see mx4j.adaptor.rmi.RMIAdaptorMBean#getNamingProperties()
   */
  public Properties getNamingProperties();

  /**
   * @see mx4j.adaptor.rmi.RMIAdaptorMBean#putJNDIProperty(Object, Object)
   */
  public void putJNDIProperty(Object arg0, Object arg1);

  /**
   * @see mx4j.adaptor.rmi.RMIAdaptorMBean#putNamingProperty(Object, Object)
   */
  public void putNamingProperty(Object arg0, Object arg1);

  /**
   * @see mx4j.adaptor.rmi.RMIAdaptorMBean#setJNDIName(String)
   */
  public void setJNDIName(String arg0);

  /**
   * @see mx4j.adaptor.rmi.RMIAdaptorMBean#start()
   */
  public void start() throws Exception;

  /**
   * @see mx4j.adaptor.rmi.RMIAdaptorMBean#stop()
   */
  public void stop() throws Exception;

  public void setMBeanServer(MBeanServer server);
}
