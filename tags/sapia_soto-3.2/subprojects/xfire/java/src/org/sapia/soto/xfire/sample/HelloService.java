package org.sapia.soto.xfire.sample;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

/**
 *  This is a web service that returns a greeting to someone.
 * @author yduchesne
 *
 */
@WebService(name="HelloService",
            targetNamespace=HelloConstants.NAMESPACE)
public interface HelloService {
  
  public static final String GREETING = "Hello World!!!";  
  
  /**
   * Returns an arbitrary greeting.
   * 
   * @param firstName the first name of the person to send back a greeting to.
   * @param lastName the last name of the person to send back a greeting to.    
   * @return a greeting.
   * @throws GreetingException if a problem occurs sending back the greeting
   * @throws Exception if an internal problem occurs. 
   */      
  @WebMethod(action="urn:getGreeting")
  @WebResult(name="greeting")
  public String getGreeting(@WebParam(name="firstName") String firstName, @WebParam(name="lastName") String lastName) throws GreetingException, Exception;

}
