package org.sapia.soto.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.sapia.soto.util.Param;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

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
public class JndiRef implements ObjectCreationCallback {

  private String _jndiName;
  private List   _params = new ArrayList();

  public void setName(String name) {
    _jndiName = name;
  }

  public Param createProperty() {
    Param p = new Param();
    _params.add(p);
    return p;
  }

  /**
   * @see org.sapia.util.xml.confix.ObjectCreationCallback#onCreate()
   */
  public Object onCreate() throws ConfigurationException {
    if(_jndiName == null) {
      throw new ConfigurationException("Jndi name not specified");
    }
    try {
      if(_params.size() == 0) {
        InitialContext ctx = new InitialContext();
        try {
          return ctx.lookup(_jndiName);
        } finally {
          ctx.close();
        }
      } else {
        Properties props = new Properties();
        Param p;
        for(int i = 0; i < _params.size(); i++) {
          p = (Param) _params.get(i);
          props.setProperty(p.getName(), p.getValue().toString());
        }
        InitialContext ctx = new InitialContext(props);
        try {
          return ctx.lookup(_jndiName);
        } finally {
          ctx.close();
        }
      }
    } catch(NamingException e) {
      throw new ConfigurationException("Could not lookup: " + _jndiName, e);
    }
  }

}
