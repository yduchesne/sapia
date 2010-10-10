package org.sapia.soto.spring.selectors;

import org.sapia.soto.ServiceMetaData;
import org.sapia.soto.ServiceSelector;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * A {@link ServiceSelector} that selects instances of the {@link BeanPostProcessor}
 * interface.
 * 
 * @author yduchesne
 *
 */
public class BeanPostProcessorSelector implements ServiceSelector{
  
  public boolean accepts(ServiceMetaData meta) {
    return meta.getService() instanceof BeanPostProcessor;
  }
}
