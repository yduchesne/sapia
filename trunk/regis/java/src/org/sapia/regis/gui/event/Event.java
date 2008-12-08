package org.sapia.regis.gui.event;

public class Event {

  Object source;
  
  public Event(Object source){
    this.source = source;
  }

  public Object getSource() {
    return source;
  }
}
