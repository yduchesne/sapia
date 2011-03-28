package org.sapia.soto.spring.selectors;

import org.sapia.soto.ServiceMetaData;
import org.sapia.soto.ServiceSelector;

/**
 * A {@link ServiceSelector} that selects services with a given ID.
 * 
 * @author yduchesne
 *
 */
public class BeanNameSelector implements ServiceSelector {

  private String _name;

  public BeanNameSelector(String name) {
    _name = name;
  }

  public boolean accepts(ServiceMetaData meta) {
    if(meta.getServiceID() != null){
      if(_name == null){
        return true;
      }
      else{
        return meta.getServiceID() != null && meta.getServiceID().equals(_name);
      }
    }
    else{
      return false;
    }
  }
}
