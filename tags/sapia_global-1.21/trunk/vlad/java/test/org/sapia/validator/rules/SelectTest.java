package org.sapia.validator.rules;

import junit.framework.TestCase;

import org.sapia.validator.*;
import org.sapia.validator.examples.*;

/**
 * @author Yanick Duchesne
 * 28-Apr-2003
 */
public class SelectTest extends TestCase {
  /**
   * Constructor for SelectTest.
   * @param arg0
   */
  public SelectTest(String arg0) {
    super(arg0);
  }

  public void testSelect() throws Exception {
    Vlad    v  = new Vlad();
    RuleSet rs = new RuleSet();

    rs.setId("select");

    Select s = new Select();

    s.setAttribute("name");

    TestRule t1 = new TestRule(false);

    s.handleObject("someRule", t1);
    rs.addValidatable(s);
    v.addRuleSet(rs);
    v.validate("select", new Company("ACME"), java.util.Locale.getDefault());
    super.assertTrue("TestRule 1 was not called", t1.wasCalled());
  }
}
