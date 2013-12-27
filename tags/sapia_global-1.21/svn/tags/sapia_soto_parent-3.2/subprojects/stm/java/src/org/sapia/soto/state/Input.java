package org.sapia.soto.state;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interfaces that is used to hide streaming details from client applications.
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
public interface Input {
  /**
   * @return an <code>InputStream</code> whose origin is implementation
   *         dependant.
   * @throws IOException
   *           if an IO problem occurs while acquiring the stream.
   */
  public InputStream getInputStream() throws IOException;
}
