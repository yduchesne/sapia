package org.sapia.corus.processor;

import java.io.File;
import java.io.FileInputStream;

import org.sapia.corus.admin.services.processor.ExecConfig;

import junit.framework.TestCase;

public class ExecConfigTest extends TestCase {

  protected void setUp() throws Exception {
    super.setUp();
  }

  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testNewInstance() throws Exception{
    ExecConfig.newInstance(new FileInputStream(new File("etc/exec/testConf.xml")));
  }

}
