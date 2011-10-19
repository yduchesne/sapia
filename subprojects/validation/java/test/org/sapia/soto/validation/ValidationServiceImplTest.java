/*
 * ValidationServiceImplTest.java
 * JUnit based test
 *
 * Created on September 10, 2005, 12:53 AM
 */

package org.sapia.soto.validation;

import java.io.File;
import java.util.Locale;

import junit.framework.TestCase;

import org.sapia.soto.SotoContainer;
import org.sapia.validator.Status;

/**
 *
 * @author yduchesne
 */
public class ValidationServiceImplTest extends TestCase {
  
  public ValidationServiceImplTest(String testName) {
    super(testName);
  }


  /**
   * Test of validate method, of class org.sapia.soto.validation.ValidationServiceImpl.
   */
  public void testValidate() throws Exception{
    SotoContainer container = new SotoContainer();
    container.load(new File("etc/validation/validationSample.xml"));
    
    ValidationService v = (ValidationService)container.lookup(ValidationService.class);
    Status st = v.validate("main", "checkHelloWorld", "Good Morning World", Locale.ENGLISH);
    super.assertTrue(st.isError());
    st = v.validate("main", "checkHelloWorld", "Hello World", Locale.ENGLISH);
    super.assertTrue(!st.isError());    
  }

}
