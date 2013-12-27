/*
 * MailStep.java
 *
 * Created on September 20, 2005, 9:29 AM
 *
 */

package org.sapia.soto.state.mail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.ClassUtils;
import org.sapia.soto.Debug;
import org.sapia.soto.state.Output;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.State;
import org.sapia.soto.state.Step;
import org.sapia.soto.state.StmKey;
import org.sapia.soto.state.config.StepContainer;
import org.sapia.soto.state.helpers.ScopeParser;

/**
 *
 * @author yduchesne
 */
public class MailStep extends StepContainer implements Step, State{
  
  public static final String DEFAULT_PROTOCOL = "smtp";
  public static final int    DEFAULT_PORT     = 25;
  public static final String DEFAULT_ENCODING = "UTF-16";
  public static final String DEFAULT_CONTENT_TYPE     = "text/plain";
  
  private String _username, _password, _from, _host, _id;
  private int _port = DEFAULT_PORT;
  private String _protocol = DEFAULT_PROTOCOL;
  private String _encoding = DEFAULT_ENCODING;
  //private String _contentType = DEFAULT_CONTENT_TYPE;
  private StmKey _subject, _recipient;
  
  /**
   * Creates a new instance of MailStep
   */
  public MailStep() {
  }
  
  public void setId(String id){
    _id = id;
  }
  
  public String getId(){
    return _id;
  }
  
  public String getName(){
    return ClassUtils.getShortClassName(getClass());
  }
  
  public void setUsername(String username){
    _username = username;
  }
  
  public void setPassword(String password){
    _password = password;
  }
  
  public void setSubject(String scopeKey){
    _subject = ScopeParser.parseKey(scopeKey);
  }
  
  public void setTo(String scopeKey){
    setRecipient(scopeKey);
  }
  
  public void setRecipient(String scopeKey){
    _recipient = StmKey.parse(scopeKey);
  }
  
  public void setFrom(String from){
    _from = from;
  }

  public void setEncoding(String encoding){
    _encoding = encoding;
  }
  
  public void setHost(String host){
    _host = host;
  }
  
  public void setPort(int port){
    _port = port;
  }
  
  public void setProtocol(String proto){
    _protocol = proto;
  }
  
  /*public void setContentType(String type){
    _contentType = type;
  }*/
  
  public void execute(Result res){
    if(_host == null){
      res.error("SMTP host not set");
      return;
    }
    if(_from == null){
      res.error("From address not set");
      return;
    }
    if(_recipient == null){
      res.error("Recipient not set");
      return;
    }
    if(_subject == null){
      res.error("Subject not set");
      return;
    }
    
    // Substituting output
    
    Output output = (Output)res.getContext();
    OutputStream original = null;
    
    try{
      original = output.getOutputStream();
    }catch(IOException e){
      res.error("Could not acquire output stream from context", e);
      return;
    }
    try{
      ByteArrayOutputStream tmp = new ByteArrayOutputStream();
      try{
        output.setOutputStream(tmp);
        super.execute(res);
        if(!res.isError() && !res.isAborted()){
          doExecute(res, new String(tmp.toByteArray(), _encoding));
        }
      }catch(IOException e){
        res.error("Could not subsitute for mail output stream", e);
      }
    }finally{
      try{
        output.setOutputStream(original);
      }catch(IOException e){}
    }
  }
  
  private void doExecute(Result res, String mailContent){
    
    Properties props = new Properties();
    props.setProperty("mail.smtp.host", _host);
    props.setProperty("mail.smtp.port", Integer.toString(_port));
    
    Session sess = Session.getInstance(props,
      (_username != null && _password != null) ?
        new MailAuthenticator(_username, _password) :
        null);
    
    sess.setDebug(Debug.DEBUG);
    
    Transport trans = null;
    
    String to = (String)_recipient.lookup(res);
    String subject = (String)_subject.lookup(res);
    if(to == null){
      res.error("Recipient not found under: " + _recipient);
      return;
    }
    if(subject == null){
      res.error("Subject not found under: " + _subject);
      return;
    }
    try{
      trans = sess.getTransport(_protocol);
      trans.connect(_host, _port, _username, _password);
    }catch(NoSuchProviderException e){
      res.error("Could not acquire provider for: " + _protocol, e);
      return;
    }catch(MessagingException e){
      try{
        if(trans.isConnected()) trans.close();
      }catch(MessagingException e2){}
      res.error("Error sending email", e);
      return;
    }
    
    MimeMessage msg = new MimeMessage(sess);
    
    try{
      msg.setSubject(subject);
    }catch(MessagingException e){
      try{
        if(trans.isConnected()) trans.close();
      }catch(MessagingException e2){}
      res.error("Could not set subject", e);
      return;
    }
    
    try{
      String from = (String)StmKey.parse(_from).lookup(res.getContext());
      if(from == null) from = _from;
      msg.setFrom(new InternetAddress(from));
      msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
    }catch(MessagingException e){
      try{
        if(trans.isConnected()) trans.close();
      }catch(MessagingException e2){}
      res.error("Could not create email address", e);
      return;
    }
    
    try{
      msg.setText(mailContent, _encoding);
    }catch(MessagingException e){
      try{
        if(trans.isConnected()) trans.close();
      }catch(MessagingException e2){}
      res.error("Could not set mail text", e);
      return;
    }
    
    try{
      Transport.send(msg);
    }catch(MessagingException e){
      res.error("Could not send email", e);
      return;
    }finally{
      try{
        trans.close();
      }catch(MessagingException e){}
    }
  }
  
  public static class MailAuthenticator extends Authenticator{
    
    private String _username, _password;
    
    MailAuthenticator(String username, String password){
      _username = username;
      _password = password;
    }
    
    public PasswordAuthentication getPasswordAuthentication(){
      return new PasswordAuthentication(_username, _password);
    }
  }
  
  public static void main(String[] args) {
    try{
      Class.forName("org.sapia.soto.state.mail.MailStep");
    }catch(Exception e){
      e.printStackTrace();
    }
  }
}
