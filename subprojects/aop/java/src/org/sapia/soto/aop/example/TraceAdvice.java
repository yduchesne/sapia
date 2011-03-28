package org.sapia.soto.aop.example;

import org.sapia.soto.aop.AroundAdvice;
import org.sapia.soto.aop.Invocation;

/**
 * @author Yanick Duchesne
 * 
 * <dl>
 * <dt><b>Copyright: </b>
 * <dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open
 * Source Software </a>. All Rights Reserved.</dd>
 * </dt>
 * <dt><b>License: </b>
 * <dd>Read the license.txt file of the jar or visit the <a
 * href="http://www.sapia-oss.org/license.html">license page </a> at the Sapia
 * OSS web site</dd>
 * </dt>
 * </dl>
 */
public class TraceAdvice implements AroundAdvice {
  public void preInvoke(Invocation call) throws Throwable {
    System.out.println(">>preInvoke: " + call.getMethodName());
  }

  public void postInvoke(Invocation call) throws Throwable {
    System.out.println(">>postInvoke: " + call.getMethodName());
  }
}
