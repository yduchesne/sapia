package org.sapia.validator.examples;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class Company {
  private String _name;
  private List   _empls = new ArrayList();

  /**
   * Constructor for Company.
   */
  public Company(String name) {
    _name = name;
  }

  public String getName() {
    return _name;
  }

  public void addEmployee(Employee empl) {
    _empls.add(empl);
  }

  public List getEmployees() {
    return _empls;
  }
}
