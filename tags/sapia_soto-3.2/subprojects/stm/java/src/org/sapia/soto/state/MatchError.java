package org.sapia.soto.state;

import org.sapia.soto.util.matcher.PathPattern;
import org.sapia.soto.util.matcher.Pattern;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectHandlerIF;

/**
 * This class implements an <code>ErrorHandler</code> that will execute its
 * internal states if the class of the error object passed to it matches a
 * specified pattern.
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
public class MatchError extends ExecContainer implements ErrorHandler,
    ObjectHandlerIF {

  private Pattern _pattern;

  /**
   * @param pattern
   *          the pattern of class names to match.
   */
  public void setPattern(String pattern) {
    _pattern = PathPattern.parse(pattern, '.', true);
  }

  /**
   * @see org.sapia.soto.state.ErrorHandler#handle(org.sapia.soto.state.Result)
   */
  public boolean handle(Result result) {
    if(_pattern == null) {
      throw new IllegalStateException("No pattern specified");
    }
    
    Err err = (Err) result.getContext().currentObject();
    if(err.getThrowable() != null) {
      if(_pattern.matches(err.getThrowable().getClass().getName())) {
        super.execute(result);
        return true;
      }
    } else {
      if(_pattern.matches(err.getMsg())) {
        super.execute(result);
        return true;
      }
    }
    return false;
  }

  /**
   * @see org.sapia.util.xml.confix.ObjectHandlerIF#handleObject(java.lang.String,
   *      java.lang.Object)
   */
  public void handleObject(String name, Object toHandle)
      throws ConfigurationException {
    try {
      super.addExecutable((Executable) toHandle);
    } catch(ClassCastException e) {
      throw new ConfigurationException("Expected instance of "
          + Executable.class.getName() + "; got: "
          + toHandle.getClass().getName());
    }

  }

}
