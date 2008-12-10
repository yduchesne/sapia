package org.sapia.regis.codegen;

import java.io.File;
import java.util.Properties;

import org.sapia.regis.RegisSession;
import org.sapia.regis.Registry;
import org.sapia.regis.RegistryContext;
import org.sapia.regis.codegen.output.CodeGenConfig;
import org.sapia.regis.codegen.output.CodeGenerator;
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
    
    props.setProperty(LocalRegistryFactory.BOOTSTRAP, 
        "etc/configCreateExample.xml");
    LocalRegistryFactory fac = new LocalRegistryFactory();
    Registry reg = fac.connect(props);
    RegisSession s = reg.open();
    CodeGenConfig conf = new CodeGenConfig(new File("codegen/src"));
    conf.setPackagePrefix("org.sapia");
    CodeGenerator gen = new CodeGenerator(reg, conf);
    gen.generate();
    s.close();
    
  }  

}
