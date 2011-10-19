package org.sapia.soto.aop;

/**
 * Implements an advice that is called before an invocation returns.
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
public interface ReturnAdvice extends Advice {
  /**
   * This method is called when the given method does NOT have a
   * <code>void</code> return type. The method returns the object that will be
   * returned in place of the return value that is contained in the given
   * invocation object. If the latter is not to be replaced, then the object
   * already in the invocation object should be returned, with code similar to
   * the following:
   * 
   * <pre>
   * return invocation.getReturnValue();
   * </pre>
   * 
   * <p>
   * This method allows to return a proxy for the object passed in.
   * <p>
   * WARNING: implementations of this method should check if the passed in
   * instance is null and act appropriately.
   * 
   * @param invocation
   *          an <code>Invocation</code> instance.
   */
  public Object onReturn(Invocation invocation) throws Throwable;
}
