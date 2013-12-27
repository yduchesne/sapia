package org.sapia.soto.state.cocoon.standalone;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.OutputKeys;

import org.apache.cocoon.environment.http.HttpEnvironment;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.Serializer;
import org.apache.xml.serialize.SerializerFactory;
import org.sapia.soto.state.Context;
import org.sapia.soto.state.cocoon.CocoonContext;
import org.sapia.soto.state.web.AbstractStmServlet;

/**
 * This servlet can be used outside of the Cocoon environment. It loads a
 * STM-based application and manages its lifecycle. A <code>SotoContainer</code>
 * is internally kept.
 *
 * @see org.sapia.soto.state.cocoon.CocoonContext
 * 
 * @author Yanick Duchesne
 *         <dl>
 *         <dt><b>Copyright: </b>
 *         <dd>Copyright &#169; 2002-2003 <a
 *         href="http://www.sapia-oss.org">Sapia Open Source Software </a>. All
 *         Rights Reserved.</dd>
 *         </dt>
 *         <dt><b>License: </b>
 *         <dd>Read the license.txt file of the jar or visit the <a
 *         href="http://www.sapia-oss.org/license.html">license page </a> at the
 *         Sapia OSS web site</dd>
 *         </dt>
 *         </dl>
 */
public class StandaloneServlet extends AbstractStmServlet{

  public StandaloneServlet() {
  }

  /**
   * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
   */
  public void init(ServletConfig conf) throws ServletException {
    getContainer().getResourceHandlers().append(new CocoonResourceHandler(conf.getServletContext()));    
    super.init(conf);
  }

  protected Context createContext(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    
    HttpEnvironment env = CocoonObjectFactory.getEnvironment(req, res, _ctx,
        null, null);

    Properties props = new Properties();
    props.setProperty(OutputKeys.INDENT, "yes");
    props.setProperty(OutputKeys.METHOD, "xml");

    OutputFormat format = new OutputFormat("xml", "UTF-8", true);
    OutputStream out = res.getOutputStream();
    Serializer ser = SerializerFactory.getSerializerFactory("xml")
        .makeSerializer(out, format);

    CocoonContext ctx = new CocoonContext(getContainer().toEnv(),
        ser.asContentHandler(), env.getObjectModel());

    return ctx;
  }
}
