package org.sapia.soto.config.types;

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
public class BooleanType implements ObjectCreationCallback {

  private final String ON   = "on";
  private final String TRUE = "true";
  private final String YES  = "yes";

  private boolean      _value;

  public void setValue(String value) {
    _value = value.equalsIgnoreCase(ON) || value.equalsIgnoreCase(TRUE)
        || value.equalsIgnoreCase(YES);
  }

  /**
   * @see org.sapia.util.xml.confix.ObjectCreationCallback#onCreate()
   */
  public Object onCreate() throws ConfigurationException {
    return new Boolean(_value);
  }
}
