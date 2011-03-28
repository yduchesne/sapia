package org.sapia.soto.ubik.example;

import java.util.Properties;

public class MonitoredService{
  
  public Properties doCheck(String someString, int someInt) throws Exception {
    Properties props = new Properties();
    props.setProperty("test", "testValue");
    return props;
  }

}
