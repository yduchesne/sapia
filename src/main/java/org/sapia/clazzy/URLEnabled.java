package org.sapia.clazzy;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * This interface is intended to be implemented by classes 
 * whose instances "point" to URLs.
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public interface URLEnabled {
  
  /**
   * @return the <code>URL</code> that this instance holds.
   * @throws MalformedURLException
   */
  public URL getURL() throws MalformedURLException;

}
