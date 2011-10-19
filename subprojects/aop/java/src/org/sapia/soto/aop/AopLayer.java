package org.sapia.soto.aop;

import org.sapia.soto.Layer;
import org.sapia.soto.ServiceMetaData;

/**
 * This layer (and its helper classes) implements basic AOP behavior.
 * <p>
 * Example Configuration:
 * 
 * <pre>
 * 
 *  &lt;soto:app xmlns:soto=&quot;http://www.sapia-oss.org/soto/2003&quot;
 *  xmlns:aop=&quot;http://www.sapia-oss.org/soto/jmx/2003&quot;
 *  xmlns:test=&quot;http://www.sapia-oss.org/soto/test&quot;&gt;
 * 
 *  &lt;soto:namespace prefix=&quot;test&quot;&gt;
 *  &lt;def class=&quot;org.sapia.soto.aop.TestService&quot;      name=&quot;testService&quot; /&gt;
 *  &lt;def class=&quot;org.sapia.soto.aop.TestThrowsAdvice&quot; name=&quot;throws&quot; /&gt;
 *  &lt;/soto:namespace&gt;
 * 
 *  &lt;soto:service&gt;
 *  &lt;test:testService/&gt;
 * 
 *  &lt;aop:aspect&gt;
 *  &lt;adviceDef class=&quot;org.sapia.soto.aop.TestAroundAdvice&quot; id=&quot;around&quot;/&gt;
 *  &lt;adviceDef class=&quot;org.sapia.soto.aop.TestBeforeAdvice&quot; id=&quot;before&quot;/&gt;
 * 
 *  &lt;group id=&quot;group&quot;&gt;
 *  &lt;adviceDef class=&quot;org.sapia.soto.aop.TestAfterAdvice&quot;  id=&quot;after&quot;/&gt;
 *  &lt;adviceRef id=&quot;before&quot; /&gt;
 *  &lt;test:throws/&gt;
 *  &lt;/group&gt;
 * 
 *  &lt;method name=&quot;*&quot;&gt;
 *  &lt;adviceRef id=&quot;around&quot; /&gt;
 *  &lt;groupRef id=&quot;group&quot; /&gt;
 *  &lt;/method&gt;
 *  &lt;/aop:aspect&gt;
 *  &lt;/soto:service&gt;
 *  &lt;/soto:app&gt;
 *  
 * </pre>
 * 
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
public class AopLayer extends Adviser implements Layer {
  /**
   * Constructor for AopLayer.
   */
  public AopLayer() {
  }

  /**
   * @see org.sapia.soto.Layer#init(ServiceMetaData)
   */
  public void init(ServiceMetaData meta) throws Exception {
    Object proxy = advise(meta.getService());
    meta.setService(proxy);
    super.clear();
  }

  /**
   * @see org.sapia.soto.Layer#start(org.sapia.soto.ServiceMetaData)
   */
  public void start(ServiceMetaData meta) throws Exception {
  }

  /**
   * @see org.sapia.soto.Layer#dispose()
   */
  public void dispose() {
  }
}
