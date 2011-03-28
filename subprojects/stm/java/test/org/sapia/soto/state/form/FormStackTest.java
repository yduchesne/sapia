/*
 * FormStackTest.java
 * JUnit based test
 *
 * Created on September 3, 2005, 7:53 AM
 */

package org.sapia.soto.state.form;

import junit.framework.*;
import java.util.Stack;

/**
 *
 * @author yduchesne
 */
public class FormStackTest extends TestCase {
  
  public FormStackTest(String testName) {
    super(testName);
  }

  /**
   * Test of retrieve method, of class org.sapia.soto.state.form.FormStack.
   */
  public void testRetrieve() {
    FormStack forms = new FormStack(new IdFactory());
    Form f = forms.create();
    super.assertEquals(f, forms.retrieve(f.getId()));    
  }

  /**
   * Test of peek method, of class org.sapia.soto.state.form.FormStack.
   */
  public void testPeek() {
    FormStack forms = new FormStack(new IdFactory());
    super.assertTrue(forms.peek() == null);
    Form f = forms.create();
    super.assertEquals(f, forms.peek());
  }

  /**
   * Test of pop method, of class org.sapia.soto.state.form.FormStack.
   */
  public void testPop() {
    FormStack forms = new FormStack(new IdFactory());
    super.assertTrue(forms.peek() == null);
    Form f = forms.create();
    super.assertEquals(f, forms.peek());    
  }

  /**
   * Test of cancel method, of class org.sapia.soto.state.form.FormStack.
   */
  public void testCancel() {
    FormStack forms = new FormStack(new IdFactory());
    Form f0 = forms.create();
    Form f1 = forms.create();
    Form f2 = forms.create();
    super.assertEquals(f2, forms.peek());        
    forms.cancel(f1.getId());
    super.assertEquals(f0, forms.peek());            
  }

}
