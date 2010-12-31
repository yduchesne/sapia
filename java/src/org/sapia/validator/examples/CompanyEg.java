package org.sapia.validator.examples;

import org.sapia.validator.Status;
import org.sapia.validator.ValidationErr;
import org.sapia.validator.Vlad;

import java.io.File;

import java.util.List;
import java.util.Locale;

/**
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class CompanyEg {
  /**
   * Constructor for CompanyEg.
   */
  public CompanyEg() {
    super();
  }

  public static void main(String[] args) {
    try {
      Vlad v = new Vlad();

      v.load(new File("vlad.xml"));

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

      Company  comp  = new Company(null);
      Employee empl1 = new Employee("empl1");
      Employee empl2 = new Employee(null);

      comp.addEmployee(empl1);
      comp.addEmployee(empl2);

      st     = v.validate("checkCompany", comp, Locale.getDefault());
      errs   = st.getErrors();

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
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
