package org.sapia.soto.osgi;

import org.osgi.framework.BundleContext;

/**
 * An instance of this class provides a callback to retrieve the
 * <code>BundleContext</code>.
 * <p>
 * Soto services that are loaded in the context of a <code>SotoBundleActivator</code>
 * automatically have access to an instance of this class. They should lookup the 
 * instance through their environment. Services that implement the <code>EnvAware</code>
 * interface are provided with an <code>Env</code> (which represents their
 * "environment") instance at startup time.
 * 
 * @see org.sapia.soto.EnvAware
 * @see org.sapia.soto.Env#lookup(Class)
 * @see org.sapia.soto.osgi.SotoBundleActivator
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2005 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public interface OSGICallback {
  
  /**
   * @return the <code>BundleContext</code> corresponding to 
   * the <code>Bundle</code> in the context of which the caller
   * was loaded.
   * 
   * @see BundleContext
   * @see Bundle
   */
  public BundleContext getBundleContext();

}
