/*
 * MbeanServerHelper.java
 *
 * Created on April 12, 2005, 9:13 AM
 */

package org.sapia.soto.jmx;

import java.util.List;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;

/**
 *
 * @author yduchesne
 */
public class MBeanServerHelper {
  
  public static final String DEFAULT_DOMAIN = "DefaultDomain";
  
  /** Creates a new instance of MbeanServerHelper */
  public MBeanServerHelper() {
  }
  
  public static synchronized MBeanServer findFor(String domain){

    List servers = MBeanServerFactory.findMBeanServer(null);
    for(int i = 0; i < servers.size(); i++){
      MBeanServer server = (MBeanServer)servers.get(i);
      return server;
    }
    if(domain == null){
      return MBeanServerFactory.createMBeanServer();
    }
    else{
      return MBeanServerFactory.createMBeanServer(domain);      
    }
  }
}
