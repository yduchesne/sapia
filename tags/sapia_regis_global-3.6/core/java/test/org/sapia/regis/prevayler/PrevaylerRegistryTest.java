package org.sapia.regis.prevayler;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.sapia.regis.Node;
import org.sapia.regis.Path;
import org.sapia.regis.RegisSession;
import org.sapia.regis.Registry;
import org.sapia.regis.RegistryContext;
import org.sapia.regis.remote.RegistryServer;
import org.sapia.regis.util.Utils;
import org.sapia.ubik.rmi.Consts;
import org.sapia.ubik.rmi.server.Hub;
import org.sapia.ubik.util.Localhost;

import junit.framework.TestCase;

public class PrevaylerRegistryTest extends TestCase {
  
  private PrevaylerRegistry reg;

  public PrevaylerRegistryTest(String arg0) {
    super(arg0);
  }

  protected void setUp() throws Exception {
    PrevaylerRegistryFactory fac = new PrevaylerRegistryFactory();
    Properties props = new Properties();
    props.setProperty(PrevaylerRegistryFactory.DELETE_ON_STARTUP, "true");
    reg = (PrevaylerRegistry)fac.connect(props);
  }

  protected void tearDown() throws Exception {
    reg.close();
    Utils.deleteRecurse(new File(PrevaylerRegistryFactory.DEFAULT_BASE_DIR));
  }
  
  public void testLoad() throws Exception{
    reg.load(Path.parse(Node.ROOT_NAME), null, null, 
        Utils.loadAsString(new FileInputStream("etc/configCreateExample.xml")), null);
    reg.close();
    PrevaylerRegistryFactory fac = new PrevaylerRegistryFactory();
    Properties props = new Properties();
    props.setProperty(PrevaylerRegistryFactory.DELETE_ON_STARTUP, "false");
    reg = (PrevaylerRegistry)fac.connect(props);
    String username = reg.getRoot().getChild(Path.parse("databases/000")).getProperty("username").asString();
    assertEquals("jsmith", username);
  }

  public void testPrevaylerServer() throws Exception{
    System.setProperty(Consts.IP_PATTERN_KEY, "localhost");    
    RegistryServer.startThread = false;
    RegistryServer.main(new String[]{"etc/prevaylerServer.properties"});
    
    Thread.sleep(2000);
    
    Properties props = new Properties();
    props.setProperty("registry.address", Localhost.getLocalAddress().getHostAddress());
    props.setProperty(RegistryContext.BOOTSTRAP, "etc/client.properties");
    RegistryContext ctx = new RegistryContext(props);
    Registry regServer = ctx.connect();
    RegisSession s = regServer.open();
    assertEquals("jsmith", regServer.getRoot().getChild(Path.parse("databases/000")).getProperty("username").asString());
    Map params = new HashMap();
    params.put("param1", "value1");
    regServer.getRoot().getChild(Path.parse("databases/000")).getProperties(params);
    s.close();
    Hub.shutdown(3000);
  }  

}
