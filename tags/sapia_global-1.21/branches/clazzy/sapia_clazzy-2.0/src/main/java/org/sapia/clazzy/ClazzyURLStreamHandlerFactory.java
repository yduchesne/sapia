package org.sapia.clazzy;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * A <code>URLStreamHandlerFactory</code> implementation.
 * <p>
 * An instance of this class can be set on the <code>URL</code> class, by
 * calling the <code>setURLStreamHandlerFactory()</code> static method:
 * 
 * <pre>
 * ClazzyURLStreamHandlerFactory fac = new ClazzyURLStreamHandlerFactory();
 * URL.setURLStreamHandlerFactory(fac);
 * </pre>
 * 
 * <p>
 * The above approach is not recommended: the URL class does not allow setting
 * the factory more than once per VM. As a workaround, the JDK provides a mechanism
 * based on package and class naming conventions for factory implementations 
 * (see <a href="http://java.sun.com/developer/onlineTraining/protocolhandlers/">this link</a>).
 * <p>
 * Thus, using this framework following the above convention involves setting the following system
 * property:
 * <pre>
 * -Djava.protocol.handler.pkgs=org.sapia
 * </pre>
 * 
 * 
 * @author Yanick Duchesne
 * 
 * @see org.sapia.clazzy.ClazzyURLConnection
 * @see org.sapia.clazzy.loader.JarLoader
 * @see org.sapia.clazzy.JarClassLoader
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class ClazzyURLStreamHandlerFactory implements URLStreamHandlerFactory{
  
  public static final String PROTOCOL = "clazzy";
  
  private Map _handlers = new HashMap();
  
  public ClazzyURLStreamHandlerFactory(){
    _handlers.put("http", sun.net.www.protocol.http.Handler.class);
    _handlers.put("ftp", sun.net.www.protocol.ftp.Handler.class);    
    _handlers.put("gopher", sun.net.www.protocol.gopher.Handler.class);    
    _handlers.put("mailto", sun.net.www.protocol.mailto.Handler.class);    
    _handlers.put("file", sun.net.www.protocol.file.Handler.class);
    _handlers.put("jar", Handler.class);    
    _handlers.put(PROTOCOL, Handler.class);    
  }
  
  /**
   * @see java.net.URLStreamHandlerFactory#createURLStreamHandler(java.lang.String)
   */
  public URLStreamHandler createURLStreamHandler(String protocol) {
    int i = protocol.indexOf(':');
    if(i > -1){
      protocol = protocol.substring(0, i);
    }
    Class handlerClass = (Class)_handlers.get(protocol);
    if(handlerClass != null){
      try{
        return (URLStreamHandler)handlerClass.newInstance();
      }catch(Exception e){
        throw new RuntimeException("Could not create URLStreamHandler for: " + protocol, e);
      }
    }
    throw new IllegalArgumentException("Unknown protocol");
  }
  
}
