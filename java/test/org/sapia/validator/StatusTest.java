/*
 * StatusTest.java
 * JUnit based test
 *
 * Created on September 9, 2005, 4:29 PM
 */

package org.sapia.validator;

import junit.framework.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author yduchesne
 */
public class StatusTest extends TestCase {
  
  public StatusTest(String testName) {
    super(testName);
  }

  /**
   * Test of getErrors method, of class org.sapia.validator.Status.
   */
  public void testGetErrors() {
    Status st = new Status(null);
    st.addError(new ValidationErr("test", "msg"));
    super.assertEquals(1, st.getErrors().size());
  }

  /**
   * Test of isError method, of class org.sapia.validator.Status.
   */
  public void testIsError() {
    Status st = new Status(null);
    st.addError(new ValidationErr("test", "msg"));
    super.assertTrue(st.isError());
  }

  /**
   * Test of addErrors method, of class org.sapia.validator.Status.
   */
  public void testAddErrors() {
    Status st = new Status(null);
    List errs = new ArrayList();
    for(int i = 0; i < 5; i++){
      errs.add(new ValidationErr(""+i, "msg" + i));
    }
    st.addErrors(errs);
    super.assertEquals(5, st.getErrors().size());
  }

  /**
   * Test of removeErrorsFor method, of class org.sapia.validator.Status.
   */
  public void testRemoveErrorsFor() {
    Status st = new Status(null);
    List errs = new ArrayList();
    for(int i = 0; i < 5; i++){
      errs.add(new ValidationErr("test"+i, "msg" + i));
    }
    st.addErrors(errs);
    super.assertEquals(5, st.removeErrorsFor("test").size());    
    super.assertEquals(0, st.getErrors().size());    
  }

  /**
   * Test of removeErrors method, of class org.sapia.validator.Status.
   */
  public void testRemoveErrors() {
    Status st = new Status(null);
    List errs = new ArrayList();
    for(int i = 0; i < 5; i++){
      errs.add(new ValidationErr(""+i, "msg" + i));
    }
    st.addErrors(errs);
    super.assertEquals(5, st.removeErrors().size());    
    super.assertEquals(0, st.getErrors().size());        
  }
  
  public void testRemoveAnonymousErrors(){
    Status st = new Status(null);
    List errs = new ArrayList();
    for(int i = 0; i < 5; i++){
      errs.add(new ValidationErr(""+i, "msg" + i));
    }
    errs.add(new ValidationErr(null, "test1"));
    errs.add(new ValidationErr(null, "test2"));
    st.addErrors(errs);
    super.assertEquals(2, st.removeAnonymousErrors().size());    
    super.assertEquals(5, st.getErrors().size());        

  }
  
}
