package org.sapia.clazzy.loader;

import java.net.URL;

/**
 * This interface specifies resource loading behavior. Classloader implementations
 * can use such implementations for loading bytes corresponding to given classes.
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public interface Loader {
  
  /**
   * Loads a the bytes of the specified resources and returns them.
   * @param resourceName
   * @return an array of <code>byte</code>s, or <code>null</code> if no resource could
   * be found for the given name, or if the bytes could not be loaded for some reason.
   */
  public byte[] loadBytes(String resourceName);
  
  /**
   * @param resourceName the name of a resource.
   * @return the <code>URL</code> corresponding to the given resource.
   */
  public URL getURL(String resourceName);
  
  /**
   * Releases all resources that this instance holds.
   */
  public void close();

}
