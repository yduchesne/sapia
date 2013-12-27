package org.sapia.soto.spring.selectors;

import org.sapia.soto.ServiceMetaData;
import org.sapia.soto.ServiceSelector;

/**
 * A {@link ServiceSelector} that is used to count the number of beans
 * in the container.
 * 
 * @author yduchesne
 *
 */
public class BeanCountSelector implements ServiceSelector {

  public BeanCountSelector() {
  }

  public boolean accepts(ServiceMetaData meta) {
    return true; 
  }
}
