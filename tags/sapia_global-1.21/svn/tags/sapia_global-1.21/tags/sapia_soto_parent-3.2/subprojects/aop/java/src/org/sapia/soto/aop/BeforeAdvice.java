package org.sapia.soto.aop;

/**
 * Defines an advice that is called before a method invocation.
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
public interface BeforeAdvice extends Advice {
  /**
   * This method is called before a method on the given instance is called.
   * <p>
   * <b>IMPORTANT </b>: implementations of this method must use the
   * <code>invokeSuper()</code> method on the passed in
   * <code>MethodProxy</code> instance when performing dynamic method calls.
   * 
   * @param invocation
   *          an <code>Invocation</code> instance.
   */
  public void preInvoke(Invocation invocation) throws Throwable;
}
