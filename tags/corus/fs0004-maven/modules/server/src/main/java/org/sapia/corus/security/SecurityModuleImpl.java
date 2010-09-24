package org.sapia.corus.security;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.sapia.corus.client.annotations.Bind;
import org.sapia.corus.client.services.security.CorusSecurityException;
import org.sapia.corus.client.services.security.SecurityModule;
import org.sapia.corus.core.ModuleHelper;
import org.sapia.corus.util.UriPattern;
import org.sapia.ubik.net.TCPAddress;
import org.sapia.ubik.rmi.interceptor.Interceptor;
import org.sapia.ubik.rmi.server.Hub;
import org.sapia.ubik.rmi.server.invocation.ServerPreInvokeEvent;

/**
 * Implements the {@link SecurityModule} interface.
 * 
 * @author Yanick Duchesne
 */
@Bind(moduleInterface=SecurityModule.class)
public class SecurityModuleImpl extends ModuleHelper implements SecurityModule, Interceptor{

  private static final String LOCALHOST;
  static {
    String hostAddress = null;
    try {
      hostAddress = InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException uhe) {
      System.err.println("Unable to get the localhost address");
      uhe.printStackTrace();
    } finally {
      LOCALHOST = hostAddress;
    }
  }
  
  private List<UriPattern> _allowedPatterns = new ArrayList<UriPattern>();
  private List<UriPattern> _deniedPatterns = new ArrayList<UriPattern>();
  
  private boolean _isRunning = false;
  
  /**
   * @see org.sapia.corus.client.Module#getRoleName()
   */
  public String getRoleName() {
    return ROLE;
  }
  
  public boolean isRunning() {
    return _isRunning;
  }
  
  /**
   * Set the pattern list of the allowed hosts that can connect to
   * this corus server.
   * 
   * @param patternList The pattern list of allowed hosts.
   */  
  public synchronized void setAllowedHostPatterns(String patternList) {
    _allowedPatterns.clear();
    
    if (patternList != null && (patternList = patternList.trim()).length() > 0) {
      String[] patterns = StringUtils.split(patternList, ',');
      for (int i = 0; i < patterns.length; i++) {
        if (patterns[i].trim().equals("localhost")) {
          _allowedPatterns.add(UriPattern.parse(LOCALHOST));
        } else {
          _allowedPatterns.add(UriPattern.parse(patterns[i].trim()));
        }
      }
    }
  }
  
  /**
   * Set the pattern list of the denied hosts that can't connect to
   * this corus server.
   * 
   * @param patternList The pattern list of denied hosts.
   */  
  public synchronized void setDeniedHostPatterns(String patternList) {
    _deniedPatterns.clear();
    
    if (patternList != null && (patternList = patternList.trim()).length() > 0) {
      String[] patterns = StringUtils.split(patternList, ',');
      for (int i = 0; i < patterns.length; i++) {
        if (patterns[i].trim().equals("localhost")) {
          _deniedPatterns.add(UriPattern.parse(LOCALHOST));
        } else {
          _deniedPatterns.add(UriPattern.parse(patterns[i].trim()));
        }
      }
    }
  }
  
  /**
   * @see org.sapia.corus.client.services.soto.Service#init()
   */
  public void init() throws Exception {
    logger().info("Initializing the security module");
    Hub.serverRuntime.addInterceptor(ServerPreInvokeEvent.class, this);
  }
  
  /**
   * @see org.sapia.corus.core.ModuleHelper#start()
   */
  public void start() throws Exception {
    logger().info("Starting the security module");
    _isRunning = true;
  }
  
  /**
   * @see org.sapia.corus.client.services.soto.Service#dispose()
   */
  public void dispose() {
    logger().info("Stopping the security module");
    _isRunning = false;
  }
  
  /**
   * 
   * @param evt
   */
  public void onServerPreInvokeEvent(ServerPreInvokeEvent evt) {
    if (!_isRunning) {
      throw new IllegalStateException("This security module is currently not running");
    }
    
    TCPAddress addr = (TCPAddress) evt.getInvokeCommand().getConnection().getServerAddress();
    if (!isMatch(addr.getHost())) {
      logger().error("Security breach; could not execute: " + evt.getInvokeCommand().getMethodName());      
      throw new CorusSecurityException("Host does not have access to corus server: " + addr);
    }
  }
  
  protected synchronized boolean isMatch(String hostAddr) {
    if (_allowedPatterns.size() == 0 && _deniedPatterns.size() == 0) {
      return true;
    }

    boolean isMatching = (_allowedPatterns.size() == 0);
    for (Iterator<UriPattern> it = _allowedPatterns.iterator(); !isMatching && it.hasNext(); ) {
      UriPattern pattern = (UriPattern) it.next();
      if (pattern.matches(hostAddr)) {
        isMatching = true;
      }
    }
    
    for (Iterator<UriPattern> it = _deniedPatterns.iterator(); isMatching && it.hasNext(); ) {
      UriPattern pattern = (UriPattern) it.next();
      if (pattern.matches(hostAddr)) {
        isMatching = false;
      }
    }
    return isMatching;
  }  
}
