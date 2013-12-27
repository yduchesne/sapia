package org.sapia.soto.state.util;

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
public class StateIdParserTest extends TestCase {
  public StateIdParserTest(String name) {
    super(name);
  }

  public void testDefault() {
    super.assertTrue(StateIdParser.parseStateFrom("", null) == null);
    super.assertTrue(StateIdParser.parseStateFrom("/", null) == null);
  }

  public void testParseExtensionNoModule() {
    StateIdParser.Created c = StateIdParser.parseStateFrom(
        "/foo/bar/state.asp", "asp");
    super.assertEquals("foo/bar/state", c.state);
    super.assertTrue(c.module == null);
    c = StateIdParser.parseStateFrom("state.asp", "asp");
    super.assertEquals("state", c.state);
    super.assertTrue(c.module == null);
  }

  public void testParseExtensionWithModule() {
    StateIdParser.Created c = StateIdParser.parseStateFrom(
        "/foo/bar/module.state.asp", "asp");
    super.assertEquals("foo/bar/state", c.state);
    super.assertEquals("module", c.module);
    c = StateIdParser.parseStateFrom("module.state.asp", "asp");
    super.assertEquals("state", c.state);
    super.assertEquals("module", c.module);
  }

  public void testParseNoExtension() {
    StateIdParser.Created c = StateIdParser.parseStateFrom(
        "/foo/bar/state.asp", null);
    super.assertEquals("foo/bar/asp", c.state);
    super.assertEquals("state", c.module);
  }

  public void testParseNoExtensionWithModule() {
    StateIdParser.Created c = StateIdParser.parseStateFrom(
        "/foo/bar/module.state", null);
    super.assertEquals("foo/bar/state", c.state);
    super.assertEquals("module", c.module);
    c = StateIdParser.parseStateFrom("module.state", null);
    super.assertEquals("state", c.state);
    super.assertEquals("module", c.module);
  }
}
