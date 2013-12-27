package org.sapia.validator.rules;

import org.sapia.validator.*;

import junit.framework.TestCase;

/**
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class MaxDoubleTest extends TestCase{

  /**
   * Constructor for MaxDoubleTest.
   */
  public MaxDoubleTest(String name) {
    super(name);
  }
  
  public void testMaxDouble()
    throws Exception {
    Vlad    v  = new Vlad();
    RuleSet rs = new RuleSet();

    rs.setId("max");

    MaxDouble m = new MaxDouble();

    m.setValue(5.0);
    rs.addValidatable(m);
    v.addRuleSet(rs);

    Status st = v.validate("max", new Double(6), java.util.Locale.getDefault());
    super.assertTrue("Check failed", st.isError());
    
    st = v.validate("max", new Double(5), java.util.Locale.getDefault());
    super.assertTrue("Check failed", !st.isError());
    
    st = v.validate("max", new Double(4), java.util.Locale.getDefault());
    super.assertTrue("Check failed", !st.isError());
  }  

}
