package org.sapia.soto.xfire;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.servlet.FilterChain;
import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * This filter can be configured in order to generate a HTML document
 * out of the WSDL of given web services. It works as follows:
 * 
 * <ul>
 *   <li>Intercepts all requests of the form <code>http://host:port/webservice?html</code>.
 *   <li>Forwards the request to the URI corresponding to <code>http://host:port/webservice?wsdl</code>:
 *   <li>Passes the resulting output to a stylesheet that transforms the WSDL into a developer-friendly HTML page.
 * </ul>
 * 
 * <p>
 * The XSL stylesheet that is used by default is stored in this class' classpath, as a resource. That resource can
 * be changed by setting the <b>xsl-resource</b> initialization parameter of this instance (in the web.xml file). The
 * value of the parameter must correspond to the full path of the custom stylesheet to use, in the classpath (for 
 * example: <code>org/acme/ws/WsdlToHtml.xsl</code>).
 *  
 * @author yduchesne
 *
 */
public class WsdlToHtmlFilter implements Filter{

  public static final String XSL_RESOURCE      = "xsl-resource";
  public static final String WSDL_PARAM        = "wsdl";  
  public static final String HTML_PARAM        = "html";
  public static final String DEFAULT_XSL_RESOURCE = WsdlToHtmlFilter.class.getName().replace(".", "/")+".xsl";  
  
  private String _resource = null;
  private Transformer _xsl;
  private FilterConfig _conf;
  
  public void init(FilterConfig conf) throws ServletException {
    _resource = conf.getInitParameter(XSL_RESOURCE);
    _conf = conf;
    if(_resource == null){
      _resource = DEFAULT_XSL_RESOURCE;
    }
    InputStream stream = getClass().getClassLoader().getResourceAsStream(_resource);
    if(stream == null){
      String msg = "Could not find resource: " + _resource;
      conf.getServletContext().log(msg);
      throw new ServletException(msg);
    }
    try{
      StreamSource src = new StreamSource(stream);
      _xsl = TransformerFactory.newInstance().newTransformer(src);
    }catch(TransformerConfigurationException e){
       String msg = "Could not instantiate transformer";
       conf.getServletContext().log(msg, e);
       //throw new ServletException(msg, e);
    }catch(Exception e){
      String msg = "Could not compile xsl";
      conf.getServletContext().log(msg, e);
      //throw new ServletException(msg, e);
      
    }finally{
      try {
        stream.close();
      } catch (Exception e) {
        // noop
      }
    }
  }
  
  public void destroy() {
  }
  
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
    if(req.getParameter(HTML_PARAM) != null && (res instanceof HttpServletResponse)){
      HttpServletRequest httpReq = (HttpServletRequest)req;
      WsdlServletResponseWrapper resWrapper = new WsdlServletResponseWrapper((HttpServletResponse)res);
      WsdlServletRequestWrapper reqWrapper = new WsdlServletRequestWrapper(httpReq); 
      res = resWrapper;
      req = reqWrapper;
    }
    chain.doFilter(req, res);
  }
 
  ///// Response wrapper
  
  public class WsdlServletResponseWrapper extends HttpServletResponseWrapper{
    
    private WsdlOutputStream _out;
    private PrintWriter _pw;
    
    public WsdlServletResponseWrapper(HttpServletResponse resp) {
      super(resp);
      _out = new WsdlOutputStream(resp);
      _pw = new PrintWriter(new OutputStreamWriter(_out));
    }
    
    public ServletOutputStream getOutputStream() throws IOException {
      return _out;
    }
    
    public PrintWriter getWriter() throws IOException {
      return _pw; 
    }
    
  }
  
  ///// Request wrapper
  
  public class WsdlServletRequestWrapper extends HttpServletRequestWrapper{
    
    public WsdlServletRequestWrapper(HttpServletRequest req){
      super(req);
    }
    
    public String getQueryString() {
      return super.getQueryString().replace(HTML_PARAM, WSDL_PARAM);
    }
    
    public Map getParameterMap() {
      Map<String, String> params = new HashMap<String, String>();
      params.put(WSDL_PARAM, "");
      return params;
    }
    
    public Enumeration getParameterNames() {
      Vector<String> v = new Vector<String>();
      v.add(WSDL_PARAM);
      return v.elements();
    }
    

    public String[] getParameterValues(String name) {
      if(name.equals(WSDL_PARAM)){
        return new String[]{""};
      }
      else{
        return null;
      }
    }
    
    public String getParameter(String name) {
      if(name.equals(WSDL_PARAM)){
        return "";
      }
      else{
        return null;
      }
    }
  }
  
  ///// ServletOutputStream 
  
  public class WsdlOutputStream extends ServletOutputStream{
    
    static final String END_TAG = "</wsdl:definitions>";
    
    private ByteArrayOutputStream _out = new ByteArrayOutputStream();
    private PrintStream           _ps  = new PrintStream(_out);
    private HttpServletResponse   _res;
    private boolean               _closed;
    
    public void flush() throws IOException {
      _out.flush();
    }

    public WsdlOutputStream(HttpServletResponse res) {
      _res = res;
    }

    public void print(boolean arg0) throws IOException {
      _ps.print(arg0);
    }

    public void print(char arg0) throws IOException {
      _ps.print(arg0);
    }

    public void print(double arg0) throws IOException {
      _ps.print(arg0);
    }

    public void print(float arg0) throws IOException {
      _ps.print(arg0);
    }

    public void print(int arg0) throws IOException {
      _ps.print(arg0);
    }

    public void print(long arg0) throws IOException {
      _ps.print(arg0);
    }

    public void print(String arg0) throws IOException {
      _ps.print(arg0);
    }

    public void println() throws IOException {
      _ps.println();
    }

    public void println(boolean arg0) throws IOException {
      _ps.println(arg0);
    }

    public void println(char arg0) throws IOException {
      _ps.println(arg0);
    }

    public void println(double arg0) throws IOException {
      _ps.println(arg0);
    }

    public void println(float arg0) throws IOException {
      _ps.println(arg0);
    }

    public void println(int arg0) throws IOException {
      _ps.println(arg0);
    }

    public void println(long arg0) throws IOException {
      _ps.println(arg0);
    }

    public void println(String arg0) throws IOException {
      _ps.println(arg0);
    }

    public void close() throws IOException {
      if(_closed) return;
      _out.close();
      Source src = new StreamSource(new ByteArrayInputStream(_out.toByteArray()));
      OutputStream os = _res.getOutputStream();
      Result result = new StreamResult(os);
      _res.setContentType("text/html");
      if(_xsl == null){
        // noop
      }
      else{
        try{
          _xsl.transform(src, result);
        }catch(TransformerException e){
          String msg ="Could not transform wsdl to XML";
          _conf.getServletContext().log(msg, e);
          throw new IOException(msg);
        }finally{
          try { os.flush(); os.close(); } catch (Exception e) {}
        }
      }
      super.close();
      _closed = true;
    }

    public void write(byte[] b, int off, int len) throws IOException {
      _out.write(b, off, len);
      String tmp = new String(b, off, len);      
      if(tmp.trim().endsWith(END_TAG)){
        close();
      }
    }

    public void write(byte[] b) throws IOException {
      _out.write(b);
      String tmp = new String(b);      
      if(tmp.trim().endsWith(END_TAG)){
        close();
      }
    }

    public void write(int b) throws IOException {
      _out.write(b);
    }
    
  }
}
