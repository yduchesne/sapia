package org.sapia.soto.osgi.deployer;

import java.net.URL;

/**
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2005 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public interface Deployment{

  /**
   * This method should be called by <code>Deployer</code> implementations
   * to inform the deploying party about the ongoing deployment. 
   * @param msg a status message.
   */
  public void info(String msg);
  
  /**
   * @return the <code>URL</code> of this deployment.
   */
  public URL getURL();
  
}
