package org.sapia.soto.state.web.simple;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.transform.OutputKeys;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.Serializer;
import org.apache.xml.serialize.SerializerFactory;
import org.sapia.soto.Debug;
import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.Service;
import org.sapia.soto.state.dispatcher.Dispatcher;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectHandlerIF;

import simple.http.ProtocolHandler;
import simple.http.Request;
import simple.http.Response;
import simple.http.connect.Connection;
import simple.http.connect.ConnectionFactory;
import simple.util.parse.URIParser;

/**
 * @author Yanick Duchesne
 * 
 * <dl>
 * <dt><b>Copyright: </b>
 * <dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open
 * Source Software </a>. All Rights Reserved.</dd>
 * </dt>
 * <dt><b>License: </b>
 * <dd>Read the license.txt file of the jar or visit the <a
 * href="http://www.sapia-oss.org/license.html">license page </a> at the Sapia
 * OSS web site</dd>
 * </dt>
 * </dl>
 */
public class SimpleServer implements ProtocolHandler, Service, EnvAware,
    ObjectHandlerIF {

  public static final int     DEFAULT_PORT = 8080;
  private int                 _port        = DEFAULT_PORT;
  private Connection          _conn;
  private ServerSocket        _sock;
  private Env                 _env;
  private List                _dispatchers = new ArrayList();

  /**
   * @param port
   *          the port on which this instance will listen.
   */
  public void setPort(int port) {
    _port = port;
  }

  /**
   * @see org.sapia.soto.EnvAware#setEnv(org.sapia.soto.Env)
   */
  public void setEnv(Env env) {
    _env = env;
  }

  /**
   * @see org.sapia.soto.Service#init()
   */
  public void init() throws Exception {
    if(_dispatchers == null) {
      throw new IllegalStateException("Dispatcher not specified");
    }
    _conn = ConnectionFactory.getConnection(this);
  }

  /**
   * @see org.sapia.soto.Service#start()
   */
  public void start() throws Exception {
    Debug.debug(getClass(), "Starting server on port: " +  _port);
    _sock = new ServerSocket(_port);
    _conn.connect(_sock);
    Debug.debug(getClass(), "Server started");
  }

  /**
   * @see org.sapia.soto.Service#dispose()
   */
  public void dispose() {
    try {
      _sock.close();
    } catch(IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * @see simple.http.ProtocolHandler#handle(simple.http.Request,
   *      simple.http.Response)
   */
  public void handle(Request req, Response res) {
    res.setDate("Date", System.currentTimeMillis());
    res.setDate("Last-Modified", System.currentTimeMillis());

    URIParser uri = new URIParser(req.getURI());

    Properties props = new Properties();
    props.setProperty(OutputKeys.INDENT, "yes");
    props.setProperty(OutputKeys.METHOD, "xml");
    OutputFormat format = new OutputFormat("xml", "UTF-8", true);
    OutputStream out;
    try {
      out = res.getOutputStream();
    } catch(IOException e) {
      return;
    }

    try {
      Serializer ser = SerializerFactory.getSerializerFactory("xml")
          .makeSerializer(out, format);
      SimpleContext ctx = new SimpleContext(_env, ser.asContentHandler(), req, res);
      Dispatcher disp;
      for(int i = 0; i < _dispatchers.size(); i++) {
        disp = (Dispatcher) _dispatchers.get(i);
        if(disp.dispatch(uri.getPath().getPath(), ctx)) {
          return;
        }
      }
      throw new FileNotFoundException(uri.getPath().getPath());
    } catch(Exception e) {
      try {
        out.flush();
      } catch(IOException e2) {
        e2.printStackTrace();
      }
      try {
        out.close();
      } catch(IOException e2) {
        e2.printStackTrace();
      }
    } 
  }

  /**
   * @see org.sapia.util.xml.confix.ObjectHandlerIF#handleObject(java.lang.String,
   *      java.lang.Object)
   */
  public void handleObject(String name, Object obj)
      throws ConfigurationException {
    if(obj instanceof Dispatcher) {
      _dispatchers.add(obj);
    } else {
      throw new ConfigurationException("Expecting instance of : "
          + Dispatcher.class.getName());
    }
  }
}
