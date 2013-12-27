package org.sapia.soto.config;

import java.io.File;

import org.sapia.soto.SotoContainer;
import org.sapia.soto.config.example.IOC3Service;

import junit.framework.TestCase;

public class AssignTest extends TestCase {

  public AssignTest(String arg0) {
    super(arg0);
  }
  
  public void testAssign() throws Exception{
    SotoContainer cont = new SotoContainer();
    cont.load(new File("etc/assign/service.xml"));
    cont.start();
    IOC3Service service = (IOC3Service)cont.lookup("ioc3");
    super.assertEquals("test", service.stringValue);
    super.assertEquals("test", service.property);
    super.assertEquals(10, service.intValue);
  }

  public void testAssignIncluded() throws Exception{
    SotoContainer cont = new SotoContainer();
    cont.load(new File("etc/assign/service.xml"));
    cont.start();
    IOC3Service service = (IOC3Service)cont.lookup("ioc3-included");
    super.assertEquals("test", service.stringValue);
    super.assertEquals("test", service.property);
    super.assertEquals(10, service.intValue);
  }  
}
