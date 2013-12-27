/*
 * PropertyPathTest.java
 * JUnit based test
 *
 * Created on December 3, 2005, 10:49 PM
 */

package org.sapia.soto.jython.bean;

import java.util.List;
import junit.framework.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.python.core.PyException;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;
import org.sapia.soto.jython.JythonServiceImpl;
import org.sapia.soto.util.Utils;

/**
 *
 * @author yduchesne
 */
public class PropertyPathTest extends TestCase {
  
  private PythonInterpreter _ip;
  private PyObject _person;
  
  public PropertyPathTest(String testName) {
    super(testName);
  }

  protected void setUp() throws Exception {
    /*JythonServiceImpl impl = new JythonServiceImpl();
    impl.setPythonPath("./etc/jython");
    impl.init();
    _ip = impl.getInterpreter();
    _ip.exec("from JythonTest import Person");
    _ip.exec("person=Person()");
    _person = _ip.get("person");*/
  }

  protected void tearDown() throws Exception {
  }
  
  public void testNoop(){}


  /**
   * Test of set method, of class org.sapia.soto.jython.bean.PropertyPath.
   */
  /*
  public void testSet() {
    PropertyPath path = PropertyPath.parse("address.phoneNumber");
    path.setLenient(false);
    path.set(_person, "123456");
    super.assertEquals(new PyString("123456"), path.get(_person));
    
    path = PropertyPath.parse("name");
    path.setLenient(false);
    path.set(_person, "foo");
    super.assertEquals(new PyString("foo"), path.get(_person));    
  }

  public void testAdd() {
    PropertyPath path = PropertyPath.parse("address.email");
    path.setLenient(false);
    path.add(_person, "email1");
    path.add(_person, "email2");
    
    path = PropertyPath.parse("address.emails");
    super.assertEquals(2, path.get(_person).__len__());
  }

  public void testParse() {
    PropertyPath path = PropertyPath.parse("address.email");
    List props = path.getProperties();
    Property prop = (Property)props.get(0);
    super.assertEquals("address", prop.getName());
    prop = (Property)props.get(1);
    super.assertEquals("email", prop.getName());    
  }

  public void testToString() {
    PropertyPath path = PropertyPath.parse("address.email");
    super.assertEquals("address.email", path.toString());
  }
*/  
}
