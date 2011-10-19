package org.sapia.soto.ubik.monitor;

import java.util.Properties;

public class TestFeedbackMonitorableService implements FeedbackMonitorable{
  
  public Properties monitor() throws Exception {
    Properties props = new Properties();
    props.setProperty("testProp", "testValue");
    return props;
  }

}
