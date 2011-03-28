package org.sapia.soto.ldap.jaas;

import java.security.Principal;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPEntry;
import netscape.ldap.LDAPException;

/**
 * This class implements a JAAS {@link javax.security.auth.spi.LoginModule}.
 * It uses the Netscape LDAP SDK to perform authentication with a remote LDAP server.
 * <p>
 * An instance of this class needs configuration that must be provided through a JAAS login
 * module configuration. An example of such a configuration for this module implementation
 * is as follows:
 * <p>
 * <code>
 * JaasSample {
 *   org.sapia.soto.ldap.jaas.LDAPLoginModule required 
 *   host="localhost" 
 *   port="10389" 
 *   protocolVersion="3" 
 *   dn="ou=system";
 *   principalFactory=org.sapia.soto.ldap.jaas.DefaultPrincipalFactory
 * };
 * </code>
 * <p>
 * The module configuration follows the standard JAAS login module configuration format
 * (better described <a href="http://java.sun.com/j2se/1.4.2/docs/guide/security/jgss/tutorials/LoginConfigFile.html">here</a>).
 * <p>
 * This module takes the following options:
 * <ul>
 *   <li><b>host</b>: the host of the remote LDAP server to connect to.
 *   <li><b>port</b>: the port of the remote LDAP server to connect to.
 *   <li><b>protocolVersion</b>: the version of the LDAP protocol to use when connecting to the LDAP server 
 *   (must be 2 or 3).
 *   <li><b>dn</b>: the distinguished name under which to perform the authentication operation (e.g.: "ou=sales").
 *   <li><b>principalFactory</b>: the {@link org.sapia.soto.ldap.jaas.PrincipalFactory} implementation to use when
 *   converting an LDAP entry to a {@link java.security.Principal} - defaults to {@link org.sapia.soto.ldap.jaas.DefaultPrincipalFactory}.
 * </ul>
 * <p>
 * An instance of this class passes a {@link javax.security.auth.callback.NameCallback} and a {@link javax.security.auth.callback.PasswordCallback}
 * to the {@link javax.security.auth.callback.CallbackHandler} with which it is initialized. It therefore expects the handler to appropriately
 * request the end-user's username and password in order to use these when authentifying with the LDAP server.
 * <p>
 * An instance of this class authentifies itself with the provided credentials, and then reads the LDAP entry corresponding to
 * the given username. It uses the specified {@link org.sapia.soto.ldap.jaas.PrincipalFactory} implementation to create a
 * <code>Principal</code> from the entry. That principal is then added to the {@link javax.security.auth.Subject} with which this
 * instance was initialized (this is done in the {@link #commit()} method).
 * <p>
 * By default, if the <code>principalFactory</code> option has not been set, a {@link org.sapia.soto.ldap.jaas.DefaultPrincipalFactory}
 * is used. That factory creates an instance of {@link org.sapia.soto.ldap.jaas.LDAPPrincipal}. 
 * 
 * @author yduchesne
 *
 */
public class LDAPLoginModule implements LoginModule {

  public static final String PROP_LDAP_PORT = "port";

  public static final String PROP_LDAP_HOST = "host";
  
  public static final String PROP_LDAP_DEBUG = "debug";

  public static final String PROP_LDAP_VERSION = "protocolVersion";
  
  public static final String PROP_LDAP_DN = "dn";
  
  public static final String PROP_LDAP_PRINCIPAL_FACTORY = "principalFactory";  

  private Subject _subject;

  private CallbackHandler _handler;

  // user data
  private String _username, _password;

  private Principal _principal;

  // the authentication status
  private boolean _succeeded = false;
  private boolean _commitSucceeded = false;

  // LDAP connection settings
  private String _host, _baseDN;
  private int _port, _version;
  private PrincipalFactory _principalFactory;
  private boolean _debug;

