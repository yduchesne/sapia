package org.sapia.corus.http.helpers;

import java.io.IOException;

import java.io.PrintStream;

import org.sapia.corus.client.services.http.HttpRequestFacade;
import org.sapia.corus.client.services.http.HttpResponseFacade;
import org.sapia.corus.http.HttpExtensionManager;

/**
 * Generates HTML content for the 404 error page.
 * 
 * @author yduchesne
 *
 */
public class NotFoundHelper implements OutputHelper {
  
  public void print(HttpRequestFacade req, HttpResponseFacade res) throws Exception{
    try{
      res.setHeader("Content-Type", "text/html");
      res.setStatusCode(HttpResponseFacade.STATUS_NOT_FOUND);
      PrintStream ps = new PrintStream(res.getOutputStream());
      ps.println("<html><body><h1>404 - NOT FOUND</h1> " + HttpExtensionManager.FOOTER + "</body></html>");
      ps.flush();
      ps.close();
      res.commit();
    }catch(IOException e){
      try{
        res.getOutputStream().close();
        res.commit();
      }catch(IOException e2){}
    }    
  }
}
