package org.sapia.soto.osgi.deployer;

/**
 * An instance of this interface provides deployment information to the
 * application code that triggered the deployment. The messages that
 * are provided are in fact create by the <code>Deployer</code> that is 
 * in charge of handling the deployment that corresponds to an instance
 * of this class.
 * 
 * @see org.sapia.soto.osgi.deployer.Deployment#info(String)
 * @see Deployer#deploy(Deployment)
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2005 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public interface DeploymentStatus {
  
  /**
   * @return <code>true</code> if this instance has a status message 
   * pending, or <code>false</code> if the deployment has completed successfully.
   * @throws DeploymentException if a problem occured during deployment. 
   */
  public boolean hasNextMessage() throws DeploymentException;
  
  /**
   * @return the next pending status message.
   */
  public String nextMessage();  

}
