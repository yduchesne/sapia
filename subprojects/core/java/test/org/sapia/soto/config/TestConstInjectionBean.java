package org.sapia.soto.config;

public class TestConstInjectionBean {
  
  String arg1;
  int arg2;
  
  public TestConstInjectionBean(){}

  public TestConstInjectionBean(String arg1){
    this.arg1 = arg1;
  }
  
  public TestConstInjectionBean(String arg1, int arg2){
    this.arg1 = arg1;
    this.arg2 = arg2;
  }
}
