package org.sapia.soto.xfire.sample;

import javax.xml.ws.WebFault;

@WebFault(name="GreetingException", targetNamespace=HelloConstants.NAMESPACE)
public class GreetingException extends Exception{
  
  public GreetingException(String msg){
    super(msg);
  }
  
  public String getFaultInfo() {
    return getMessage();
  }

}
