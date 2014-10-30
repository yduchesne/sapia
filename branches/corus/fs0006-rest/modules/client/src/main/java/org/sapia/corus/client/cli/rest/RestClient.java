package org.sapia.corus.client.cli.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.sapia.corus.client.common.rest.RestRequest;
import org.sapia.corus.client.common.rest.Value;
import org.sapia.corus.client.facade.CorusConnector;
import org.sapia.corus.client.rest.RequestContext;
import org.sapia.corus.client.rest.RestContainer;
import org.sapia.corus.client.rest.RestResponseFacade;
import org.sapia.corus.client.services.http.HttpResponseFacade;
import org.sapia.ubik.net.Uri;

/**
 * Handles RESTfull calls.
 * 
 * @author yduchesne
 *
 */
public class RestClient {
  
  private static final String LOCAL            = "local";
  private static final String HTTP             = "http";
  private static final String CONTENT_TYPE     = "Content-Type";
  private static final String ACCEPT           = "Accept";
  private static final String APPLICATION_JSON = "application/json";

  private static final String LINE_SEPARATOR = System.getProperty("line.separator");
  
  private CorusConnector connector;
  private RestContainer  container;
  
  /**
   * Creates an instance of this class encapsulating the given {@link CorusConnector}.
   * @param connector a {@link CorusConnector}.
   */
  public RestClient(CorusConnector connector) {
    this.connector = connector;
    this.container = RestContainer.Builder.newInstance().buildDefaultInstance();
  }
  
  /**
   * @param uri A URI, in literal form.
   * @param headers a {@link Map} of request headers to use.
   * @return the {@link ClientRestResponse} holding the response payload, headers, status code, etc.
   * @throws a Throwable instance corresponding to an error.
   */
  public ClientRestResponse get(String uri, Map<String, String> headers) throws Throwable { 
    return invoke(Uri.parse(uri), "GET", headers);
  }

  /**
   * @param uri A {@link Uri}.
   * @param method the HTTP method to use.
   * @param headers a {@link Map} of request headers to use.
   * @return the {@link ClientRestResponse} holding the response payload, headers, status code, etc.
   * @throws a Throwable instance corresponding to an error.
   */
  private ClientRestResponse invoke(Uri uri, String method, Map<String, String> headers) throws Throwable {
    ClientRestResponse response = new ClientRestResponse();
    if (uri.getScheme().equals(LOCAL)) {
      ClientRestRequest request = new ClientRestRequest();
      request.values.putAll(uri.getQueryString().getParameters());
      request.values.putAll(headers);
      String payload = container.invoke(new RequestContext(request, connector), response);
      response.setPayload(payload);
    } else if (uri.getScheme().equals(HTTP)) {
      URL               url    = new URL(uri.toString());
      DefaultHttpClient client = new DefaultHttpClient();
      HttpGet           get    = new HttpGet(url.toExternalForm());
      for (Map.Entry<String, String> entry : headers.entrySet()) {
        get.setHeader(entry.getKey(), entry.getValue());
      }
      get.setHeader(ACCEPT, APPLICATION_JSON);
      HttpResponse httpResp    = client.execute(get);
      Header       contentType = httpResp.getFirstHeader(CONTENT_TYPE);
      if (contentType != null) {
        response.setContentType(contentType.getValue());
      }
      response.setStatus(httpResp.getStatusLine().getStatusCode());
      response.setStatusMessage(httpResp.getStatusLine().getReasonPhrase());
      InputStream is = httpResp.getEntity().getContent();
      try {
        BufferedReader reader  = new BufferedReader(new InputStreamReader(is));
        String         line    = null;
        StringBuilder  builder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
          builder.append(line).append(LINE_SEPARATOR);
        }
        response.setPayload(builder.toString());      
      } finally {
        try {
          is.close();
        } catch (IOException e) {
          // noop
        }
      }
    } else {
      throw new IllegalArgumentException("Unknown scheme: " + uri.getScheme());
    }
    
    return response;
  }
  
  // ==========================================================================
  
  public static class ClientRestRequest implements RestRequest {
    
    private Map<String, String> values = new HashMap<String, String>();
    private String contentType;
    private Set<String> accepts = new HashSet<String>();
    private String method;
    private String path;
    
    @Override
    public Set<String> getAccepts() {
      return accepts;
    }
    
    @Override
    public String getContentType() {
      return contentType;
    }
    
    @Override
    public String getMethod() {
      return method;
    }
    
    @Override
    public String getPath() {
      return path;
    }
    
    @Override
    public Value getValue(String name) {
      return new Value(name, values.get(name));
    }
    
    @Override
    public Value getValue(String name, String defaultVal) {
      String value = values.get(name);
      if (value == null) {
        value = defaultVal;
      }
      return new Value(name, value);
    }
  }
  
  // --------------------------------------------------------------------------
    
  public static class ClientRestResponse implements RestResponseFacade {
    private int                   statusCode    = HttpResponseFacade.STATUS_OK;
    private String                contentType   = "";
    private int                   contentLength;
    private String                statusMessage = "";
    private String                payload;
    
    public int getStatus() {
      return statusCode;
    }
    
    @Override
    public void setStatus(int code) {
      statusCode = code;
    }

    public String getStatusMessage() {
      return statusMessage;
    }
    
    @Override
    public void setStatusMessage(String msg) {
      statusMessage = msg;
    }
    
    public int getContentLength() {
      return contentLength;
    }
    
    @Override
    public void setContentType(String contentType) {
      this.contentType = contentType;
    }
    
    public String getContentType() {
      return contentType;
    }
    
    public void setPayload(String payload) {
      this.payload = payload;
    }
    
    public String getPayload() {
      return payload;
    }
    
  }
  
}
