package org.sapia.regis.codegen;

import java.io.File;
import java.util.Properties;

import org.sapia.regis.RegisSession;
import org.sapia.regis.Registry;
import org.sapia.regis.RegistryContext;
import org.sapia.regis.codegen.output.CodeGenConfig;
import org.sapia.regis.codegen.output.NodeIntrospector;
import org.sapia.regis.local.LocalRegistryFactory;

import junit.framework.TestCase;

public class CodeGenTest extends TestCase {

  public CodeGenTest(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    super.setUp();
  }
  
  public void testGenerate() throws Exception{
    Properties props = new Properties();
    /*
    props.setProperty(LocalRegistryFactory.BOOTSTRAP, 
        "etc/configCreateExample.xml");
    LocalRegistryFactory fac = new LocalRegistryFactory();
    Registry reg = fac.connect(props);
    */
    props.setProperty(RegistryContext.BOOTSTRAP, "classpath.bootstrap.properties");
    RegistryContext ctx = new RegistryContext(props);
    Registry reg = ctx.connect();
    
    RegisSession s = reg.open();
    CodeGenConfig conf = new CodeGenConfig(new File("codegen/src"));
    conf.setPackagePrefix("org.sapia");
    NodeIntrospector root = new NodeIntrospector(reg.getRoot(), conf);
    root.generate();
    s.close();
    
  }  

}
