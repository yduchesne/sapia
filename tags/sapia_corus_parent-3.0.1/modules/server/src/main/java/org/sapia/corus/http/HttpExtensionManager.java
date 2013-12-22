package org.sapia.corus.http;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log.Logger;
import org.sapia.corus.client.services.http.HttpContext;
import org.sapia.corus.client.services.http.HttpExtension;
import org.sapia.corus.client.services.http.HttpExtensionInfo;
import org.sapia.corus.core.ServerContext;
import org.sapia.corus.http.helpers.HomePageHelper;
import org.sapia.corus.http.helpers.NotFoundHelper;
import org.sapia.ubik.net.mplex.MultiplexSocketConnector;
import org.sapia.ubik.net.mplex.ServerSocketAdapter;
import org.sapia.ubik.rmi.server.transport.TransportProvider;
import org.sapia.ubik.rmi.server.transport.socket.MultiplexSocketHelper;
import org.sapia.ubik.rmi.server.transport.socket.MultiplexSocketTransportProvider;

import simple.http.ProtocolHandler;
import simple.http.Request;
import simple.http.Response;
import simple.http.connect.Connection;
import simple.http.connect.ConnectionFactory;
import simple.util.net.Path;

/**
 * An instance of this class manages {@link HttpExtension}s.
 * 
 * @author yduchesne
 *
 */
public class HttpExtensionManager implements ProtocolHandler{
  
  public static final String FOOTER = "<hr><i>Corus HTTP Service - <a href=\"http://www.sapia-oss.org/projects/corus\">www.sapia-oss.org</a></i>";
  
  private MultiplexSocketConnector httpConnector;  
  private Connection 							 connection;
  private Logger 									 logger;
  private ServerContext 					 context;
  private Map<HttpExtensionInfo, HttpExtension> extensions = Collections.synchronizedMap(new HashMap<HttpExtensionInfo, HttpExtension>());

  public HttpExtensionManager(Logger logger, ServerContext context) {
     this.logger  = logger;
     this.context = context;
  }
  
  public void init() throws Exception {
    TransportProvider provider = this.context.getTransport().getTransportProvider();
      
    if (!(provider instanceof MultiplexSocketTransportProvider)) {
      throw new IllegalStateException(
              "Could not initialize the http module - the transport provider is not multiplex [" + provider + "]");
    }
  }  
  
  public void start() throws Exception{
    logger.info("Starting http extension manager");
    
    // Create the connector for HTTP post to /corus/ext context
    HttpStreamSelector selector = new HttpStreamSelector(null, null);
    httpConnector = MultiplexSocketHelper.createSocketConnector(selector);
    
    connection = ConnectionFactory.getConnection(this);
    connection.connect(new ServerSocketAdapter(httpConnector));
  }
  
  public void dispose(){
    if(httpConnector != null){
      try {
        httpConnector.close();
      } catch (IOException ioe) {
        logger.error("Error closing the http connector", ioe);
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
   * @param ext a {@link HttpExtension}.
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
    if(extensions.containsKey(info)){
      throw new IllegalStateException("Extension already bound under context path: " + contextPath);
    }
    logger.debug("Adding HTTP extension under " + contextPath + ": " + ext);
    extensions.put(info, ext);
  }
  
  @Override
  public void handle(Request req, Response res) {
    try{
      doHandle(req, res);
    }catch(Exception e){
      res.setCode(500);
      logger.error("Could not process HTTP request", e);
    }
    
  }
  
  private void doHandle(Request req, Response res) throws Exception{

    if(req.getPath().getSegments().length == 0){
      synchronized(extensions){
        HomePageHelper helper = new HomePageHelper(context, extensions.keySet());
        helper.print(req, res);
      }
    } else {
      synchronized(extensions){
        Iterator<HttpExtensionInfo> infos = extensions.keySet().iterator();
        Path path = req.getPath();
        if(logger.isDebugEnabled()){
          logger.debug(String.format("Trying to find HTTP extension for %s", path.getPath()));
        }
        while(infos.hasNext()){
          HttpExtensionInfo info = infos.next();
          if(path.getPath().startsWith(info.getContextPath())){
            HttpExtension ext = (HttpExtension)extensions.get(info);
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
            if(logger.isDebugEnabled()){
              logger.debug("Found extension for URI: " + path + "; path info = " + ctx.getPathInfo());
            }
            try{
              ext.process(ctx);
              return;
            }catch(FileNotFoundException e){
              logger.error("URI not recognized: " + path);
              NotFoundHelper out = new NotFoundHelper();
              out.print(req, res);            
            }catch(Exception e){
              logger.error("Error caught while handling request", e);            
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
    logger.error("Could not find extension for URI " + req.getPath());
    NotFoundHelper out = new NotFoundHelper();
    out.print(req, res);    
  }
}