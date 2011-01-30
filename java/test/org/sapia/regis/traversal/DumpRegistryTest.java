package org.sapia.regis.traversal;

import java.io.File;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.sapia.regis.RWNode;
import org.sapia.regis.RWSession;
import org.sapia.regis.Registry;
import org.sapia.regis.loader.RegistryConfigLoader;
import org.sapia.regis.local.LocalRegistryFactory;

public class DumpRegistryTest {

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testDump() throws Exception{
    LocalRegistryFactory factory = new LocalRegistryFactory();
    Registry reg = factory.connect(new Properties());
    RWSession session = (RWSession)reg.open();
    session.begin();    
    RWNode node = (RWNode)reg.getRoot();
    RegistryConfigLoader loader = new RegistryConfigLoader(node);
    loader.load(new File("etc/configCreateExample.xml"));
    session.commit();
    new DumpRegistry().dump(reg);  
  }

}
