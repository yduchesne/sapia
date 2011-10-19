package org.sapia.soto.state.util;

import junit.framework.TestCase;

import org.sapia.soto.state.ContextImpl;
import org.sapia.soto.state.MapScope;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.Scope;
import org.sapia.soto.state.StateMachine;

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
public class AssertTest extends TestCase {
  public AssertTest(String name) {
    super(name);
  }

  public void testMissingParam() {
    ContextImpl ctx;
    Result res = new Result(new StateMachine(), ctx = new ContextImpl());
    Scope sc = new MapScope();
    sc.putVal("key", "value");
    ctx.addScope("test", sc);

    Assert a = new Assert();
    a.setKey("key");
    a.execute(res);
    super.assertTrue(!res.isError());
    sc = new MapScope();
    ctx.addScope("test2", sc);
    a.setScopes("test2");
    res = new Result(new StateMachine(), ctx);
    a.execute(res);
    super.assertTrue(res.isError());
  }
}
