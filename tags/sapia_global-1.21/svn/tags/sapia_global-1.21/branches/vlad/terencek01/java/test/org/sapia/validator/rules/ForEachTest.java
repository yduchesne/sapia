package org.sapia.validator.rules;

import junit.framework.*;

import org.sapia.validator.*;

import java.util.*;

/**
 * @author Yanick Duchesne
 * 28-Apr-2003
 */
public class ForEachTest extends TestCase {
  /**
   * Constructor for ForEachTest.
   */
  public ForEachTest(String name) {
    super(name);
  }

  public void testStop() throws Exception {
    Vlad    v  = new Vlad();
    RuleSet rs = new RuleSet();

    rs.setId("forEach");

    ForEach  fe = new ForEach();
    TestRule t1 = new TestRule(true);
    TestRule t2 = new TestRule(false);

    fe.addValidatable(t1);
    fe.addValidatable(t2);
    rs.addValidatable(fe);
    v.addRuleSet(rs);
    v.validate("forEach", makeCollection(), java.util.Locale.getDefault());
    super.assertTrue("TestRule 1 was not called", t1.wasCalled());
    super.assertTrue("TestRule 2 was called", !t2.wasCalled());
  }

  public void testNoStop() throws Exception {
    Vlad    v  = new Vlad();
    RuleSet rs = new RuleSet();

    rs.setId("forEach");

    ForEach fe = new ForEach();

    fe.setStop(false);

    TestRule t1 = new TestRule(true);
    TestRule t2 = new TestRule(false);

    fe.addValidatable(t1);
    fe.addValidatable(t2);
    rs.addValidatable(fe);
    v.addRuleSet(rs);
    v.validate("forEach", makeCollection(), java.util.Locale.getDefault());
    super.assertTrue("TestRule 1 was not called", t1.wasCalled());
    super.assertTrue("TestRule 2 was not called", t2.wasCalled());
  }

  public Collection makeCollection() {
    List coll = new ArrayList();

    coll.add(new Object());
    coll.add(new Object());
    coll.add(new Object());

    return coll;
  }
}
