package org.sapia.soto.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class TestBeanPostProcessor implements BeanPostProcessor{
  
  public boolean before, after;
  
  public Object postProcessBeforeInitialization(Object arg0, String arg1) throws BeansException {
    before = true;
    return arg0;
  }
  
  public Object postProcessAfterInitialization(Object arg0, String arg1) throws BeansException {
    after = true;
    return null;
  }

}
