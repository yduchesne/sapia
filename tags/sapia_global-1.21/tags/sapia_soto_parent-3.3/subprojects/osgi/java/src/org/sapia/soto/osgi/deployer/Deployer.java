package org.sapia.soto.osgi.deployer;

import java.net.URL;

/**
 * An instance of this class is responsible for handling deployments. 
 * A deployment consists of some type of application packaged has a
 * Java archive file. The archive is usually named according to a predefined
 * pattern so that it can be recognized by a specific deployer. For example,
 * a deployer specializing in the deployment of web applications could be
 * expecting war files (Java archives ending with the .war file extension).
 * <p>
 * The resources to deploy are specified by a URL. Once a deployer has
 * successfully deployed a resource, it should be able to associate that URL
 * to that resource later on - this is necessary for the undeployment procedure).
 * <p>
 * The URL that was originally passed in the context of a deployment is reused
 * in two cases:
 * <ul>
 *   <li>If a new resource with the same name is to be deployed, which should be
 *   interpreted as an update by an instance of this class.
 *   <li>If a deployed resource is to be undeployed (in this case,d a deployer must
 *   be able to relate the URL to the actual application/component/bundle that is currently
 *   deployed).
 * </ul>
 * <p>
 * An instance of this class must register itself with a <code>DeploymentManager</code>. It
 * is also responsible for unregistering itself from its deployment manager, if that applies
 * (for example, if it is terminated or cannot process deployments anymore).
 * <p>
 * Note that a deployer must be configured with a logical type that identifies it uniquely
 * in the scope of a <code>DeploymentManager</code>. * 
 * 
 * @see org.sapia.soto.osgi.deployer.DeploymentManager
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2005 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public interface Deployer {
  
  /**
   * @param toDeploy the <code>URL</code> of a file to deploy.
   * @return <code>true</code> if this instance can handle the given
   * URL.
   */
  public boolean accepts(URL toDeploy);
  
  /**
   * Deploys the given deployment unit.
   * 
   * @param toDeploy a <code>Deployment</code>
   */
  public void deploy(Deployment toDeploy) throws DeploymentException;
  
  /**
   * @param toUndeploy a <code>URL</code> corresponding to the resource to undeploy.
   * @throws DeploymentException if the resource corresponding to the
   * given URL could not be undeployed.
   */
  public void undeploy(URL toUndeploy) throws DeploymentException;
  
  /**
   * @return a logical deployer type.
   */
  public String getType();

}
