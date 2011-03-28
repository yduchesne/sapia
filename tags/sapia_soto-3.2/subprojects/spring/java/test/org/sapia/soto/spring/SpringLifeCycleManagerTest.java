package org.sapia.soto.spring;

import java.io.File;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.sapia.soto.SotoContainer;
import org.sapia.soto.util.Utils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.core.io.Resource;

import junit.framework.TestCase;

public class SpringLifeCycleManagerTest extends TestCase {
  
  private SotoContainer container;
  private ApplicationContext ctx;
  private BeanFactory factory;

  public SpringLifeCycleManagerTest(String arg0) {
    super(arg0);
  }
  
  protected void setUp() throws Exception {
    container = new SotoContainer();
    container.load(new File("etc/test/main.xml"));
    container.start();
    SpringLifeCycleManager manager = (SpringLifeCycleManager)container.getLifeCycleManagerFor("spring");
    ctx = (ApplicationContext)manager;
    factory = (BeanFactory)manager;
  }
  
  public void testBeanFactoryAware() throws Exception{
    TestSpringService service = (TestSpringService)container.lookup("test");
    assertTrue(service.factory != null);
  }
  
  public void testAppContextAware() throws Exception{
    TestSpringService service = (TestSpringService)container.lookup("test");
    assertTrue(service.ctx != null);
  }  

  public void testInit() throws Exception{
    TestSpringService service = (TestSpringService)container.lookup("test");
    assertTrue(service.init);
  }
  
  public void testDestroy() throws Exception{
    container.dispose();
    TestSpringService service = (TestSpringService)container.lookup("test");
    assertTrue(service.destroy);
  }  
  
  
  public void testPostProcessor() throws Exception{
    TestBeanPostProcessor proc = (TestBeanPostProcessor)container.lookup("postProcessor");
    assertTrue(proc.before);
    assertTrue(proc.after);    
  }
  
  public void testAppListener() throws Exception{
    ctx.publishEvent(new TestApplicationEvent("TEST"));
    TestSpringService service = (TestSpringService)container.lookup("test");
    assertEquals("TEST", service.evt.getSource());
  }
  
  ////// BeanFactory tests...
  
  public void testContainsBeanForName(){
    assertTrue(factory.containsBean("test"));
  }
  
  public void testGetBeanForName(){
    factory.getBean("test");
    try{
      factory.getBean("test2");
      fail("Should not have found bean for type");
    }catch(NoSuchBeanDefinitionException e){}    
  }
  
  public void testGetBeanForNameAndType(){
    factory.getBean("test", TestSpringService.class);
    
    try{
      factory.getBean("test2", TestSpringService.class);
      fail("Should not have found bean for type");
    }catch(NoSuchBeanDefinitionException e){}    
    
    try{
      factory.getBean("test", String.class);
      fail("Should not have found bean for type");
    }catch(BeanNotOfRequiredTypeException e){}
  }
  
  public void testGetType() throws Exception{
    factory.getType("test").equals(TestSpringService.class);
  }
  
  ////// ApplicationContext tests...
  
  public void testGetBeanDefinitionCount() throws Exception{
    assertEquals(3, ctx.getBeanDefinitionCount());
  }
  
  public void testGetBeanDefNames() throws Exception{
    Set names = new HashSet();
    String[] str = ctx.getBeanDefinitionNames();
    assertEquals(2, str.length);    
    names.add(str[0]);
    names.add(str[1]);    
  }  
  
  public void testGetBeansOfType() throws Exception{
    
    Map beans = ctx.getBeansOfType(TestSpringService.class);
    assertEquals(1, beans.size());
    assertTrue(beans.get("test") instanceof TestSpringService);
  }
  
  public void testGetBeanNamesForType() throws Exception{
    Set names = new HashSet();
    String[] str = ctx.getBeanNamesForType(TestSpringService.class);
    assertEquals(1, str.length);    
    names.add(str[0]);
  }  

  public void testGetResource() throws Exception{
    Resource res = ctx.getResource("file:etc/test/resource.txt");
    assertTrue(res.exists());
    assertTrue(res.getFile().exists());
    assertEquals("this is a test", Utils.textStreamToString(res.getInputStream()).trim());
  }
  
  
  public void testGetMessage() throws Exception{
    String msg = ctx.getMessage("test.msg", null, Locale.FRENCH);
    assertEquals("ceci est un test", msg.trim());
  }   
    
  
  public static final class TestApplicationEvent extends ApplicationEvent{
    
    public TestApplicationEvent(String str){
      super(str);
    }
    
  }
  
  
}
