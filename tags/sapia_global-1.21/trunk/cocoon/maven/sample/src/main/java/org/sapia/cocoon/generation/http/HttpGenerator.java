package org.sapia.cocoon.generation.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.ResourceNotFoundException;
import org.apache.cocoon.caching.CacheableProcessingComponent;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.generation.Generator;
import org.apache.cocoon.sitemap.SitemapModelComponent;
import org.apache.cocoon.xml.XMLConsumer;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.excalibur.source.SourceValidity;
import org.apache.excalibur.source.impl.validity.ExpiresValidity;
import org.cyberneko.html.parsers.SAXParser;
import org.sapia.cocoon.util.NullValidity;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * This generator uses the <a href="">HTTPClient</a> library to download content from a given
 * web site. It generates XML from the content (optionally using the <a href="http://nekohtml.sourceforge.net/">Neko</a> HTML parser). 
 * The generator allows specifying the following parameters:
 * 
 * <ul>
 *   <li>method: must be GET or POST.
 *   <li>url: the URL to connect to.
 *   <li>username: the username to use for authentication (optional).
 *   <li>password: the password to use for authentication (optional).
 *   <li>time-out: the connection timeout (in millis, defaults to 10000) - optional.
 *   <li>parse-html: if it exists and set to "true", the Neko HTML parser will be used
 *   to parse the downloaded content.
 *   <li>state-in-session: if "true", keeps the HTTP client state as part of the current servlet session (optional).
 *   <li>state-attribute: the name of the serlvet session attribute under which the HTTP client state must be kept (defaults
 *   to http.client.state). If <code>state-in-session</code> is not set (or not "true"), this parameter has no effect.
 *   <li>create-session: if "true", specifies that a servlet session must be created to hold the HTTP client state.
 *   If <code>state-in-session</code> is not set (or not "true"), this parameter has no effect.
 *   <li>request-headers: allows passing in HTTP request headers. Such headers must be passed as a list of name-value pairs, where each
 *   pair is delimited by a pipe character (|) and each name and value is delimited by a colon (:). 
 * </ul>
 * 
 * In addition, this generator supports caching (it allows profiting from Cocoon's caching architecture to avoid hitting a remote HTTP server
 * at every request). Caching is disabled by default, but it can be enabled by specifying an expiration time (after which cached content will be 
 * invalidated). The expiration time can be specified in either milliseconds, seconds, minutes, or hours.
 * 
 * <ul>
 *   <li>expiration-millis: the number of millis after which the content expires.
 *   <li>expiration-seconds: the number of seconds after which the content expires.
 *   <li>expiration-minutes: the number of minutes after which the content expires.
 *   <li>expiration-hours: the number of hours after which the content expires.
 * </ul>
 * 
 * <p>
 * <b>Example Spring configuration:</b>
 * </p>
 * <pre>
 *    &lt;bean name="org.apache.cocoon.generation.Generator/http"
 *       class="org.sapia.cocoon.generation.http.HttpGenerator" 
 *       scope="prototype" /&gt;   
 * </pre>  
 * <b>Example Sitemap configuration:</b>
 * </p>
 * <pre>
 *   &lt;map:generate src="http://rss.news.yahoo.com/rss/business" type="http"&gt;
 *     &lt;map:parameter name="parse-html" value="false" /&gt;
 *     &lt;map:parameter name="request-headers" 
 *                    value="User-Agent: Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)" /&gt;            
 *   &lt;/map:generate&gt;
 * </pre> 
 * 
 * @author yduchesne
 *
 */
public class HttpGenerator implements Generator, SitemapModelComponent, CacheableProcessingComponent{
  
  public static final String PARAM_METHOD               =  "method";
  public static final String PARAM_URL                  =  "url";
  public static final String PARAM_USERNAME             =  "username";
  public static final String PARAM_PASSWORD             =  "password";  
  public static final String PARAM_TIMEOUT              =  "time-out";
  public static final String PARAM_PARSE_HTML           =  "parse-html";
  public static final String PARAM_STATE_IN_SESSION     =  "state-in-session";
  public static final String PARAM_STATE_ATTRIBUTE      =  "state-attribute";
  public static final String PARAM_CREATE_SESSION       =  "create-session";
  public static final String PARAM_REQUEST_HEADERS      =  "request-headers";
  public static final String PARAM_EXPIRATION_MILLIS    =  "expiration-millis";
  public static final String PARAM_EXPIRATION_SECONDS   =  "expiration-seconds";
  public static final String PARAM_EXPIRATION_MINUTES   =  "expiration-minutes";
  public static final String PARAM_EXPIRATION_HOURS     =  "expiration-hours";
  
  public static final int DEFAULT_TIMEOUT = 10000;
  public static final long DEFAULT_EXPIRATION = 0;
  public static final String  DEFAULT_STATE_ATTRIBUTE = "http.client.state";  
  
  public static final String METHOD_GET =  "get";
  public static final String METHOD_POST =  "post";
  
  private String _method;
  private String _username, _password;
  private int _timeout = DEFAULT_TIMEOUT;
  private long _expiration = DEFAULT_EXPIRATION;
  private String _stateAttribute;
  private Map<String, String> _requestHeaders;
  private boolean _stateInSession, _createSession, _parseHtml;
  private XMLConsumer _consumer;
  private String _source;
  private Map _objectModel;
  
