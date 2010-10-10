package org.sapia.regis.sample;

import java.util.Properties;

import org.sapia.regis.RegisSession;
import org.sapia.regis.Registry;
import org.sapia.regis.RegistryContext;

import junit.framework.TestCase;

public class CodeGenTest extends TestCase{
  
  public CodeGenTest(String name){
    super(name);
  }
  
  public void testLoad() throws Exception{
    Properties props = new Properties();
    
    props.setProperty(RegistryContext.BOOTSTRAP, 
        "etc/codegen/bootstrap.properties");
    RegistryContext ctx = new RegistryContext(props);
    Registry reg = ctx.connect();
    RegisSession s = reg.open();
        
    SampleApp appConfig = SampleAppFactory.createFor(reg);
    for(DatabasesNode node : appConfig.getDatabases().getDatabasesNodes()){
      System.out.println(node.getUsername() + ", " + node.getUrl());
    }
    
    s.close();
    
  }    

}
