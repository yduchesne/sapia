package org.sapia.corus.http;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log.Logger;
import org.sapia.corus.admin.services.http.HttpContext;
import org.sapia.corus.admin.services.http.HttpExtension;
import org.sapia.corus.admin.services.http.HttpExtensionInfo;
import org.sapia.corus.core.CorusRuntime;
import org.sapia.corus.http.helpers.HomePageHelper;
import org.sapia.corus.http.helpers.NotFoundHelper;
import org.sapia.ubik.net.mplex.MultiplexSocketConnector;
import org.sapia.ubik.rmi.server.transport.TransportProvider;
import org.sapia.ubik.rmi.server.transport.socket.MultiplexSocketTransportProvider;
import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerServer;

/**
 * An instance of this class manages {@link HttpExtension}s.
 * 
 * @author yduchesne
 *
 */
public class HttpExtensionManager implements Container{
  
  public static final String FOOTER = "<hr><i>Corus HTTP Service - <a href=\"http://www.sapia-oss.org/projects/corus\">www.sapia-oss.org</a></i>";
  
  private MultiplexSocketConnector _httpConnector;  
  private ContainerServer _containerServer;
  private Logger _logger;
  private Map<HttpExtensionInfo, HttpExtension> _extensions = Collections.synchronizedMap(new HashMap<HttpExtensionInfo, HttpExtension>());
  
  public HttpExtensionManager(Logger logger) {
     _logger = logger;
  }
  
  public void init() throws Exception {
    TransportProvider provider = CorusRuntime.getTransport().getTransportProvider();
      
    if (!(provider instanceof MultiplexSocketTransportProvider)) {
      throw new IllegalStateException(
              "Could not initialize the http module - the transport provider is not multiplex [" + provider + "]");
    }
  }  
  
  public void start() throws Exception{
    _logger.info("Starting http extension manager");
    MultiplexSocketTransportProvider mplexProvider = (MultiplexSocketTransportProvider) CorusRuntime.getTransport().getTransportProvider();
    
    // Create the connector for HTTP post to /corus/ext context
    HttpStreamSelector selector = new HttpStreamSelector(null, null);
    _httpConnector = mplexProvider.createSocketConnector(selector);
    
    // Create the Simple server
    _containerServer = new ContainerServer(this, 10);
  }
  
  public void dispose(){
    if(_containerServer != null){
      try{
        _containerServer.stop();
      } catch (Exception e) {
        _logger.error("Error stopping the http server", e);
      }

    }
    if(_httpConnector != null){
      try {
        _httpConnector.close();
      } catch (IOException ioe) {
        _logger.error("Error closing the http connector", ioe);
      }
    }
  }
  
  /**
   * This method internally registers the given extension under the context
   * path that is provided. Then, the extension can be reached by typing
   * a URL of the following form in a browser:
   * 
   * <pre>&lt;http&gt;://&lt;corus_host&gt;:&lt;corus_port&gt;/corus/ext/&lt;contextPath&gt;</pre>
   * 
   * @param contextPath a context path.
   * @param ext a <code>HttpExtension</code>.
   */
  public void addHttpExtension(HttpExtension ext){
    HttpExtensionInfo info = ext.getInfo();
    String contextPath = info.getContextPath();
    String name = info.getName();    
    if(contextPath == null){
      throw new IllegalStateException("Context path not specified on extension info for: " + ext);
    }
    if(name == null){
      throw new IllegalStateException("Name not specified on extension info for: " + ext);
    }    
    if(!contextPath.startsWith("/")){
      contextPath = "/" + contextPath;
    }
    info.setContextPath(contextPath);
    if(_extensions.containsKey(info)){
      throw new IllegalStateException("Extension already bound under context path: " + contextPath);
    }
    _logger.debug("Adding HTTP extension under " + contextPath + ": " + ext);
    _extensions.put(info, ext);
  }
  
  @Override
  public void handle(Request req, Response res) {
    try{
      doHandle(req, res);
    }catch(Exception e){
      res.setCode(500);
      _logger.error("Could not process HTTP request", e);
    }
    
  }
  
  private void doHandle(Request req, Response res) throws Exception{

    if(req.getPath().getSegments().length == 0){
      synchronized(_extensions){
        HomePageHelper helper = new HomePageHelper(_extensions.keySet());
        helper.print(req, res);
      }
    }
    else{
      synchronized(_extensions){
  
        Iterator infos = _extensions.keySet().iterator();
        Path path = req.getPath();
        while(infos.hasNext()){
          HttpExtensionInfo info = (HttpExtensionInfo)infos.next();
          if(path.getSegments()[0].startsWith(info.getContextPath())){
            HttpExtension ext = (HttpExtension)_extensions.get(info);
            HttpContext ctx = new HttpContext();
            ctx.setRequest(req);
            ctx.setResponse(res);
            ctx.setContextPath(info.getContextPath());
            if(path.getPath().equals(info.getContextPath())){
              ctx.setPathInfo("");
            }
            else{
              ctx.setPathInfo(req.getPath().getPath().substring(info.getContextPath().length()));  
            }
            if(_logger.isDebugEnabled()){
              _logger.debug("Found extension for URI: " + path + "; path info = " + ctx.getPathInfo());
            }
            try{
              ext.process(ctx);
              return;
            }catch(FileNotFoundException e){
              _logger.error("URI not recognized: " + path);
              NotFoundHelper out = new NotFoundHelper();
              out.print(req, res);            
            }catch(Exception e){
              _logger.error("Error caught while handling request", e);            
              res.setCode(500);
              try{
                res.getOutputStream().close();
              }catch(IOException e2){}
              return;
            }finally{
              try{
                if(!res.isCommitted()){
                  res.commit();
                }
              }catch(IOException e){}
            }
          }
        }
      }
    }
    _logger.error("Could not find extension for URI " + req.getPath());
    NotFoundHelper out = new NotFoundHelper();
    out.print(req, res);    
  }
  
  
}
