package org.sapia.soto.state;

import junit.framework.TestCase;

/**
 * @author Yanick Duchesne
 * 
 * <dl>
 * <dt><b>Copyright: </b>
 * <dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open
 * Source Software </a>. All Rights Reserved.</dd>
 * </dt>
 * <dt><b>License: </b>
 * <dd>Read the license.txt file of the jar or visit the <a
 * href="http://www.sapia-oss.org/license.html">license page </a> at the Sapia
 * OSS web site</dd>
 * </dt>
 * </dl>
 */
public class ContextScopeTest extends TestCase {

  public ContextScopeTest(String name) {
    super(name);
  }

  public void testPutGet() {
    Context context = new ContextImpl();
    Scope scope = (Scope) context.getScopes().get("context");
    scope.putVal("currentObject", "Foo");
    super.assertEquals("Foo", context.get("currentObject", "context"));
  }

}
