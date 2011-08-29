package org.sapia.regis.cache;

import java.util.Properties;

import org.sapia.regis.RegistryContext;
import org.sapia.regis.local.LocalRegistryFactory;

import junit.framework.TestCase;

public class CacheRegistryFactoryTest extends TestCase {

  public CacheRegistryFactoryTest(String arg0) {
    super(arg0);
  }
  
  public void testConnect() throws Exception{
    Properties props = new Properties();
    props.setProperty(RegistryContext.FACTORY_CLASS, CacheRegistryFactory.class.getName());
    props.setProperty(CacheRegistryFactory.FACTORY_CLASS, LocalRegistryFactory.class.getName());
    RegistryContext ctx = new RegistryContext(props);
    ctx.connect();
  }

  
}
