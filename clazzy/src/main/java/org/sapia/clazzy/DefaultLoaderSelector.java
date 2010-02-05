package org.sapia.clazzy;

import org.sapia.clazzy.loader.Loader;

/**
 * This class implements a <code>ClassLoaderSelector</code> that accepts
 * "everything" (both of its methods systematically return <code>true</code>).
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class DefaultLoaderSelector implements LoaderSelector{
  
  /**
   * @see org.sapia.clazzy.LoaderSelector#acceptsClass(java.lang.String, org.sapia.clazzy.loader.Loader)
   */
  public boolean acceptsClass(String className, Loader loader) {
    return true;
  }
  
  /**
   * @see org.sapia.clazzy.LoaderSelector#acceptsResource(java.lang.String, org.sapia.clazzy.loader.Loader)
   */
  public boolean acceptsResource(String resourceName, Loader loader) {
    return true;
  }

}
