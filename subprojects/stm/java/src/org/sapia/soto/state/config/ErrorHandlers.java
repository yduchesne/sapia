package org.sapia.soto.state.config;

import java.util.ArrayList;
import java.util.List;

import org.sapia.soto.Debug;
import org.sapia.soto.state.ErrorHandler;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectHandlerIF;

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
public class ErrorHandlers implements ObjectHandlerIF {

  private List _errorHandlers = new ArrayList();

  public void addHandleError(ErrorHandler handler) {
    _errorHandlers.add(handler);
  }

  public void addErrorHandlers(List handlers) {
    _errorHandlers.addAll(handlers);
  }

  public List getErrorHandlers() {
    return _errorHandlers;
  }

  /**
   * @see org.sapia.util.xml.confix.ObjectHandlerIF#handleObject(java.lang.String,
   *      java.lang.Object)
   */
  public void handleObject(String name, Object handler)
      throws ConfigurationException {
    if(!(handler instanceof ErrorHandler)) {
      throw new ConfigurationException(handler + " not an instance of "
          + ErrorHandler.class.getName());
    }
    Debug.debug("Adding error handler: " + handler);
    _errorHandlers.add(handler);
  }

}
