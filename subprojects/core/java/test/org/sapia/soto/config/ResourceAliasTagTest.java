package org.sapia.soto.config;

import java.io.File;

import org.sapia.soto.SotoContainer;

import junit.framework.TestCase;

public class ResourceAliasTagTest extends TestCase {

  public ResourceAliasTagTest(String arg0) {
    super(arg0);
  }
  
  public void testLoad() throws Exception{
    SotoContainer container = new SotoContainer();
    container.load(new File("etc/alias/alias.xml"));
  }
  
}
