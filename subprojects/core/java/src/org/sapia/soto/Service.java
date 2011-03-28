package org.sapia.soto;

/**
 * This interface specifies the behavior common to all Soto service
 * implementations.
 * 
 * @author Yanick Duchesne
 *         <dl>
 *         <dt><b>Copyright: </b>
 *         <dd>Copyright &#169; 2002-2003 <a
 *         href="http://www.sapia-oss.org">Sapia Open Source Software </a>. All
 *         Rights Reserved.</dd>
 *         </dt>
 *         <dt><b>License: </b>
 *         <dd>Read the license.txt file of the jar or visit the <a
 *         href="http://www.sapia-oss.org/license.html">license page </a> at the
 *         Sapia OSS web site</dd>
 *         </dt>
 *         </dl>
 */
public interface Service {
  /**
   * Performs initialization actions.
   * 
   * @throws Exception
   *           if a problem occurs while initializing.
   */
  public void init() throws Exception;

  /**
   * Starts this instance.
   * 
   * @throws Exception
   *           if a problem occurs while starting.
   */
  public void start() throws Exception;

  /**
   * Shuts down this service.
   */
  public void dispose();
}
