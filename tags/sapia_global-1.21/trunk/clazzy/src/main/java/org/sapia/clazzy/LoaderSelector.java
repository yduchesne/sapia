package org.sapia.clazzy;

import org.sapia.clazzy.loader.Loader;

/**
 * An implementation of this interface is used to determine if given classes
 * or resources correspond to given <code>Loader</code>s.
 * 
 * @see org.sapia.clazzy.CompositeClassLoader
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
public interface LoaderSelector {

  /**
   * @param className the name of a class.
   * @param loader a <code>Loader</code>.
   * @return <code>true</code> if the given <code>Loader</code> is in charge
   * of providing the bytes of the class corresponding to the given class name.
   */
  public boolean acceptsClass(String className, Loader loader);

  /**
   * @param resourceName the name of a resource.
   * @param loader a <code>Loader</code>.
   * @return <code>true</code> if the given <code>Loader</code> is in charge
   * of providing the bytes of the resource corresponding to the given resource name.
   */  
  public boolean acceptsResource(String resourceName, Loader loader);

}
