package org.sapia.soto.ubik.monitor;

public class TestMonitoredService implements Monitorable {
  
  public boolean error;
  public boolean wasCalled;
  
  public TestMonitoredService(boolean error){
    this.error = error;
  }
  
  public void perform() throws Exception{
    wasCalled = true;
    if(error){
      throw new Exception("Status error");
    }
  }
  
  public void perform(String arg) throws Exception{
    wasCalled = true;
    if(error){
      throw new Exception("Status error");
    }    
  }
  
  public void monitor() throws Exception {
    perform();
  }

}
