/*
 * ParamStringTest.java
 * JUnit based test
 *
 * Created on June 10, 2005, 9:26 AM
 */

package org.sapia.soto.state.util;

import junit.framework.TestCase;

import org.sapia.soto.state.ContextImpl;
import org.sapia.soto.state.MapScope;

/**
 *
 * @author yduchesne
 */
public class ParamStringTest extends TestCase {

  private ParamString _str;
  
  public ParamStringTest(String testName) {
    super(testName);
  }

  protected void setUp() throws Exception {
    _str = new ParamString("{first} {second}");
  }

  public void testRender() throws Exception{
    ContextImpl ctx = new ContextImpl();
    MapScope scope = new MapScope();
    scope.put("first", "value1");
    scope.put("second", "value2");    
    ctx.addScope("test", scope);
    super.assertEquals("value1 value2", _str.render(ctx));
  }
  
}