  public void setConsumer(XMLConsumer consumer) {
    _consumer = consumer;
  }
  
  public void setup(SourceResolver resolver, Map model, String src, Parameters params) throws ProcessingException, SAXException, IOException {
    _objectModel = model;
    _source = src;
    
    _method = params.getParameter(PARAM_METHOD, METHOD_GET);
    _username = params.getParameter(PARAM_USERNAME, null);
    _password = params.getParameter(PARAM_PASSWORD, null);
    _timeout = params.getParameterAsInteger(PARAM_TIMEOUT, DEFAULT_TIMEOUT);
    _stateInSession = params.getParameterAsBoolean(PARAM_STATE_IN_SESSION, false);
    _createSession = params.getParameterAsBoolean(PARAM_CREATE_SESSION, false);
    _parseHtml = params.getParameterAsBoolean(PARAM_PARSE_HTML, false);
    _stateAttribute = params.getParameter(PARAM_STATE_ATTRIBUTE, DEFAULT_STATE_ATTRIBUTE);
    _expiration = params.getParameterAsLong(PARAM_EXPIRATION_MILLIS, DEFAULT_EXPIRATION);
    
    if(_expiration <= DEFAULT_EXPIRATION){
      _expiration = params.getParameterAsLong(PARAM_EXPIRATION_SECONDS, DEFAULT_EXPIRATION) * 1000;
    }
    if(_expiration <= DEFAULT_EXPIRATION){
      _expiration = params.getParameterAsLong(PARAM_EXPIRATION_MINUTES, DEFAULT_EXPIRATION) * 1000 * 60;
    }
    if(_expiration <= DEFAULT_EXPIRATION){
      _expiration = params.getParameterAsLong(PARAM_EXPIRATION_HOURS, DEFAULT_EXPIRATION) * 1000 * 60 * 60;
    }    
    
    String requestHeaders = params.getParameter(PARAM_REQUEST_HEADERS, null);
    if(requestHeaders != null){
      _requestHeaders = new HashMap<String, String>();
      String[] headerValuePairs = requestHeaders.split("\\|");
      for(String pair:headerValuePairs){
        String[] nameAndValue = pair.split(":");
        if(nameAndValue.length > 1){
          _requestHeaders.put(nameAndValue[0], nameAndValue[1]);
        }
      }
    }
    if(_username != null && _password == null){
      throw new ProcessingException("Username set, but not password");
    }
    if(_username == null && _password != null){
      throw new ProcessingException("Password set, but not username");
    }    
  }
  
  public void generate() throws IOException, SAXException, ProcessingException {
    HttpClient client = new HttpClient();
    client.getHttpConnectionManager().getParams().setConnectionTimeout(_timeout);
    if(_username != null && _password != null){
      
    }
    if(_stateInSession){
      HttpSession sess = ObjectModelHelper.getRequest(_objectModel).getSession(_createSession);
      if(sess != null){
        HttpState state = (HttpState)sess.getAttribute(_stateAttribute);
        if(state == null){
          state = new HttpState();
          sess.setAttribute(_stateAttribute, state);
        }
        client.setState(state);
      }
    }
    HttpMethod method = createMethod();
    if(_requestHeaders != null && _requestHeaders.size() > 0){
      for(String name:_requestHeaders.keySet()){
        method.setRequestHeader(name.trim(), _requestHeaders.get(name.trim()));
      }
    }
    InputStream is = null;
    
    try{
      client.executeMethod(method);      
      if(method.getStatusCode() == HttpStatus.SC_OK){
        is = method.getResponseBodyAsStream();
        processBody(is);
      }
      else if(method.getStatusCode() == HttpStatus.SC_NOT_FOUND){
        throw new ResourceNotFoundException(_source);
      }
      else{
        throw new ProcessingException(method.getStatusText() == null ? "Could not process HTTP request (" + method.getStatusCode() + ")" : method.getStatusText() + "(" + method.getStatusCode() + ")");
      }

    }finally{
      if(is != null){
        is.close();
      }
      method.releaseConnection();
    }
  }
  
  private void processBody(InputStream is) throws ProcessingException, SAXException, IOException{
    if(_parseHtml){
      SAXParser parser = new SAXParser();
      parser.setContentHandler(_consumer);
      parser.parse(new InputSource(is));   
    }
    else{
      XMLReader reader = XMLReaderFactory.createXMLReader();
      reader.setContentHandler(_consumer);
      reader.parse(new InputSource(is));      
    }
  }
  
  public Serializable getKey() {
    if(_expiration > DEFAULT_EXPIRATION){
      return _source;
    }
    else{
      return null;
    }
  }
  
  public SourceValidity getValidity() {
    if(_expiration > DEFAULT_EXPIRATION){
      return new NullValidity();
    }
    else{
      return new ExpiresValidity(_expiration);
    }
  }
  
  private HttpMethod createMethod(){
    if(_method.equals(METHOD_GET)){
      return new GetMethod(_source);
    }
    else{
      return new PostMethod(_source);
    }
  }
  
}