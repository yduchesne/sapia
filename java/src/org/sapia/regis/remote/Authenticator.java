package org.sapia.regis.remote;

public class Authenticator {

  private String _username, _password;
  
  Authenticator(String username, String password){
    _username = username;
    _password = password;
  }
  
  void authenticate(String username, String password){
    if(_username == null || _password == null){
      return;
    }
    else{
      if(_username == null){
        throw new IllegalStateException("Registry server username not set");
      }
      if(username == null){
        throw new SecurityException("Invalid access; username not specified");
      }
      if(!_username.equals(username)){
        throw new SecurityException("Invalid access; username invalid");
      }
      if(_password != null){
        if(password == null){
          throw new SecurityException("Invalid access; password not specified");
        }
        if(!_password.equals(password)){
          throw new SecurityException("Invalid access; password invalid");
        }
      }
    }
  }
}
