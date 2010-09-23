package org.sapia.regis.gui.event;

import org.sapia.regis.Property;

public class NewNodePropertyCreationEvent{
  
  
  private Property _prop;
  
  public NewNodePropertyCreationEvent(Property prop){
    _prop = prop;
  }
  
  public Property getProperty(){
    return _prop;
  }

}
