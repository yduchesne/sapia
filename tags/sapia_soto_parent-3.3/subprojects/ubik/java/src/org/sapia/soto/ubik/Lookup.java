package org.sapia.soto.ubik;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.sapia.ubik.rmi.naming.remote.RemoteInitialContextFactory;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

/**
 * An instance of this class is used to lookup remote Ubik services.
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
public class Lookup implements ObjectCreationCallback {
  private String _host;
  private String _domain;
  private String _name;
  private int    _port;

  /**
   * Constructor for Lookup.
   */
  public Lookup() {
  }

  /**
   * @param host
   *          the host of the remote Ubik JNDI server.
   */
  public void setJndiHost(String host) {
    _host = host;
  }

  /**
   * @param port
   *          the port of the remote Ubik JNDI server.
   */
  public void setJndiPort(int port) {
    _port = port;
  }

  /**
   * @param domain
   *          the domain of the remote Ubik JNDI server.
   */
  public void setDomain(String domain) {
    _domain = domain;
  }

  /**
   * @param name the name of the object to lookup.
   */
  public void setJndiName(String name) {
    _name = name;
  }

  /**
   * @see org.sapia.util.xml.confix.ObjectCreationCallback#onCreate()
   */
  public Object onCreate() throws ConfigurationException {
    if(_port == 0) {
      _port = 1099;
    }

    if(_host == null) {
      _host = "localhost";
    }

    if(_domain == null) {
      _domain = "default";
    }

    Properties props = new Properties();
    props.setProperty(Context.INITIAL_CONTEXT_FACTORY,
        RemoteInitialContextFactory.class.getName());
    props.setProperty(RemoteInitialContextFactory.UBIK_DOMAIN_NAME, _domain);
    props.setProperty(Context.PROVIDER_URL, "ubik://" + _host + ":" + _port);

    InitialContext ctx = null;

    try {
      ctx = new InitialContext(props);

      return ctx.lookup(_name);
    } catch(NamingException e) {
      throw new ConfigurationException("Could not lookup " + _name, e);
    } finally {
      if(ctx != null) {
        try {
          ctx.close();
        } catch(NamingException e) {
          //noop;
        }
      }
    }
  }
}
