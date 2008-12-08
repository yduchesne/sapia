package org.sapia.regis.gui.event;

import org.sapia.regis.Property;

public class PropertyCreationEvent {
  
  private Property _prop;
  
  public PropertyCreationEvent(Property prop){
    _prop = prop;
  }
  
  public Property getProperty(){
    return _prop;
  }

}
