package org.sapia.validator.examples;

import org.sapia.validator.*;
import org.sapia.validator.rules.*;

import java.util.*;

/**
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class MandatoryEg {
  /**
   * Constructor for MandatoryEg.
   */
  public MandatoryEg() {
    super();
  }

  public static void main(String[] args) {
    RuleSet rules = new RuleSet();

    rules.setId("checkCompany");

    Mandatory m = new Mandatory();

    m.setId("CompanyName");
    m.setAttribute("name");
    rules.addValidatable(m);

    Vlad         v   = new Vlad();
    ErrorMessage msg = m.createMessage();

    msg.setValue("Company name is mandatory");
    v.addRuleSet(rules);

    Status        st   = v.validate("checkCompany", new Company(null),
        Locale.getDefault());
    List          errs = st.getErrors();
    ValidationErr err;

    for (int i = 0; i < errs.size(); i++) {
      err = (ValidationErr) errs.get(i);

      if (err.isThrowable()) {
        System.out.println("id :" + err.getId());
        err.getThrowable().printStackTrace();
      } else {
        System.out.println("id :" + err.getId());
        System.out.println(err.getMsg());
      }
    }
  }
}
