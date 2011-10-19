package org.sapia.soto.util.matcher;

import junit.framework.TestCase;

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
public class PathPatternTest extends TestCase {
  public PathPatternTest(String arg0) {
    super(arg0);
  }

  public void testName() throws Exception {
    PathPattern pattern = PathPattern.parse("java.lang.String", false);
    super.assertTrue(pattern.matches("java.lang.String"));
  }

  public void testBeginMatch() throws Exception {
    PathPattern pattern = PathPattern.parse("*Buffer", false);
    super.assertTrue(pattern.matches("StringBuffer"));
  }

  public void testEndMatch() throws Exception {
    PathPattern pattern = PathPattern.parse("String*", false);
    super.assertTrue(pattern.matches("StringBuffer"));
  }

  public void testMiddleMatch() throws Exception {
    PathPattern pattern = PathPattern.parse("Str*uffer", false);
    super.assertTrue(pattern.matches("StringBuffer"));
  }

  public void testMiddlePath() throws Exception {
    PathPattern pattern = PathPattern.parse("java.**.String", false);
    super.assertTrue(pattern.matches("java.lang.String"));
  }

  public void testMiddlePathEndMatch() throws Exception {
    PathPattern pattern = PathPattern.parse("java.**.String*", false);
    super.assertTrue(pattern.matches("java.lang.StringBuffer"));
  }

  public void testMiddlePathStartMatch() throws Exception {
    PathPattern pattern = PathPattern.parse("*va.**.StringBuffer", false);
    super.assertTrue(pattern.matches("java.lang.StringBuffer"));
  }

  public void testBeginPath() throws Exception {
    PathPattern pattern = PathPattern.parse("**.StringBuffer", false);
    super.assertTrue(pattern.matches("java.lang.StringBuffer"));
  }

  public void testEndPath() throws Exception {
    PathPattern pattern = PathPattern.parse("java.**", false);
    super.assertTrue(pattern.matches("java.lang.StringBuffer"));
  }

  public void testPath() throws Exception {
    PathPattern pattern = PathPattern.parse("**", false);
    super.assertTrue(pattern.matches("java.lang.StringBuffer"));
  }
}
