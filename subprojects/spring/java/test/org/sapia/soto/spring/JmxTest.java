package org.sapia.soto.spring;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.sapia.soto.SotoContainer;

import junit.framework.TestCase;

public class JmxTest extends TestCase {

  private SotoContainer container;
  
  public JmxTest(String arg0) {
    super(arg0);
  }

  protected void setUp() throws Exception {
    container = new SotoContainer();
    container.load(new File("etc/test/jmx.xml"));
    container.start();
  }
  
  public void testLoad() throws Exception{
    MBeanServer mbeanServer = (MBeanServer)container.lookup(MBeanServer.class);
    
    
    ObjectName name = new ObjectName("bean:name=testBean1");
    MBeanInfo mbean = mbeanServer.getMBeanInfo(name);
    Set<String> expectedAttrs = new HashSet<String>();
    expectedAttrs.add("Name");
    expectedAttrs.add("Age");
    MBeanAttributeInfo[] attrs = mbean.getAttributes();
    for(MBeanAttributeInfo attr:attrs){
      assertTrue(expectedAttrs.contains(attr.getName()));
    }
  }
}
