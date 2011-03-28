package org.sapia.soto.state.config;

import junit.framework.TestCase;

import org.sapia.soto.state.TestState;

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
public class StateSetTest extends TestCase {
  /**
   *  
   */
  public StateSetTest(String name) {
    super(name);
  }

  public void testIncludeNoExclude() {
    StateSet ss = new StateSet();
    TestState ts1 = new TestState(true);
    ts1.setId("someState");

    TestState ts2 = new TestState(true);
    ts2.setId("some");

    TestState ts3 = new TestState(true);
    ts3.setId("other");

    ss.createInclude().setPattern("some*");
    super.assertTrue(ss.matches(ts1));
    super.assertTrue(ss.matches(ts2));
    super.assertTrue(!ss.matches(ts3));
  }

  public void testNoIncludeExclude() {
    StateSet ss = new StateSet();
    TestState ts1 = new TestState(true);
    ts1.setId("someState");

    TestState ts2 = new TestState(true);
    ts2.setId("some");

    TestState ts3 = new TestState(true);
    ts3.setId("other");

    ss.createExclude().setPattern("someState");
    super.assertTrue(!ss.matches(ts1));
    super.assertTrue(ss.matches(ts2));
    super.assertTrue(ss.matches(ts3));
  }

  public void testIncludeExclude() {
    StateSet ss = new StateSet();
    TestState ts1 = new TestState(true);
    ts1.setId("someState");

    TestState ts2 = new TestState(true);
    ts2.setId("some");

    TestState ts3 = new TestState(true);
    ts3.setId("other");

    ss.createInclude().setPattern("someState");
    ss.createExclude().setPattern("some");
    super.assertTrue(ss.matches(ts1));
    super.assertTrue(!ss.matches(ts2));
    super.assertTrue(!ss.matches(ts3));
  }
}
