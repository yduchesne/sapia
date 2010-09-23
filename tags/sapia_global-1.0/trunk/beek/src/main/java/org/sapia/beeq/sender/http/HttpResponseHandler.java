package org.sapia.beeq.sender.http;

/**
 * A callback interface whose method is invoked once a HTTP post as successfully
 * be done. The call back is given the data of the response (by the {@link HttpSender}
 * that sent the corresponding request).
 * 
 * @author yduchesne
 *
 */
public interface HttpResponseHandler {
  
  public void handleResponse(byte[] data) throws Exception;

}
