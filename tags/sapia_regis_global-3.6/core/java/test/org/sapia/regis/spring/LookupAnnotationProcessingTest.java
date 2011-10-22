package org.sapia.regis.spring;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.sapia.regis.RWNode;
import org.sapia.regis.RWSession;
import org.sapia.regis.Registry;
import org.sapia.regis.loader.RegistryConfigLoader;
import org.sapia.regis.local.LocalRegistryFactory;

public class LookupAnnotationProcessingTest {
  
  private Registry registry;

  @Before
  public void setUp() throws Exception {
    LocalRegistryFactory factory = new LocalRegistryFactory();
    registry = factory.connect(new Properties());
    RWSession session = (RWSession)registry.open();
    session.begin();    
    RWNode node = (RWNode)registry.getRoot();
    RegistryConfigLoader loader = new RegistryConfigLoader(node);
    loader.load(new File("etc/configCreateExample.xml"));
    session.commit();    
  }

  @Test
  public void testProcessFieldsForBaseClass() {
    RegisAnnotationProcessor proc = new RegisAnnotationProcessor();
    proc.setRegistry(registry);
    TestBaseRegisFieldAnnotatedClass bean = new TestBaseRegisFieldAnnotatedClass();
    proc.postProcessBeforeInitialization(bean, "test");
    
    assertEquals(registry, bean.getRegistry());
    assertTrue(bean.getNode() != null);
  }
  
  @Test
  public void testProcessFieldsForChildClass() {
    RegisAnnotationProcessor proc = new RegisAnnotationProcessor();
    proc.setRegistry(registry);
    TestChildRegisFieldAnnotatedClass bean = new TestChildRegisFieldAnnotatedClass();
    proc.postProcessBeforeInitialization(bean, "test");
    
    assertEquals(registry, bean.getRegistry());
    assertTrue(bean.getNode() != null);
  }  
  
  @Test
  public void testProcessMethodsForBaseClass() {
    RegisAnnotationProcessor proc = new RegisAnnotationProcessor();
    proc.setRegistry(registry);
    TestBaseRegisMethodAnnotatedClass bean = new TestBaseRegisMethodAnnotatedClass();
    proc.postProcessBeforeInitialization(bean, "test");
    
    assertEquals(registry, bean.getRegistry());
    assertTrue(bean.getNode() != null);
  }  
  
  @Test
  public void testProcessMethodsForChildClass() {
    RegisAnnotationProcessor proc = new RegisAnnotationProcessor();
    proc.setRegistry(registry);
    TestChildRegisMethodAnnotatedClass bean = new TestChildRegisMethodAnnotatedClass();
    proc.postProcessBeforeInitialization(bean, "test");
    
    assertEquals(registry, bean.getRegistry());
    assertTrue(bean.getNode() != null);
  }  
  
  @Test
  public void testNodeCapable(){
    RegisAnnotationProcessor proc = new RegisAnnotationProcessor();
    proc.setRegistry(registry);
    TestNodeCapableAnnotatedClass bean = new TestNodeCapableAnnotatedClass();
    proc.postProcessBeforeInitialization(bean, "test");
    
    assertTrue(bean.getNodeCapable() != null);    
  }
}
