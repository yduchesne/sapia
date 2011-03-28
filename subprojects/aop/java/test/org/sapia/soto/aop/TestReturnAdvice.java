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
public class TestReturnAdvice implements ReturnAdvice {
  int count = 0;

  /**
   * Constructor for TestReturnAdvice.
   */
  public TestReturnAdvice() {
    super();
  }

  /**
   * @see org.sapia.soto.aop.ReturnAdvice#onReturn(Invocation)
   */
  public Object onReturn(Invocation call) throws Throwable {
    count++;

    return call.getReturnValue();
  }
}
