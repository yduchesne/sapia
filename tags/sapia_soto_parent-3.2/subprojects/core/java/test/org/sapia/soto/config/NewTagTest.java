package org.sapia.soto.config;

import java.io.File;

import org.sapia.soto.SotoContainer;
import org.sapia.soto.config.example.IOC3Service;

import junit.framework.TestCase;

public class NewTagTest extends TestCase {

  public NewTagTest(String arg0) {
    super(arg0);
  }
  
  public void testEmptyConstructor() throws Exception{
    NewTag.Constructor cons = new NewTag.Constructor();
    cons.newInstance(TestConstInjectionBean.class.getName());
  }
  
  public void testArgConstructor() throws Exception{
    NewTag.Constructor cons = new NewTag.Constructor();
    NewTag.Arg arg1 = cons.createArg();
    arg1.setType(String.class.getName());
    arg1.setValue("test");
    NewTag.Arg arg2 = cons.createArg();
    arg2.setValue(new Integer(10));
    TestConstInjectionBean bean = (TestConstInjectionBean)cons.newInstance(TestConstInjectionBean.class.getName());
    super.assertEquals("test", bean.arg1);
    super.assertTrue(10 == bean.arg2);
  }  
  
  public void testConfiguration() throws Exception{
    SotoContainer cont = new SotoContainer();
    cont.load(new File("etc/ioc3/service.xml"));
    cont.start();
    IOC3Service service = (IOC3Service)cont.lookup("ioc3");
    super.assertEquals("test", service.stringValue);
    super.assertEquals("test", service.property);
    super.assertEquals(10, service.intValue);
    super.assertTrue(service.start);
    super.assertTrue(service.init);
    cont.dispose();
    super.assertTrue(service.dispose);
  }
}
