package org.sapia.soto.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public class TestSpringService implements
  InitializingBean, DisposableBean,  
  BeanFactoryAware,
  ApplicationContextAware,
  ApplicationListener{
  
  public ApplicationContext ctx;
  public BeanFactory factory;
  public ApplicationEvent evt;
  public boolean init, destroy;
  
  public void onApplicationEvent(ApplicationEvent arg0) {
    evt = arg0;
  }

  public void setBeanFactory(BeanFactory arg0) throws BeansException {
    factory = arg0;
  }
  
  public void setApplicationContext(ApplicationContext arg0) throws BeansException {
    ctx = arg0;
  }
  
  public void afterPropertiesSet() throws Exception {
    init = true;
  }
  
  public void destroy() throws Exception {
    destroy = true;
  }

}
