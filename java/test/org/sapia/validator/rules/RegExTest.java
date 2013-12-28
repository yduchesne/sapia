/*
 * RegExTest.java
 * JUnit based test
 *
 * Created on May 6, 2005, 9:52 AM
 */

package org.sapia.validator.rules;

import junit.framework.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.sapia.validator.BeanRule;
import org.sapia.validator.Status;
import org.sapia.validator.RuleSet;
import org.sapia.validator.Vlad;

/**
 *
 * @author yduchesne
 */
public class RegExTest extends TestCase {
  
  public RegExTest(String testName) {
    super(testName);
  }

    public void testValidate() throws Exception{
      Vlad v = new Vlad();
      RegEx regEx = new RegEx();
      regEx.setPattern("http://[^\\s]+");
      RuleSet rs = new RuleSet();
      rs.setId("regEx");
      rs.addValidatable(regEx);
      v.addRuleSet(rs);  
      Status st = v.validate("regEx", "http://www.yahoo.com ", java.util.Locale.getDefault());
      super.assertTrue("Check failed", st.isError());
 
    }
  
}
