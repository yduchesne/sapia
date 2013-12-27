package org.sapia.soto.state.cocoon.util;

import java.io.Serializable;

import java.util.Stack;

/**
 * This class implements a stack of objects that act as data holders for forms.
 * The objects are pushed onto the stack successively, if required (a form might
 * need input that comes from a selection of another form's output). <p/>An
 * instance of this class can be kept in a HTTP session. It holds the forms that
 * are currently "active" for a given user. <p/>A form is pushed onto this
 * instance when the corresponding form page is first displayed. Typically, the
 * form object that is put onto the stack becomes a model that is displayed in
 * the view. <p/>If the form is displayed again after submission (such as when
 * required input data is missing), the current form object is used to display
 * the data that has already been entered. <p/>Once submitted or cancelled, the
 * current form object should be popped from the stack. <p/>Typically,
 * instances of the <code>Form</code> class are used with an instance of this
 * class.
 * 
 * @see org.sapia.soto.state.cocoon.util.Form
 * @see org.sapia.soto.state.util.FormStep
 * 
 * @author Yanick Duchesne
 * 
 * <dl>
 * <dt><b>Copyright: </b>
 * <dd>Copyright &#169; 2002-2004 <a href="http://www.sapia-oss.org">Sapia Open
 * Source Software </a>. All Rights Reserved.</dd>
 * </dt>
 * <dt><b>License: </b>
 * <dd>Read the license.txt file of the jar or visit the <a
 * href="http://www.sapia-oss.org/license.html">license page </a> at the Sapia
 * OSS web site</dd>
 * </dt>
 * </dl>
 */
public class Forms implements Serializable {
  private Stack _forms = new Stack();

  /**
   * @param form
   *          a <code>Object</code>.
   */
  public void pushForm(Object form) {
    _forms.push(form);
  }

  /**
   * @return the "current" form <code>Object</code>, or <code>null</code>
   *         if there is no form in this instance - the returned form is removed
   *         from this instance.
   */
  public Object popForm() {
    if(_forms.size() == 0) {
      return null;
    }

    return _forms.pop();
  }

  /**
   * @return the "current" form <code>Object</code>, or <code>null</code>
   *         if there is no form in this instance.
   */
  public Object peekForm() {
    if(_forms.size() == 0) {
      return null;
    }

    return _forms.peek();
  }

  /**
   * clears all form objects that this instance holds.
   */
  public void clear() {
    _forms.clear();
  }
}
