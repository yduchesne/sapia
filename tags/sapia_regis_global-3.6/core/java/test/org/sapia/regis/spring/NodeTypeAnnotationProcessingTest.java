package org.sapia.regis.spring;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.sapia.regis.RWNode;
import org.sapia.regis.RWSession;
import org.sapia.regis.Registry;
import org.sapia.regis.loader.RegistryConfigLoader;
import org.sapia.regis.local.LocalRegistryFactory;

public class NodeTypeAnnotationProcessingTest {
  
  private Registry registry;

  @Before
  public void setUp() throws Exception {
    LocalRegistryFactory factory = new LocalRegistryFactory();
    registry = factory.connect(new Properties());
    RWSession session = (RWSession)registry.open();
    session.begin();    
    RWNode node = (RWNode)registry.getRoot();
    RegistryConfigLoader loader = new RegistryConfigLoader(node);
    loader.load(new File("etc/springPropertyExample.xml"));
    session.commit();    
  }
  
  @Test
  public void testNonAutoNodeType(){
    RegisAnnotationProcessor proc = new RegisAnnotationProcessor();
    proc.setRegistry(registry);
    TestPropertyHolder bean = new TestPropertyHolder();
    proc.postProcessBeforeInitialization(bean, "test");

    assertEquals("jsmith", bean.getUsername());
    assertEquals(23, bean.getAge());
    assertEquals(123456789L, bean.getTimestamp());
    assertTrue(bean.isAdmin());    
    assertTrue(bean.isValid());
    assertTrue(bean.getGreeting().equals("Welcome"));
    assertTrue(bean.getBirthDate() != null);    
    assertTrue(bean.getGender() != null);
  }
  
  @Test
  public void testNonAutoType(){
    RegisAnnotationProcessor proc = new RegisAnnotationProcessor();
    proc.setRegistry(registry);
    TestAutoPropertyHolder bean = new TestAutoPropertyHolder();
    proc.postProcessBeforeInitialization(bean, "test");

    assertEquals("jsmith", bean.getUsername());
    assertEquals(23, bean.getAge());
    assertEquals(123456789L, bean.getTimestamp());
    assertTrue(bean.isAdmin());    
    assertTrue(bean.isValid());
    assertTrue(bean.getBirthDate() != null);
  }  

}
