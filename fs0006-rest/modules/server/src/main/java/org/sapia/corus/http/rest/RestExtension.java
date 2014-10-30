package org.sapia.corus.http.rest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Set;

import org.apache.log.Hierarchy;
import org.apache.log.Logger;
import org.sapia.corus.client.cli.DefaultClientFileSystem;
import org.sapia.corus.client.common.rest.RestRequest;
import org.sapia.corus.client.common.rest.Value;
import org.sapia.corus.client.facade.CorusConnector;
import org.sapia.corus.client.facade.CorusConnectorImpl;
import org.sapia.corus.client.rest.RequestContext;
import org.sapia.corus.client.rest.RestContainer;
import org.sapia.corus.client.rest.RestResponseFacade;
import org.sapia.corus.client.services.http.HttpContext;
import org.sapia.corus.client.services.http.HttpExtension;
import org.sapia.corus.client.services.http.HttpExtensionInfo;
import org.sapia.corus.client.services.http.HttpResponseFacade;
import org.sapia.corus.core.ServerContext;

/**
 * Entry point into the RESTful API.
 * 
 * @author yduchesne
 *
 */
public class RestExtension implements HttpExtension {
  
  private static final int BYTES_PER_CHAR = 4;
  private static HttpExtensionInfo INFO = HttpExtensionInfo.newInstance()
   .setContextPath("/rest")
   .setDescription("Handles REST calls")
   .setName("Corus REST API");

  private Logger         logger     = Hierarchy.getDefaultHierarchy().getLoggerFor(RestExtension.class.getName());
  private CorusConnector connector;
  private RestContainer  container;
  
  public RestExtension(ServerContext serverContext) {
    connector = new CorusConnectorImpl(
        new RestConnectionContext(
            serverContext.getCorus(), 
            new DefaultClientFileSystem(new File(serverContext.getHomeDir()))
        )
    );
    
    container = RestContainer.Builder.newInstance().buildDefaultInstance();
  }
  
  @Override
  public HttpExtensionInfo getInfo() {
    return INFO;
  }
  
  @Override
  public void process(final HttpContext ctx) throws Exception, FileNotFoundException {
    ServerRestRequest request = new ServerRestRequest(ctx);
    String            payload = null;
    
    if (logger.isDebugEnabled()) {
      logger.debug(String.format("Receiving REST call %s", ctx.getPathInfo()));
      logger.debug(String.format("=> Accept header values..: %s", ctx.getRequest().getAccepts()));
      logger.debug(String.format("=> Content type..........: %s", ctx.getRequest().getContentType()));
    }
    
    try {
      payload = container.invoke(new RequestContext(request, connector), new RestResponseFacade() {
        @Override
        public void setStatus(int statusCode) {
          ctx.getResponse().setStatusCode(statusCode);
        }
        
        @Override
        public void setContentType(String contentType) {
          ctx.getResponse().setContentType(contentType);
        }
        
        @Override
        public void setStatusMessage(String msg) {
          ctx.getResponse().setStatusMessage(msg);
        }
      });
      
      if (logger.isDebugEnabled()) {
        logger.debug(String.format("Got response payload for REST call %s:", ctx.getPathInfo()));
        logger.debug(payload);
      }
      
      sendResponse(ctx, payload);
      
    } catch (FileNotFoundException e) {
      logger.error("Error performing RESTful call (resource not found): " + ctx.getPathInfo());
      ctx.getResponse().setStatusCode(HttpResponseFacade.STATUS_NOT_FOUND);
      ctx.getResponse().setStatusMessage(e.getMessage());
      ctx.getResponse().getOutputStream().close();
      ctx.getResponse().commit();
    } catch (Throwable e) {
      logger.error("Error performing RESTful call: " + ctx.getPathInfo(), e);
      ctx.getResponse().setStatusCode(HttpResponseFacade.STATUS_SERVER_ERROR);
      ctx.getResponse().setStatusMessage(e.getMessage());
      ctx.getResponse().getOutputStream().close();
      ctx.getResponse().commit();
    }
  }
  
  private void sendResponse(HttpContext ctx, String payload) throws IOException {
    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ctx.getResponse().getOutputStream()));
    ctx.getResponse().setContentLength(payload.length() * BYTES_PER_CHAR);
    try {
      ctx.getResponse().setStatusCode(HttpResponseFacade.STATUS_OK);
      writer.write(payload, 0, payload.length());
      writer.flush();
    } finally {
      writer.flush();
      writer.close();
      ctx.getResponse().commit();
    }
    
  }
  
  // ==========================================================================
  
  public class ServerRestRequest implements RestRequest {
    
    private HttpContext delegate;
    
    ServerRestRequest(HttpContext delegate) {
      this.delegate = delegate;
    }
    
    @Override
    public String getContentType() {
      return delegate.getRequest().getContentType();
    }
    
    @Override
    public Set<String> getAccepts() {
      return delegate.getRequest().getAccepts();
    }
    
    @Override
    public String getMethod() {
      return delegate.getRequest().getMethod();
    }
    
    @Override
    public String getPath() {
      return delegate.getPathInfo();
    }
    
    @Override
    public Value getValue(String name) {
      String value = delegate.getRequest().getParameter(name);
      if (value == null) {
        value = delegate.getRequest().getHeader(name);
      }
      return new Value(name, value);
    }
    
    @Override
    public Value getValue(String name, String defaultVal) {
      String value = delegate.getRequest().getParameter(name);
      if (value == null) {
        value = delegate.getRequest().getHeader(name);
      }
      if (value == null) {
        value = defaultVal;
      }
      return new Value(name, value);
    }
    
  }

}
