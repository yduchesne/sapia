package org.sapia.soto;

/**
 * Instance of classes that implement this interface are assigned an
 * <code>Env</code> instance after their creation. This allows them to
 * indirectly have a hook on their container.
 * 
 * @see org.sapia.soto.Env
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
public interface EnvAware {
  /**
   * @param env
   *          an <code>Env</code> instance that indirectly corresponds to the
   *          Soto container to which this instance belongs.
   */
  public void setEnv(Env env);
}
