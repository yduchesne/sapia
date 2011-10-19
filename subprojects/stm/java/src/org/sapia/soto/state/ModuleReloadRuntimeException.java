package org.sapia.soto.state;

import org.sapia.util.CompositeRuntimeException;

/**
 * Thrown when a module could not be reloaded.
 * 
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
public class ModuleReloadRuntimeException extends CompositeRuntimeException {

  /**
   * @param arg0
   * @param arg1
   */
  public ModuleReloadRuntimeException(String arg0, Throwable arg1) {
    super(arg0, arg1);
  }

  /**
   * @param arg0
   */
  public ModuleReloadRuntimeException(String arg0) {
    super(arg0);
  }

}
