package org.sapia.soto.osgi.deployer;

/**
 * Thrown when some problem occurs during a deployment.
 * 
 * @author Yanick Duchesne
 *
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2005 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class DeploymentException extends Exception{
  
  static final long serialVersionUID = 1L;
  
  /**
   * @param message
   */
  public DeploymentException(String message) {
    super(message);
  }
  /**
   * @param message
   * @param cause
   */
  public DeploymentException(String message, Throwable cause) {
    super(message, cause);
  }
  /**
   * @param cause
   */
  public DeploymentException(Throwable cause) {
    super(cause);
  }
}
