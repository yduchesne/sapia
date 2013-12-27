package org.sapia.soto.state;

import junit.framework.TestCase;

import org.sapia.soto.SotoContainer;

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
 */
public class ConfigTest extends TestCase {
  public ConfigTest(String name) {
    super(name);
  }

  public void testLoad() throws Exception {
    SotoContainer cont = new SotoContainer();
    cont.load("org/sapia/soto/state/stateLoadTest.xml");
    cont.start();

    ContextImpl ctx = new ContextImpl();
    MapScope scope = new MapScope();
    ctx.addScope("request", scope);
    scope.put("key", "value");

    StateMachineService svc = (StateMachineService) cont.lookup("test");
    svc.execute("state1", null, ctx);
  }
}
