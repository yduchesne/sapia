/*
 * ScopeParserTest.java
 * JUnit based test
 *
 * Created on September 2, 2005, 9:53 PM
 */

package org.sapia.soto.state.helpers;

import junit.framework.TestCase;

/**
 *
 * @author yduchesne
 */
public class ScopeParserTest extends TestCase {
  
  public ScopeParserTest(String testName) {
    super(testName);
  }

  /**
   * Test of parse method, of class org.sapia.soto.state.helpers.ScopeParser.
   */
  public void testParse() {
    String scopes = "scope";
    super.assertEquals("scope", ScopeParser.parse(scopes)[0]);
    scopes = "scope1, scope2";
    super.assertEquals("scope1", ScopeParser.parse(scopes)[0]);
    super.assertEquals("scope2", ScopeParser.parse(scopes)[1]);
  }
  
}
