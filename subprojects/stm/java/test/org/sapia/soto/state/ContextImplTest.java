package org.sapia.soto.state;

import junit.framework.TestCase;

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
public class ContextImplTest extends TestCase {
  /**
   *  
   */
  public ContextImplTest(String name) {
    super(name);
  }

  public void testPutGet() {
    ContextImpl ctx = new ContextImpl();
    Scope sc = new MapScope();
    sc.putVal("key", "value");
    ctx.addScope("request", sc);
    super.assertEquals("value", ctx.get("key"));
    super.assertEquals("value", ctx.get("key", "request"));
  }

  public void testPush() {
    ContextImpl ctx = new ContextImpl();
    ctx.push("test");
    super.assertEquals("test", ctx.currentObject());
    super.assertEquals("test", ctx.get("currentObject", "context"));
  }

  public void testPutScopedGet() {
    ContextImpl ctx = new ContextImpl();
    Scope req = new MapScope();
    Scope ses = new MapScope();
    req.putVal("key", "value");
    ctx.addScope("request", req);
    ctx.addScope("session", ses);
    super.assertTrue(ctx.get("key", new String[] { "session" }) == null);
    super.assertEquals("value", ctx.get("key", new String[] { "request" }));
  }

  public void testPushPop() {
    ContextImpl ctx = new ContextImpl();
    ctx.push("foo");
    super.assertEquals("foo", ctx.pop());
  }

  public void testCurrentObject() throws Exception {
    ContextImpl ctx = new ContextImpl();
    ctx.push("foo");
    super.assertEquals("foo", ctx.currentObject());
    ctx.pop();

    try {
      ctx.currentObject();
      throw new Exception("Object should not be on stack");
    } catch(IllegalStateException e) {
      // ok
    }
  }
}
