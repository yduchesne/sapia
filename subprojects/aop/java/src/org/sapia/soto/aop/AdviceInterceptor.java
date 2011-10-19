package org.sapia.soto.aop;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import gnu.trove.THashMap;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Intercepts methods and dispatches their call to the appropriate
 * <code>Adviser</code>.
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
public class AdviceInterceptor implements MethodInterceptor {
  private Map   _invokers = new THashMap();
  private Class _advised;

  /**
   * Constructor for AopInterceptor.
   */
  public AdviceInterceptor(Class advised) {
    _advised = advised;
  }

  /**
   * Adds an <code>AopDelegate</code> to this instance, for the given method.
   * 
   * @param method
   *          <code>Method</code> object to which the given delegate should be
   *          associated.
   * 
   * @param inv
   *          an <code>Invoker</code>.
   */
  public void addInvoker(Method method, Invoker inv) {
    List invokers = (List) _invokers.get(method);

    if(invokers == null) {
      invokers = new ArrayList(1);
    }

    invokers.add(inv);
    _invokers.put(method, invokers);
  }

  /**
   * Initializes this instance.
   */
  public void init() {
    ((THashMap) _invokers).compact();
  }

  public Class getAdvisedClass() {
    return _advised;
  }

  /**
   * @see net.sf.cglib.MethodInterceptor#intercept(Object, Method, Object[],
   *      MethodProxy)
   */
  public Object intercept(Object instance, Method toCall, Object[] args,
      MethodProxy proxy) throws Throwable {
    Object toReturn;

    if(_invokers.size() > 0) {
      List invokers = (List) _invokers.get(toCall);

      if((invokers == null) || (invokers.size() == 0)) {
        toReturn = proxy.invokeSuper(instance, args);
      } else {
        Iterator itr = invokers.iterator();
        Invocation invocation = new Invocation(instance, toCall, proxy, args);
        toReturn = ((Invoker) itr.next()).invoke(invocation, itr);
      }
    } else {
      toReturn = proxy.invokeSuper(instance, args);
    }

    return toReturn;
  }
}
