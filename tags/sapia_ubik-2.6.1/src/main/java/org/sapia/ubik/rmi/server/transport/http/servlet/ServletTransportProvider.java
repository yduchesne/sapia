package org.sapia.ubik.rmi.server.transport.http.servlet;

import java.io.EOFException;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sapia.ubik.net.Request;
import org.sapia.ubik.net.ServerAddress;
import org.sapia.ubik.net.UriSyntaxException;
import org.sapia.ubik.rmi.server.Config;
import org.sapia.ubik.rmi.server.Log;
import org.sapia.ubik.rmi.server.RMICommand;
import org.sapia.ubik.rmi.server.Server;
import org.sapia.ubik.rmi.server.invocation.InvokeCommand;
import org.sapia.ubik.rmi.server.perf.PerfAnalyzer;
import org.sapia.ubik.rmi.server.perf.Topic;
import org.sapia.ubik.rmi.server.transport.Connections;
import org.sapia.ubik.rmi.server.transport.TransportProvider;
import org.sapia.ubik.rmi.server.transport.http.HttpAddress;
import org.sapia.ubik.rmi.server.transport.http.HttpClientConnectionPool;
import org.sapia.ubik.rmi.server.transport.http.JdkClientConnectionPool;


/**
 * This transport provider is intended to be instantiated from within a servlet.
 * The servlet delegates request handling to this instance's <code>handleRequest()</code>
 * method.
 * <p>
 * The code excerpt below demonstrates how to create an instance of this class and
 * use it in a servlet implementation:
 *
 * <pre>
 * ...
 *
 * public void init(ServletConfig conf) throws ServletException {
 *   _provider = new ServletTransportProvider();
 *    TransportManager.registerProvider(_provider);
 *
 *   Properties props = new Properties();
 *
 *   // property below would normally be passed through a
 *   // servlet init parameter.
 *
 *   props.setProperty(ServletConsts.SERVLET_URL_KEY,
 *                     "http://localhost:8080/ubik");
 *
 *  // this tells ubik "under" which transport our object will be exported
 *   props.setProperty(Consts.TRANSPORT_TYPE, ServletConsts.DEFAULT_SERVLET_TRANSPORT_TYPE);
 *
 *   try{
 *     _foo = new UbikFoo();
 *     Hub.exportObject(_foo, props);
 *   }catch(RemoteException e){
 *    throw new ServletException("Could not export Foo", e);
 *   }
 * }
 *
 * ...
 *
 * protected void service(HttpServletRequest req, HttpServletResponse res)
 *   throws ServletException, IOException {
 *   _provider.handleRequest(req, res);
 * }
 *
 * ...
 * </pre>
 * <p>
 * Then, in a client VM, to connect to the remote object, we proceed as follows:
 * <pre>
 * TransportManager.registerProvider(new ServletTransportProvider());
 * Foo foo = (Foo)Hub.connect(new ServletAddress("http://localhost:8080/ubik"));
 * </pre>
 * <p>
 * NOTE: there can be only ONE instance of this instance registered to the <code>Hub</code>
 * <b>per web application</p>.
 *
 * @see #handleRequest(HttpServletRequest, HttpServletResponse)
 *
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class ServletTransportProvider implements TransportProvider,
  ServletConsts {
  private static boolean _usesJakarta;

  static {
    try {
      Class.forName("org.apache.commons.httpclient.HttpClient");
      _usesJakarta = true;
    } catch (Exception e) {
    }
  }

  private Perf           _perf          = new Perf();
  private ServletAddress _addr;
  private String         _transportType;
  private Map<ServerAddress, Connections> _pools = new ConcurrentHashMap<ServerAddress, Connections>();

  public ServletTransportProvider() {
    this(DEFAULT_SERVLET_TRANSPORT_TYPE);
  }

  protected ServletTransportProvider(String transportType) {
    _transportType = transportType;
  }

  /**
   * @see org.sapia.ubik.rmi.server.transport.TransportProvider#getTransportType()
   */
  public String getTransportType() {
    return _transportType;
  }

  /**
   * @see org.sapia.ubik.rmi.server.transport.TransportProvider#newDefaultServer()
   */
  public Server newDefaultServer() throws RemoteException {
    throw new UnsupportedOperationException(
      "Transport provider does not support anonymous servers/dynamic ports");
  }

  /**
   * This method returns a <code>Server</code> impl., given the properties passed in.
   * A single property is expected: <code>ubik.rmi.transport.servlet.url</code>, whose
   * value must correspond to the URL of the servlet that is encapsulating this instance.
   *
   * @see ServletConsts#SERVLET_URL_KEY
   *
   * @see org.sapia.ubik.rmi.server.transport.TransportProvider#newServer(java.util.Properties)
   */
  public Server newServer(Properties props) throws RemoteException {
    String servletUrl = props.getProperty(SERVLET_URL_KEY);

    if (servletUrl == null) {
      throw new RemoteException("'" + SERVLET_URL_KEY + "' not specified");
    }

    try {
      return new ServletServer(_addr = new ServletAddress(servletUrl));
    } catch (UriSyntaxException e) {
      throw new RemoteException("Could not parse servlet URL property", e);
    }
  }

  /**
   * Empty implementation.
   *
   * @see org.sapia.ubik.rmi.server.transport.TransportProvider#shutdown()
   */
  public void shutdown() {
  }

  /**
   * @see org.sapia.ubik.rmi.server.transport.TransportProvider#getPoolFor(org.sapia.ubik.net.ServerAddress)
   */
  public synchronized Connections getPoolFor(ServerAddress address)
    throws RemoteException {
    Connections conns;

    if ((conns = _pools.get(address)) == null) {
      try {
        if (_usesJakarta) {
          conns = new HttpClientConnectionPool((HttpAddress) address);
        } else {
          conns = new JdkClientConnectionPool((HttpAddress) address);
        }

        _pools.put(address, conns);
      } catch (UriSyntaxException e) {
        throw new RemoteException("Could not process given address", e);
      }
    }

    return conns;
  }

  public void handleRequest(HttpServletRequest httpReq,
    HttpServletResponse httpRes) {
    ServletRmiConnection conn = new ServletRmiConnection(_addr, httpReq, httpRes);

    Request              req = new Request(conn, _addr);

    if (Log.isDebug()) {
      Log.debug(getClass(), "handling request");
    }

    RMICommand cmd;
    Object     resp = null;

    try {
      if (Log.isDebug()) {
        Log.debug(getClass(), "receiving command");
      }

      cmd = (RMICommand) req.getConnection().receive();

      if (Log.isDebug()) {
        Log.debug(getClass(),
          "command received: " + cmd.getClass().getName() + " from " +
          req.getConnection().getServerAddress() + '@' + cmd.getVmId());
      }

      cmd.init(new Config(req.getServerAddress(), req.getConnection()));

      try {
        if (_perf.remoteCall.isEnabled()) {
          if (cmd instanceof InvokeCommand) {
            _perf.remoteCall.start();
          }
        }

        resp = cmd.execute();

        if (_perf.remoteCall.isEnabled()) {
          if (cmd instanceof InvokeCommand) {
            _perf.remoteCall.end();
          }
        }
      } catch (Throwable t) {
        t.printStackTrace();
        t.fillInStackTrace();
        resp = t;
        if (_perf.remoteCall.isEnabled()) {
          if (cmd instanceof InvokeCommand) {
            _perf.remoteCall.end();
          }
        }        
      }

      if (_perf.sendResponse.isEnabled()) {
        if (cmd instanceof InvokeCommand) {
          _perf.sendResponse.start();
        }
      }

      conn.send(resp, cmd.getVmId(), cmd.getServerAddress().getTransportType());

      if (_perf.sendResponse.isEnabled()) {
        if (cmd instanceof InvokeCommand) {
          _perf.sendResponse.end();
        }
      }
    } catch (RuntimeException e) {
      Log.error(getClass(), "RuntimeException caught sending response", e);

      try {
        e.fillInStackTrace();
        req.getConnection().send(e);
      } catch (IOException e2) {
        req.getConnection().close();

        return;
      }
    } catch (ClassNotFoundException e) {
      e.fillInStackTrace();
      Log.error(getClass(), "Class not found while receiving sending request", e);

      try {
        req.getConnection().send(e);
      } catch (IOException e2) {
        e2.fillInStackTrace();
        req.getConnection().close();

        return;
      }
    } catch (EOFException e) {
      e.fillInStackTrace();
      req.getConnection().close();

      return;
    } catch (java.net.SocketException e) {
      e.fillInStackTrace();
      req.getConnection().close();

      return;
    } catch (NotSerializableException e) {
      e.fillInStackTrace();
      Log.error(getClass().getName(),
        "Could not serialize class while sending response", e);

      try {
        req.getConnection().send(e);
      } catch (IOException e2) {
        req.getConnection().close();

        return;
      }
    } catch (InvalidClassException e) {
      e.fillInStackTrace();
      Log.error(getClass(), "Class is invalid; object could not be sent", e);

      e.fillInStackTrace();

      try {
        req.getConnection().send(e);
      } catch (IOException e2) {
        req.getConnection().close();

        return;
      }
    } catch (java.io.IOException e) {
      e.fillInStackTrace();

      try {
        req.getConnection().send(e);
      } catch (IOException e2) {
        req.getConnection().close();

        return;
      }
    }
  }
  
  private String className(){
    return getClass().getName();
  }
    
  ////// Inner classes
  
  class Perf {
    Topic remoteCall = PerfAnalyzer.getInstance().getTopic(className() + ".RemoteCall");
    Topic sendResponse = PerfAnalyzer.getInstance().getTopic(className() + ".SendResponse");
  }  
}
