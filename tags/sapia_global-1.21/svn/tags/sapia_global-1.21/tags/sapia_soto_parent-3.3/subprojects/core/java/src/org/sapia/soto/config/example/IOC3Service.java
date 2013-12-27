package org.sapia.soto.config.example;

import org.sapia.soto.Service;

public class IOC3Service implements Service{
  
  public boolean init, start, dispose;
  public String stringValue, property;
  public int intValue, otherIntValue;
  
  public IOC3Service(String strVal, int intVal, int otherIntVal){
    this.stringValue = strVal;
    this.intValue = intVal;
    this.otherIntValue = otherIntVal;
  }
  
  public void init() throws Exception {
    init = true;
  }
  
  public void start() throws Exception {
    start = true;
  }
  
  public void dispose() {
    dispose = true;
  }

  public void setProperty(String prop){
    property = prop;
  }
}
