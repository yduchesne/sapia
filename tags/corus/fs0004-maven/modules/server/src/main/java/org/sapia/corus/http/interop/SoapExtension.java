package org.sapia.corus.http.interop;

import java.util.List;

import org.apache.log.Hierarchy;
import org.apache.log.Logger;
import org.sapia.corus.admin.services.http.HttpContext;
import org.sapia.corus.admin.services.http.HttpExtension;
import org.sapia.corus.admin.services.http.HttpExtensionInfo;
import org.sapia.corus.admin.services.processor.Processor;
import org.sapia.corus.core.ServerContext;
import org.sapia.corus.exceptions.CorusException;
import org.sapia.corus.interop.ConfirmShutdown;
import org.sapia.corus.interop.Poll;
import org.sapia.corus.interop.Process;
import org.sapia.corus.interop.Restart;
import org.sapia.corus.interop.Status;
import org.sapia.corus.interop.helpers.RequestListener;
import org.sapia.corus.interop.helpers.ServerStatelessSoapStreamHelper;


/**
 * This extension implement the server side of the Corus interoperability spec. 
 * It can be accessed with an URL similar as the following one:
 * <p>
 * <pre>http://localhost:33000/interoap/soap</pre>
 * 
 * @author Yanick Duchesne
 */
public class SoapExtension implements HttpExtension, RequestListener {
  
  public static final String HTTP_INTEROP_SOAP_CONTEXT = "interop/soap";  
  
  private ServerStatelessSoapStreamHelper _helper;
  private Logger                          _logger;
  private ServerContext                   _serverContext;
  
  public SoapExtension(ServerContext serverContext) throws CorusException{
    _helper = new ServerStatelessSoapStreamHelper(this, "corus.server");
    _logger = Hierarchy.getDefaultHierarchy().getLoggerFor(getClass().getName());
    _serverContext = serverContext;
  }
  
  public HttpExtensionInfo getInfo() {
    HttpExtensionInfo info = new HttpExtensionInfo();
    info.setContextPath(HTTP_INTEROP_SOAP_CONTEXT);
    info.setName("Corus Interop Link");
    info.setDescription("Call-back invoked by Corus processes, as described in the Corus Interoperability Specification");
    return info;
  }
  
  public void process(HttpContext ctx) throws Exception {
    ctx.getResponse().set("Content-Type", "text/xml");
    try {
      _helper.processRequest(ctx.getRequest().getInputStream(), ctx.getResponse().getOutputStream());
    } catch (Exception e) {
      throw new Exception("Error processing request", e);
    }finally{
      ctx.getResponse().commit();
    }
  }
  
  public synchronized void onConfirmShutdown(Process proc,
                                             ConfirmShutdown confirm)
    throws Exception {
    if (proc.getCorusPid() == null) {
      throw new CorusException("'corusPid' not specified in header");
    }
    
    _logger.info("Process: " + proc + " confirming shutdown");
    _serverContext.lookup(Processor.class).getProcess(proc.getCorusPid()).confirmKilled();
  }
  
  public synchronized List onPoll(Process proc, Poll poll)
  throws Exception {
    if (proc.getCorusPid() == null) {
      throw new CorusException("'corusPid' not specified in header");
    }
    
    _logger.debug("Process: " + proc + " polling...");
    
    return _serverContext.lookup(Processor.class).getProcess(proc.getCorusPid()).poll();
  }
  
  public synchronized void onRestart(Process proc, Restart restart)
  throws Exception {
    if (proc.getCorusPid() == null) {
      throw new CorusException("'corusPid' not specified in header");
    }
    
    _logger.debug("Process requested a restart: " + proc);
    _serverContext.lookup(Processor.class).restart(proc.getCorusPid());
  }
  
  public synchronized List onStatus(Process proc, Status stat)
  throws Exception {
    if (proc.getCorusPid() == null) {
      throw new CorusException("'corusPid' not specified in header");
    }
    
    _logger.debug("Status received for " + proc);
    
    return _serverContext.lookup(Processor.class).getProcess(proc.getCorusPid()).status(stat);
  }
  
}
