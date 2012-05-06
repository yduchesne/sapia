package org.sapia.validator.examples;

import org.sapia.validator.Rule;
import org.sapia.validator.Status;
import org.sapia.validator.ValidationContext;
import org.sapia.validator.ValidationErr;
import org.sapia.validator.Vlad;

import java.io.File;

import java.util.List;

/**
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class PositiveRule extends Rule {
  /**
   * Constructor for PositiveRule.
   */
  public PositiveRule() {
    super();
  }

  /**
   * @see org.sapia.validator.Rule#validate(ValidationContext)
   */
  public void validate(ValidationContext context) {
    Integer intg = (Integer) context.get();

    if (intg.intValue() < 0) {
      context.getStatus().error(this);
    }
  }

  public static void main(String[] args) {
    try {
      Vlad v = new Vlad().loadDefs(
          "org/sapia/validator/examples/positivedefs.xml")
                         .load(new File("positive.xml")).load(new File(
            "vlad.xml"));
      Status s = v.validate("checkPositive", new Integer(-1),
          java.util.Locale.getDefault());

      if (s.isError()) {
        List          errs = s.getErrors();
        ValidationErr err;

        for (int i = 0; i < errs.size(); i++) {
          err = (ValidationErr) errs.get(i);

          if (err.isThrowable()) {
            err.getThrowable().printStackTrace();
          } else {
            System.out.println(err.getMsg());
          }
        }
      } else {
        System.out.println("No validation error.");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
