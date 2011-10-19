package org.sapia.soto.state.cocoon.example;

/**
 * @author Yanick Duchesne
 *         <dl>
 *         <dt><b>Copyright: </b>
 *         <dd>Copyright &#169; 2002-2003 <a
 *         href="http://www.sapia-oss.org">Sapia Open Source Software </a>. All
 *         Rights Reserved.</dd>
 *         </dt>
 *         <dt><b>License: </b>
 *         <dd>Read the license.txt file of the jar or visit the <a
 *         href="http://www.sapia-oss.org/license.html">license page </a> at the
 *         Sapia OSS web site</dd>
 *         </dt>
 *         </dl>
 */
public class Contact {
  private static int idCount = 0;
  private String     _firstName;
  private String     _lastName;
  private String     _phoneNumber;
  private Integer    _id     = new Integer(createId());

  public Contact() {
  }

  public Integer getId() {
    return _id;
  }

  public void setFirstName(String fn) {
    _firstName = fn;
  }

  public void setLastName(String ln) {
    _lastName = ln;
  }

  public void setPhoneNumber(String pn) {
    _phoneNumber = pn;
  }

  public String getFirstName() {
    return _firstName;
  }

  public String getLastName() {
    return _lastName;
  }

  public String getPhoneNumber() {
    return _phoneNumber;
  }

  public void update(Contact c) {
    _firstName = c.getFirstName();
    _lastName = c.getLastName();
    _phoneNumber = c.getPhoneNumber();
  }

  public String toString() {
    return "[ firstName=" + _firstName + ", lastName=" + _lastName + " ]";
  }

  private static synchronized int createId() {
    return ++idCount;
  }
}
