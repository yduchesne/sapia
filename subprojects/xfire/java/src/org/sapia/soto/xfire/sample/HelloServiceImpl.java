package org.sapia.soto.xfire.sample;

public class HelloServiceImpl implements HelloService{
  
  public String getGreeting(String firstName, String lastName) {
    return GREETING;
  }

}
