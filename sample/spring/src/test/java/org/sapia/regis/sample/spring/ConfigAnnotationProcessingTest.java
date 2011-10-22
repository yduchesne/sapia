package org.sapia.regis.sample.spring;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ConfigAnnotationProcessingTest {

  @Test
  public void testLoadApplication() throws Exception{
    ClassPathXmlApplicationContext coreContext = new ClassPathXmlApplicationContext();
    coreContext.registerShutdownHook();
    coreContext.setConfigLocation("application.xml");
    coreContext.refresh();    
  }
}
