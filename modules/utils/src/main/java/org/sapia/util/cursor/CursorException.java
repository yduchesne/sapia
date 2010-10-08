package org.sapia.util.cursor;

import org.sapia.util.CompositeRuntimeException;

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
public class CursorException extends CompositeRuntimeException {

  public CursorException(String msg, Throwable cause) {
    super(msg, cause);
  }

  public CursorException(String msg) {
    super(msg);
  }

}
