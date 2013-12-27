package org.sapia.soto.aop;


/**
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
 *  
 */
public class TestAroundAdvice implements AroundAdvice {
  int postCount;
  int preCount;

  /**
   * Constructor for TestAroundAdvice.
   */
  public TestAroundAdvice() {
    super();
  }

  /**
   * @see org.sapia.soto.aop.AfterAdvice#postInvoke(Invocation)
   */
  public void postInvoke(Invocation invocation) throws Throwable {
    postCount++;
  }

  /**
   * @see org.sapia.soto.aop.BeforeAdvice#preInvoke(Invocation)
   */
  public void preInvoke(Invocation invocation) throws Throwable {
    preCount++;
  }
}
