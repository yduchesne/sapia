package org.sapia.soto.xfire.sample;

import javax.jws.WebService;

/**
 * This is a web service implemented in Java with Jsr181 annotations.
 * 
 * @author yduchesne
 *
 */
@WebService(endpointInterface=HelloConstants.ENDPOINT_INTERFACE,
            serviceName="HelloWebService")
public class Jsr181HelloService implements HelloService{

  public String getGreeting(String firstName, String lastName) throws GreetingException, Exception{
    return GREETING; 
  }

}
  