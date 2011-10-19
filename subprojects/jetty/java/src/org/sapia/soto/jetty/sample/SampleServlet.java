package org.sapia.soto.jetty.sample;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.BasicConfigurator;
import org.sapia.soto.SotoContainer;

public class SampleServlet extends HttpServlet{
  
  protected void service(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
    PrintWriter pw = arg1.getWriter();
    pw.println("<html><body><h1>You have invoked a servlet!</h1>");
    Enumeration names = arg0.getHeaderNames();
    while(names.hasMoreElements()){
      String name = (String)names.nextElement();
      pw.println(name + ": " + arg0.getHeader(name) + "<br>");
    }
    pw.println("</body></html>");
    pw.flush();
    pw.close();
  }
  
  public static void main(String[] args) throws Exception{
    System.setProperty("DEBUG", "true");
    SotoContainer cont = new SotoContainer();
    cont.load(new File("etc/jetty.soto.xml"));
    cont.start();
    while(true){
      Thread.sleep(1000);
    }
  }
}