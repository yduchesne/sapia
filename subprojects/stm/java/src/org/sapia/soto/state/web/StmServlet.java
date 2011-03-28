/*
 * StmServlet.java
 *
 * Created on December 1, 2005, 1:53 PM
 *
 */
package org.sapia.soto.state.web;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.xml.transform.OutputKeys;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.Serializer;
import org.apache.xml.serialize.SerializerFactory;

import org.sapia.soto.state.Context;

/**
 * An instance of this class creates <code>WebContext</code> instances.
 *
 * @author yduchesne
 */
public class StmServlet extends AbstractStmServlet{
  
  /** Creates a new instance of StmServlet */
  public StmServlet() {
  }
  
  public Context createContext(HttpServletRequest req, HttpServletResponse res)
    throws IOException{
    

    Properties props = new Properties();
    props.setProperty(OutputKeys.INDENT, "yes");
    props.setProperty(OutputKeys.METHOD, "xml");

    OutputFormat format = new OutputFormat("xml", "UTF-8", true);
    OutputStream out = res.getOutputStream();
    Serializer ser = SerializerFactory.getSerializerFactory("xml")
        .makeSerializer(out, format);

    WebContext ctx = new WebContext(
        getContainer().toEnv(),
        ser.asContentHandler(), 
        req, 
        res);

    return ctx;
  }
  
}
