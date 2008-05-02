package org.sapia.beeq.sender.http;

import java.net.URI;
import java.net.URL;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.sapia.beeq.Message;
import org.sapia.beeq.sender.Sender;
import org.sapia.beeq.sender.SenderException;

public class HttpSender implements Sender{
  
  public boolean accepts(Message message) {
    if(message.getDestination() instanceof String){
      return doAccepts((String)message.getDestination());
    }
    else if(message.getDestination() instanceof URL){
      return doAccepts(((URL)message.getDestination()).toExternalForm());
    }
    else if(message.getDestination() instanceof URI){
      return doAccepts(((URI)message.getDestination()).toString());
    }
    else{
      return false;
    }
  }
  
  public void send(Message message) throws Exception {
    doSend(message); 
  }
  
  protected byte[] doSend(Message message) throws Exception{
    HttpClient client = new HttpClient();
    String dest = getDestination(message);
    HttpMethod method = new PostMethod(dest);
    try{
      client.executeMethod(method);
      if(method.getStatusCode() == HttpStatus.SC_OK){
        return method.getResponseBody();
      }
      else{
        throw new SenderException("Could not send message to " + dest + " - status code: " + method.getStatusCode());
      }
    }finally{
      method.releaseConnection();
    }
  }
  
  private String getDestination(Message message){
    if(message.getDestination() instanceof String){
      return (String)message.getDestination();
    }
    else if(message.getDestination() instanceof URL){
      return ((URL)message.getDestination()).toExternalForm();
    }
    else if(message.getDestination() instanceof URI){
      return ((URI)message.getDestination()).toString();
    }
    else{
      throw new IllegalArgumentException("Expected instance of String, URL or URI as message destination");
    }    
  } 
  
  private boolean doAccepts(String url){
    return url.startsWith("http:") || url.startsWith("https:");
  }

}
