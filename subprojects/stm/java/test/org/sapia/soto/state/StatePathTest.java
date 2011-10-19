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
public class StatePathTest extends TestCase {

  public StatePathTest(String name) {
    super(name);
  }

  public void testNewState() {
    StatePath sp = new StatePath("state");
    super.assertTrue(!sp.hasNextPathToken());
    super.assertEquals("state", sp.getStateId());
  }

  public void testNewStateModule() {
    StatePath sp = new StatePath("state", "module");
    super.assertTrue(sp.hasNextPathToken());
    super.assertEquals("module", sp.nextPathToken());
    super.assertEquals("state", sp.getStateId());
  }

  public void testNewComplexStateModule() {
    StatePath sp = new StatePath("state", "module1/module2");
    super.assertTrue(sp.hasNextPathToken());
    super.assertEquals("module1", sp.nextPathToken());
    super.assertTrue(sp.hasNextPathToken());
    super.assertEquals("module2", sp.nextPathToken());
    super.assertEquals("state", sp.getStateId());
  }
  
  public void testEquals(){
    StatePath sp1 = StatePath.parse("foo/bar/sna/fu");
    super.assertTrue(!sp1.equals(StatePath.parse("/foo/bar/sna/fu")));
    super.assertTrue(!sp1.equals(StatePath.parse("foo/bar/sna")));
    super.assertTrue(sp1.equals(StatePath.parse("foo/bar/sna/fu")));
  }
}
