package org.sapia.soto.ldap.server;

import java.io.File;
import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import junit.framework.TestCase;

import org.sapia.soto.SotoContainer;

public class LdapServiceImplTest extends TestCase {
  
  private SotoContainer container;

  public LdapServiceImplTest(String arg0) {
    super(arg0);
  }

  protected void setUp() throws Exception {
    container = new SotoContainer();
    container.load(new File("etc/server.soto.xml"));
    container.start();
  }

  protected void tearDown() throws Exception {
    container.dispose();
    deleteRecurse(new File("etc/server"));
  }
  
  public void testAuthenticate() throws Exception{
    System.setProperty("java.security.auth.login.config", "file:" + 
        System.getProperty("user.dir") + "/etc/jaas.conf");
    LoginContext ctx = new LoginContext("JaasSample", new TestCallbackHandler("admin", "secret"));
    ctx.login();
    try{
      ctx = new LoginContext("JaasSample", new TestCallbackHandler("admin", "foo"));
      ctx.login();
      fail("Should not have logged in");
    }catch(LoginException loe){
      //ok
    }
  }
  
  class TestCallbackHandler implements CallbackHandler{
    
    String username, password;
    
    TestCallbackHandler(String u, String p){
      username = u;
      password = p;
    }
    
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
      for(int i = 0; i < callbacks.length; i++){
        if(callbacks[i] instanceof NameCallback){
          NameCallback cb = (NameCallback)callbacks[i];
          cb.setName(username);
        }
        else if(callbacks[i] instanceof PasswordCallback){
          PasswordCallback cb = (PasswordCallback)callbacks[i];
          cb.setPassword(password.toCharArray());
        }
      }
    }
  }
  
  void deleteRecurse(File f){
    if(f.isDirectory()){
      File[] files = f.listFiles();
      if(files != null){
        for(int i = 0; i < files.length; i++){
          if(files[i].isDirectory()){
            deleteRecurse(files[i]);
          }
          else{
            files[i].delete();
          }
        }
      }
      f.delete();
    }
    else{
      f.delete();
    }
  }   
}
