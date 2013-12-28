package org.sapia.validator.rules;

import junit.framework.TestCase;

import org.sapia.validator.*;

import java.util.*;

/**
 * @author Yanick Duchesne
 * 28-Apr-2003
 */
public class MinSizeTest extends TestCase {
  /**
   * Constructor for MinSizeTest.
   * @param arg0
   */
  public MinSizeTest(String arg0) {
    super(arg0);
  }

  public void testValidCollection()
    throws Exception {
    Vlad    v  = new Vlad();
    RuleSet rs = new RuleSet();

    rs.setId("minSize");

    MinSize m = new MinSize();

    m.setSize(1);
    rs.addValidatable(m);
    v.addRuleSet(rs);

    Status st = v.validate("minSize", makeCollection(),
        java.util.Locale.getDefault());

    super.assertTrue("Mandatory check failed", !st.isError());
  }

  public void testInvalidCollection()
    throws Exception {
    Vlad    v  = new Vlad();
    RuleSet rs = new RuleSet();

    rs.setId("minSize");

    MinSize m = new MinSize();

    m.setSize(5);
    rs.addValidatable(m);
    v.addRuleSet(rs);

    Status st = v.validate("minSize", makeCollection(),
        java.util.Locale.getDefault());

    super.assertTrue("Mandatory check failed", st.isError());
  }

  public void testValidArray()
    throws Exception {
    Vlad    v  = new Vlad();
    RuleSet rs = new RuleSet();

    rs.setId("minSize");

    MinSize m = new MinSize();

    m.setSize(3);
    rs.addValidatable(m);
    v.addRuleSet(rs);

    Status st = v.validate("minSize", makeArray(), java.util.Locale.getDefault());

    super.assertTrue("Mandatory check failed", !st.isError());
  }

  public void testInvalidArray()
    throws Exception {
    Vlad    v  = new Vlad();
    RuleSet rs = new RuleSet();

    rs.setId("minSize");

    MinSize m = new MinSize();

    m.setSize(5);
    rs.addValidatable(m);
    v.addRuleSet(rs);

    Status st = v.validate("minSize", makeArray(), java.util.Locale.getDefault());

    super.assertTrue("Mandatory check failed", st.isError());
  }

  public Collection makeCollection() {
    List coll = new ArrayList();

    coll.add(new Object());
    coll.add(new Object());
    coll.add(new Object());

    return coll;
  }

  public Object[] makeArray() {
    List coll = new ArrayList();

    coll.add(new Object());
    coll.add(new Object());
    coll.add(new Object());

    return coll.toArray();
  }
}
