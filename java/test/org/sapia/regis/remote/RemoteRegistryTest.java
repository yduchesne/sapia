package org.sapia.regis.remote;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import junit.framework.TestCase;

import org.sapia.regis.Path;
import org.sapia.regis.RegisSession;
import org.sapia.regis.Registry;
import org.sapia.regis.RegistryContext;
import org.sapia.ubik.rmi.Consts;
import org.sapia.ubik.rmi.server.Hub;
import org.sapia.ubik.util.Localhost;

public class RemoteRegistryTest extends TestCase{

  public RemoteRegistryTest(String arg0) {
    super(arg0);
  }
  
  public void testServer() throws Exception{
    System.setProperty(Consts.IP_PATTERN_KEY, "localhost");    
    RegistryServer.startThread = false;
    RegistryServer.main(new String[]{"etc/server.properties"});
    
    Thread.sleep(2000);
    
    Properties props = new Properties();
    props.setProperty("registry.address", Localhost.getLocalAddress().getHostAddress());
    props.setProperty(RegistryContext.BOOTSTRAP, "etc/client.properties");
    RegistryContext ctx = new RegistryContext(props);
    Registry reg = ctx.connect();
    RegisSession s = reg.open();
    assertEquals("jsmith", reg.getRoot().getChild(Path.parse("databases/000")).getProperty("username").asString());
    Map params = new HashMap();
    params.put("param1", "value1");
    reg.getRoot().getChild(Path.parse("databases/000")).getProperties(params);
    s.close();
    Hub.shutdown(3000);
  }

  
}
