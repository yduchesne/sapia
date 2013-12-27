package org.sapia.soto.ubik.monitor.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;
import org.sapia.soto.NotFoundException;
import org.sapia.soto.SotoContainer;
import org.sapia.soto.freemarker.FreemarkerService;
import org.sapia.soto.ubik.monitor.Monitor;
import org.sapia.util.text.SystemContext;
import org.sapia.util.text.TemplateFactory;

import freemarker.template.Template;
import freemarker.template.TemplateException;

public class MonitorServlet extends HttpServlet{
  
  public static final String CONFIG_RESOURCE    = "config-resource";

  public static final String MONITOR_SERVICE_ID = "monitor-service-id";  
  
  private SotoContainer _cont = new SotoContainer();
  private Monitor _monitor;
  private Logger _log = Logger.getLogger(getClass());
  private FreemarkerService _freemarker;
  
  public void init(ServletConfig conf) throws ServletException {
    String sotoConfig = conf.getInitParameter(CONFIG_RESOURCE);
    String monitorId  = conf.getInitParameter(MONITOR_SERVICE_ID);
    
    if(sotoConfig == null){
      String msg = CONFIG_RESOURCE + " init parameter not set";
      _log.error(msg);
      throw new ServletException(msg);
    }
    if(monitorId == null){
      String msg = MONITOR_SERVICE_ID + " init parameter not set";
      _log.error(msg);
      throw new ServletException(msg);      
    }
    try{
      TemplateFactory fac = new TemplateFactory();
      sotoConfig = fac.parse(sotoConfig).render(new SystemContext());      
      _cont.load(sotoConfig);
    }catch(Exception e){
      _log.error(e);
      throw new ServletException("Could not load config " + sotoConfig, e);
    }
    try{
      _cont.start();
    }catch(Exception e){
      _log.error(e);      
      throw new ServletException("Could not start");
    }
    try{
      _monitor = (Monitor)_cont.lookup(monitorId);
      _freemarker = (FreemarkerService)_cont.lookup(FreemarkerService.class);
    }catch(NotFoundException e){
      _log.error(e);      
      throw new ServletException("Could not look up", e);
    }
  }
  
  public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
    res.setContentType("text/plain");
    Template tmpl = _freemarker.resolveTemplate("resource:/org/sapia/soto/ubik/monitor/server/templates/poll.ftl");
    PrintWriter pw = res.getWriter();
    try{
      Map root = new HashMap();
      root.put("Model", _monitor.getStatusForId(null));
      tmpl.process(root, pw);
      pw.flush();
      pw.close();
    }catch(TemplateException e){
      _log.error("Could not process template", e);
    }
  }

}
