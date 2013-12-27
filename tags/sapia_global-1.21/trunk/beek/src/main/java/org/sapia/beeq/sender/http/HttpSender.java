package org.sapia.beeq.sender.http;

import java.net.URI;
import java.net.URL;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.sapia.beeq.Message;
import org.sapia.beeq.sender.Sender;
import org.sapia.beeq.sender.SenderException;

/**
 * Implements the {@link Sender} over a {@link HttpClient}. An instance
 * of this class connects to a HTTP server either through http or https. 
 * It supports basic authentication (username and password must be set). 
 * The authentication scope ({@link AuthScope}) can also be set (if not,
 * a default one will internally be created).
 * <p>
 * Furthermore, this instance accepts messages that have either a {@link String},
 * a {@link URL} or a {@link URI} as a destination. Either one of these must specify
 * an address starting with <code>http:</code> or <code>https:</code>.
 * <p>
 * An instance of {@link HttpResponseHandler} must be set, to which the data
 * of the response is eventually dispatched.
 * 
 * @author yduchesne
 *
 */
public class HttpSender implements Sender{
  
  private String username, password;
  private AuthScope scope;
  private HttpResponseHandler handler;
  
  public void setPassword(String password) {
    this.password = password;
  }

  public void setUsername(String username) {
    this.username = username;
  }
  
  public void setHandler(HttpResponseHandler handler){
    this.handler = handler;
  }
  
  /**
   * @param scope sets the 
   */
  public void setAuthScope(AuthScope scope){
    this.scope = scope;
  }

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
    if(handler == null){
      throw new IllegalStateException(HttpResponseHandler.class.getName() + " not set");
    }
    handler.handleResponse(doSend(message)); 
  }
  
  protected byte[] doSend(Message message) throws Exception{
    HttpClient client = new HttpClient();
    String dest = getDestination(message);    
    HttpMethod method = new PostMethod(dest);
    
    if(username != null && password != null){
      AuthScope aScope = scope;
      if(aScope == null){
        aScope = new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM, AuthScope.ANY_SCHEME);
      }
      client.getState().setCredentials(aScope, new UsernamePasswordCredentials(username, password));
      method.setDoAuthentication(true); 
    }
    else if(username != null){
      throw new IllegalStateException("Username is set, but not password");
    }
    else if(password != null){
      throw new IllegalStateException("Password is set, but not username");
    }
    
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
