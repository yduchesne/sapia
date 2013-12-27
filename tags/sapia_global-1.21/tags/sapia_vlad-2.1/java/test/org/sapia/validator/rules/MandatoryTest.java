package org.sapia.validator.rules;

import junit.framework.*;

import org.sapia.validator.*;
import org.sapia.validator.examples.Company;

/**
 * @author Yanick Duchesne
 * 28-Apr-2003
 */
public class MandatoryTest extends TestCase {
  /**
   * Constructor for MandatoryTest.
   * @param arg0
   */
  public MandatoryTest(String arg0) {
    super(arg0);
  }

  public void testExists() throws Exception {
    Vlad    v  = new Vlad();
    RuleSet rs = new RuleSet();

    rs.setId("mandatory");

    Mandatory m = new Mandatory();

    rs.addValidatable(m);
    v.addRuleSet(rs);

    Status st = v.validate("mandatory", new Object(),
        java.util.Locale.getDefault());

    super.assertTrue("Mandatory check failed", !st.isError());
  }

  public void testNotExists()
    throws Exception {
    Vlad    v  = new Vlad();
    RuleSet rs = new RuleSet();

    rs.setId("mandatory");

    Mandatory m = new Mandatory();

    rs.addValidatable(m);
    v.addRuleSet(rs);

    Status st = v.validate("mandatory", null, java.util.Locale.getDefault());

    super.assertTrue("Mandatory check failed", st.isError());
  }

  public void testExistAttribute()
    throws Exception {
    Vlad    v  = new Vlad();
    RuleSet rs = new RuleSet();

    rs.setId("mandatory");

    Mandatory m = new Mandatory();

    m.setAttribute("name");
    rs.addValidatable(m);
    v.addRuleSet(rs);

    Status st = v.validate("mandatory", new Company("ACME"),
        java.util.Locale.getDefault());

    super.assertTrue("Mandatory check failed", !st.isError());
  }

  public void testNotExistAttribute()
    throws Exception {
    Vlad    v  = new Vlad();
    RuleSet rs = new RuleSet();

    rs.setId("mandatory");

    Mandatory m = new Mandatory();

    m.setAttribute("name");
    rs.addValidatable(m);
    v.addRuleSet(rs);

    Status st = v.validate("mandatory", new Company(null),
        java.util.Locale.getDefault());

    super.assertTrue("Mandatory check failed", st.isError());
  }
}
