package org.sapia.validator.rules;

import junit.framework.TestCase;

import org.sapia.validator.*;
import org.sapia.validator.examples.Company;

/**
 * @author Yanick Duchesne
 * 28-Apr-2003
 */
public class IfDefinedTest extends TestCase {
  /**
   * Constructor for IfDefinedTest.
   * @param arg0
   */
  public IfDefinedTest(String arg0) {
    super(arg0);
  }

  public void testDefined() throws Exception {
    Vlad    v  = new Vlad();
    RuleSet rs = new RuleSet();

    rs.setId("ifDefined");

    IfDefined ifDef = new IfDefined();

    ifDef.setAttribute("name");

    TestRule t1 = new TestRule(false);

    ifDef.handleObject("someRule", t1);
    rs.addValidatable(ifDef);
    v.addRuleSet(rs);
    v.validate("ifDefined", new Company("ACME"), java.util.Locale.getDefault());
    super.assertTrue("TestRule 1 was not called", t1.wasCalled());
  }

  public void testNotDefined()
    throws Exception {
    Vlad    v  = new Vlad();
    RuleSet rs = new RuleSet();

    rs.setId("ifDefined");

    IfDefined ifDef = new IfDefined();

    ifDef.setAttribute("name");

    TestRule t1 = new TestRule(false);

    ifDef.handleObject("someRule", t1);
    rs.addValidatable(ifDef);
    v.addRuleSet(rs);
    v.validate("ifDefined", new Company(null), java.util.Locale.getDefault());
    super.assertTrue("TestRule 1 was not called", !t1.wasCalled());
  }

  public void testNotDefinedNoAttribute()
    throws Exception {
    Vlad    v  = new Vlad();
    RuleSet rs = new RuleSet();

    rs.setId("ifDefined");

    IfDefined ifDef = new IfDefined();
    TestRule  t1 = new TestRule(false);

    ifDef.handleObject("someRule", t1);
    rs.addValidatable(ifDef);
    v.addRuleSet(rs);
    v.validate("ifDefined", null, java.util.Locale.getDefault());
    super.assertTrue("TestRule 1 was called", !t1.wasCalled());
  }
}
