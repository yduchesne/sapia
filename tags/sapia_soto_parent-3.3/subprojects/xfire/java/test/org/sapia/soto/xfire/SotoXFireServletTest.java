package org.sapia.soto.xfire;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.binding.ObjectServiceFactory;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;
import org.sapia.soto.util.Utils;
import org.sapia.soto.xfire.sample.HelloService;
import org.sapia.soto.xfire.sample.HelloServiceImpl;

import junit.framework.TestCase;

public class SotoXFireServletTest extends TestCase {

  private Server _server;
  
  public SotoXFireServletTest(String arg0) {
    super(arg0);
  }

  protected void setUp() throws Exception {
    _server = new Server(8191);
    WebAppContext ctx = new WebAppContext();
    ctx.setContextPath("/soap");
    ctx.setWar(System.getProperty("user.dir") + "/webapp");
    _server.addHandler(ctx);
    ctx.setParentLoaderPriority(true);
    _server.start();
  }
  
  protected void tearDown() throws Exception {
    if(_server != null && _server.isStarted()){
      try{
        _server.stop();
      }catch(InterruptedException e){}
    }
  }  
  
  public void testRequest() throws Exception{
    ObjectServiceFactory serviceFactory = new ObjectServiceFactory();
    Service serviceModel = serviceFactory.create(HelloService.class);
    
    XFireProxyFactory proxyFactory = new XFireProxyFactory();
    
    HelloService service = (HelloService) proxyFactory.create(serviceModel, "http://localhost:8191/soap/HelloService");
    assertEquals(HelloServiceImpl.GREETING, service.getGreeting("test", "test"));
    service = (HelloService) proxyFactory.create(serviceModel, "http://localhost:8191/soap/HelloWebService");
    assertEquals(HelloServiceImpl.GREETING, service.getGreeting("test", "test"));    
  }
  
  public void testWsdl() throws Exception{
    ObjectServiceFactory serviceFactory = new ObjectServiceFactory();
    Service serviceModel = serviceFactory.create(HelloService.class);
    
    XFireProxyFactory proxyFactory = new XFireProxyFactory();
    
    URL url = new URL("http://localhost:8191/soap/HelloWebService?wsdl");
    InputStream is = url.openStream();
    String wsdl = Utils.textStreamToString(url.openStream());
    is.close();
    System.out.println(wsdl);
  }  
}
