/*
 * ErrorMessagesTest.java
 * JUnit based test
 *
 * Created on June 9, 2005, 10:53 AM
 */

package org.sapia.validator;

import junit.framework.*;
import java.util.Locale;

/**
 *
 * @author yduchesne
 */
public class ErrorMessagesTest extends TestCase {
  
  
  public ErrorMessagesTest(String testName) {
    super(testName);
  }

  protected void setUp() throws Exception {
  }

  protected void tearDown() throws Exception {
  }

  public void testGetErrorMessageFor() {
    ErrorMessages msgs = new ErrorMessages();
    ErrorMessage m;

    m = new ErrorMessage();
    m.setLocale("fr");
    m.setValue("french");
    msgs.addErrorMessage(m);    

    m = new ErrorMessage();
    m.setLocale("en");
    m.setValue("english");
    msgs.addErrorMessage(m);

    m = new ErrorMessage();
    m.setValue("default");
    msgs.addErrorMessage(m);        

    super.assertEquals("english", msgs.getErrorMessageFor(Locale.US).getValue());            
    super.assertEquals("english", msgs.getErrorMessageFor("en/US/alabama").getValue());
    super.assertEquals("english", msgs.getErrorMessageFor("en/US").getValue());    
    super.assertEquals("english", msgs.getErrorMessageFor("en").getValue());        
    super.assertEquals("french", msgs.getErrorMessageFor("fr").getValue());    
    super.assertEquals("default", msgs.getErrorMessageFor("es").getValue());        
  }
  
}
