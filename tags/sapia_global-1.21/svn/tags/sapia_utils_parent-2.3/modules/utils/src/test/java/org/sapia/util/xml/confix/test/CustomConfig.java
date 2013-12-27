package org.sapia.util.xml.confix.test;

// Import of Sun's JDK classes
// ---------------------------
import org.jdom.Element;
import org.sapia.util.xml.confix.JDOMHandlerIF;
import org.sapia.util.xml.confix.ConfigurationException;


/**
 *
 *
 * @author Jean-Cedric Desrochers
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class CustomConfig extends NestedConfig implements JDOMHandlerIF {

  public CustomConfig() {
  }

  public void handleElement(Element elem) throws ConfigurationException {
    _theConfigElement = elem;
  }
}
