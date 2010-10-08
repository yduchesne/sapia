package org.sapia.regis;

import java.util.Properties;

import org.sapia.regis.local.LocalRegistry;
import org.sapia.regis.local.LocalRegistryFactory;

import junit.framework.TestCase;

public class RegistryContextTest extends TestCase {

  public RegistryContextTest(String arg0) {
    super(arg0);
  }
  
  public void testConnect() throws Exception{
    Properties props = new Properties();
    props.setProperty(RegistryContext.FACTORY_CLASS, LocalRegistryFactory.class.getName());
    super.assertTrue(new RegistryContext(props).connect() instanceof LocalRegistry);
  }
  
  public void testBootstrapPrecedence() throws Exception{
    Properties props = new Properties();
    props.setProperty(RegistryContext.BOOTSTRAP, "${user.dir}/etc/bootstrap1.properties, file:etc/bootstrap2.properties");
    Node node = new RegistryContext(props).connect().getRoot().getChild(Path.parse("databases/000"));
    super.assertEquals("jsmith", node.getProperty("username").asString());    
  }
  
  public void testBootstrapFallback() throws Exception{
    Properties props = new Properties();
    props.setProperty(RegistryContext.BOOTSTRAP, "etc/foo.properties, ${user.dir}/etc/bootstrap2.properties");
    Node node = new RegistryContext(props).connect().getRoot().getChild(Path.parse("databases/001"));
    super.assertEquals("stiger", node.getProperty("username").asString());    
  }  

  
}
