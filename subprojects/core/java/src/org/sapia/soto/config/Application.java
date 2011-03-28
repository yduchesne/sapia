package org.sapia.soto.config;

import org.sapia.soto.util.CompositeObjectFactoryEx;
import org.sapia.soto.util.Namespace;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectHandlerIF;

/**
 * This class implements the <code>soto:app</code> tag.
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
public class Application implements ObjectHandlerIF {
  private CompositeObjectFactoryEx _fac;

  /**
   * Constructor for Application.
   */
  public Application(CompositeObjectFactoryEx fac) {
    _fac = fac;
  }

  /**
   * @see org.sapia.util.xml.confix.ObjectHandlerIF#handleObject(String, Object)
   */
  public void handleObject(String elementName, Object obj)
      throws ConfigurationException {
    if(obj instanceof Namespace) {
      Namespace ns = (Namespace) obj;
      _fac.registerDefs(ns);
    }
  }
}
