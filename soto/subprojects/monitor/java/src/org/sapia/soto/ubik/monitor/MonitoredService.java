package org.sapia.soto.ubik.monitor;

import java.util.Properties;

public class MonitoredService implements FeedbackMonitorable{

  private boolean _throwExc;
  
  public void setThrowException(boolean throwExc){
    _throwExc = throwExc; 
  }
  
  public Properties monitor() throws Exception {
    if(_throwExc){
      throw new Exception("An error occured");
    }
    return System.getProperties();
  }
}

