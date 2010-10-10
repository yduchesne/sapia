package org.sapia.soto.aop;

/**
 * This interface specifies the behavior of advices that implement interception
 * of <code>Throwable</code> instances - thrown on method calls.
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
public interface ThrowsAdvice extends Advice {
  /**
   * Called when an exception is thrown when invoking an advised method.
   * 
   * @param invocation
   *          an <code>Invocation</code> instance.
   * @param thrown
   *          the <code>Throwable</code> that was created has part of the
   *          method call.
   */
  public void onThrows(Invocation invocation, Throwable thrown)
      throws Throwable;
}
