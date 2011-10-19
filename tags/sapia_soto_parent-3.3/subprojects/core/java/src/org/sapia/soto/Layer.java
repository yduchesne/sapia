package org.sapia.soto;

/**
 * This interface specifies the minimal behavior of implementations of the
 * "layer" concept.
 * <p>
 * The lifecycle of layers is as follows:
 * <li>the init() method is called after init() has been called on the layer's
 * corresponding service's init() method.
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
public interface Layer {
  /**
   * Initializes this instance.
   * 
   * @throws Exception
   *           if an exception occurs during initialization.
   */
  public void init(ServiceMetaData meta) throws Exception;

  /**
   * Starts this instance.
   * 
   * @throws Exception
   *           if an exception occurs during startup.
   */
  public void start(ServiceMetaData meta) throws Exception;

  /**
   * Shuts down this instance.
   */
  public void dispose();
}
