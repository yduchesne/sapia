package org.sapia.regis.gui;

import java.io.File;
import java.util.Properties;

import org.sapia.regis.RegisLog;
import org.sapia.regis.Registry;
import org.sapia.regis.RegistryContext;
import org.sapia.regis.local.LocalRegistry;
import org.sapia.regis.local.LocalRegistryFactory;
import org.sapia.regis.remote.client.RemoteRegistryFactory;

public class RegistryFactory {

  public static Registry newEmbeddedInstance() throws Exception{
    Properties props = new Properties();
    props.setProperty(RegistryContext.FACTORY_CLASS, LocalRegistryFactory.class.getName());

    RegistryContext ctx = new RegistryContext(props);
    return ctx.connect();
  }
  
  public static Registry newDebugInstance() throws Exception{
    RegisLog.setDebug();
    LocalRegistry local = (LocalRegistry)newEmbeddedInstance();
    local.load(new File("etc/configCreateExample.xml"));
    return local;
  }  
  
  public static Registry newRemoteInstance(String host, int port, String jndiName) throws Exception{
    Properties props = new Properties();
    props.setProperty(RegistryContext.FACTORY_CLASS, RemoteRegistryFactory.class.getName());
    props.setProperty(RemoteRegistryFactory.ADDRESS, host);
    props.setProperty(RemoteRegistryFactory.PORT, ""+port);
    if(jndiName != null && jndiName.trim().length() > 0){
      props.setProperty(RemoteRegistryFactory.JNDI_NAME, ""+jndiName);
    }
    RegistryContext ctx = new RegistryContext(props);
    return ctx.connect();
  }  
}
