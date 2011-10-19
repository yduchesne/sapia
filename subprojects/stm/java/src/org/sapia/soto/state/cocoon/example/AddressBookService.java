package org.sapia.soto.state.cocoon.example;

import org.sapia.soto.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
public class AddressBookService implements Service {
  private Map _contacts = new HashMap();

  public AddressBookService() {
  }

  public int addContact(Contact c) {
    _contacts.put(c.getId(), c);
    return c.getId().intValue();
  }

  public Contact findContact(String id) throws AddrBookException {
    return findContact(Integer.parseInt(id));
  }

  public Contact findContact(int id) throws AddrBookException {
    Contact c = (Contact) _contacts.get(new Integer(id));

    if(c == null) {
      throw new AddrBookException("No contact found for: " + id);
    }

    return c;
  }

  public Collection getAllContacts() {
    return _contacts.values();
  }

  public void deleteContact(Contact c) {
    deleteContact(c.getId().intValue());
  }

  public void deleteContact(String id) {
    deleteContact(Integer.parseInt(id));
  }

  public void deleteContact(int id) {
    _contacts.remove(new Integer(id));
  }

  /**
   * @see org.sapia.soto.Service#init()
   */
  public void init() throws Exception {
  }

  /**
   * @see org.sapia.soto.Service#start()
   */
  public void start() throws Exception {
  }

  /**
   * @see org.sapia.soto.Service#dispose()
   */
  public void dispose() {
  }
}
