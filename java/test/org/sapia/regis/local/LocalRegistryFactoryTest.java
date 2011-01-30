package org.sapia.regis.local;

import java.util.Properties;

import org.sapia.regis.RegisSession;
import org.sapia.regis.Registry;

import junit.framework.TestCase;

public class LocalRegistryFactoryTest extends TestCase {

  public LocalRegistryFactoryTest(String arg0) {
    super(arg0);
  }

  public void testConnect() throws Exception{
    Properties props = new Properties();
    props.setProperty(LocalRegistryFactory.BOOTSTRAP, 
        "org/sapia/regis/local/configTest.xml");
    LocalRegistryFactory fac = new LocalRegistryFactory();
    Registry reg = fac.connect(props);
    RegisSession s = reg.open();
    assertEquals("value", reg.getRoot().getChild("test").getProperty("prop").asString());
    s.close();
    
  }

  public void testConnectWithVars() throws Exception{
    Properties props = new Properties();
    props.setProperty(LocalRegistryFactory.BOOTSTRAP, "${user.dir}/etc/bootstrap1.xml");
    LocalRegistryFactory fac = new LocalRegistryFactory();
    fac.connect(props);
  }
  
}