  public void initialize(Subject subject, CallbackHandler handler,
      Map sharedState, Map options) {
    _subject = subject;
    _handler = handler;
    _host = assertOption(PROP_LDAP_HOST, options);
    _baseDN = assertOption(PROP_LDAP_DN, options);
    _port = Integer.parseInt(assertOption(PROP_LDAP_PORT, options));
    _version = Integer.parseInt(assertOption(PROP_LDAP_VERSION, options));
    
    String debug = (String)options.get(PROP_LDAP_DEBUG);
    if(debug != null){
      _debug = debug.equalsIgnoreCase("true");
    }
    
    String factory = (String)options.get(PROP_LDAP_PRINCIPAL_FACTORY);
    if(factory != null){
      try{
        _principalFactory = (PrincipalFactory)Class.forName(factory).newInstance();
      }catch(Exception e){
        throw new IllegalStateException("Could not create PrincipalFactory instance: " + e.getMessage());
      }
    }
    else{
      _principalFactory = new DefaultPrincipalFactory();
    }
    
  }

  public boolean login() throws LoginException {
    NameCallback cbUsername = new NameCallback("USERNAME");
    PasswordCallback cbPassword = new PasswordCallback("PASSWORD", false);
    Callback[] callbacks = new Callback[] { cbUsername, cbPassword };
    try {
      _handler.handle(callbacks);
      _username = ((NameCallback) callbacks[0]).getName();
      char[] passwordChars = ((PasswordCallback) callbacks[1]).getPassword();
      if (passwordChars == null)
        passwordChars = new char[0];
      _password = new String(passwordChars, 0, passwordChars.length);

    } catch (java.io.IOException ioe) {
      throw new LoginException("Could not perform login - " + ioe.getMessage());
    } catch (UnsupportedCallbackException uce) {
      throw new LoginException("Error: " + uce.getCallback().toString()
          + " not available to authenticate user");
    }
    LDAPConnection conn = new LDAPConnection();
    try {
      conn.connect(_host, _port);
    } catch (LDAPException e) {
      throw new LoginException("Could not connect to LDAP host: " + _host
          + " - " + e.getLDAPErrorMessage() + "(" + e.getLDAPResultCode() + ")");
    }
    try {
      String full = new StringBuffer("uid=")
        .append(_username)
        .append(",")
        .append(_baseDN).toString();
      conn.authenticate(_version, full, _password);
      LDAPEntry entry = conn.read(full, new String[]{"*"});
      if(entry == null){
        _succeeded = false;
        throw new FailedLoginException(_username);
      }
      else{
        _principal = _principalFactory.createPrincipalFrom(_username, entry);
        _succeeded = true;
      }
      return _succeeded;
    } catch (LDAPException e) {
      _succeeded = false;
      debug(e);
      throw new FailedLoginException(_username);      
    } finally {
      try {
        conn.disconnect();
      } catch (Exception e) {
        // noop
      }
    }
  }

  public boolean commit() throws LoginException {
    if (!_succeeded) {
      return false;
    } else {
      if (!_subject.getPrincipals().contains(_principal)) {
        _subject.getPrincipals().add(_principal);
      }

      // in any case, clean out state
      _username = null;
      _password = null;

      _commitSucceeded = true;
      return true;
    }
  }

  public boolean abort() throws LoginException {
    if (_succeeded == false) {
      return false;
    } else if (_succeeded && !_commitSucceeded) {
      // login succeeded but overall authentication failed
      _succeeded = false;
      _username = null;
      _password = null;
      _principal = null;
    } else {
      // overall authentication succeeded and commit succeeded,
      // but someone else's commit failed
      logout();
    }
    return true;
  }

  public boolean logout() throws LoginException {
    _subject.getPrincipals().remove(_principal);
    _succeeded = false;
    _commitSucceeded = false;
    _username = null;
    _password = null;
    _principal = null;
    return true;
  }

  private String assertOption(String key, Map options) {
    String val = (String) options.get(key);
    if (val == null) {
      throw new IllegalStateException("Option not specified: " + key);
    }
    else{
      return val;
    }
  }
  
  private void debug(String msg){
    if(_debug){
      System.out.println(getClass() + " >> " + msg);
    }
  }
  
  private void debug(Exception e){
    if(_debug){
      System.out.println(getClass() + " >> " + e.getMessage());
      e.printStackTrace();
    }
  }  

}
