package org.sapia.soto.osgi.deployer;

import java.net.URL;


/**
 * An instance of this class manages <code>Deployer</code>s. It in fact consists of a chain
 * of responsability: when the <code>deploy()</code> method is called, the passed in URL
 * is handed over to the proper <code>Deployer</code>. If no deployer could be found that
 * accepts the provided URL, then the deployment does not occur.
 * <p>
 * Deployers are normally implemented so that they handle specific types file archives (usually
 * identied with the proper extension), corresponding to different types of applications.
 * <p>
 * An instance of this class does not determine by itself when deployers should be removed
 * from its internal list. It is up to application code to remove deployers appropriately,
 * using the the <code>remove()</code> method.
 * 
 * @see org.sapia.soto.osgi.deployer.Deployer
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2005 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public interface DeploymentManager {

  /**
   * Adds a deployer at the end of this instance's chain 
   * of deployers.
   * 
   * @param deployer a <code>Deployer</code>.
   */
  public void add(Deployer deployer);
  
  /**
   * Inserts a deployer at the beginning of this instance's chain 
   * of deployers.
   * 
   * @param deployer a <code>Deployer</code>.
   */  
  public void insert(Deployer deployer);
  
  /**
   * Removes the given deployer from this instance. 
   * 
   * @param deployer a <code>Deployer</code>.
   */
  public void remove(Deployer deployer);
    
  /**
   * @param toDeploy a <code>URL</code> corresponding to a deployment
   * unit.
   * @throws DeploymentException if the deployment could not be done.
   * @return the <code>DeploymentStatus</code> corresponding to the current
   * deploymeent.
   */
  public DeploymentStatus deploy(URL toDeploy) throws DeploymentException;
  
  /**
   * @param toUndeploy the <code>URL</code> corresponding to the location
   * of the resource to undeploy.
   * @throws DeploymentException if the undeployment could not be done.
   */
  public void undeploy(URL toUndeploy) throws DeploymentException;  
  
}
