package org.sapia.validator;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * This abstract rule class provides the behavior that handles
 * dynamic attributes (using the BeanUtils library from the
 * Jakarta project).
 * <p>
 * This class allows to set the name of an accessor that will be
 * used against the current object in the validation context
 * stack. The accessor is invoked on the object, and the return
 * value will be the object that is validated. If no accessor is
 * specified, the current object on the stack will be validated.
 * <p>
 * If there is no object on the stack, or if the object returned
 * by the accessor is <code>null</code>, an instance of this class
 * can optionally throw an <code>IllegalStateException</code>; for this
 * to occur, inheriting classes must call this class' 
 * <code>throwExceptionOnNull(true)</code> method.
 * <p>
 * Classes inheriting from this class must implement the <code>doValidate()</code>
 * method, which is passed the object to validate. This implies that implementations
 * must "know" what type of object to expect as a parameter.
 * 
 * @see #validate(ValidationContext)
 * @see #doValidate(Object)
 * @see #throwExceptionOnNull(boolean)
 * 
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public abstract class BeanRule extends Rule {
  protected String _attribute;
  private boolean  _throw;
  
  /**
   * Sets the name of the accessor that should be invoked on
   * the current object in the validation context's stack.
   * 
   * @param attr the name of the accessor to invoke.
   * @see ValidationContext
   */
  public void setAttribute(String attr) {
    _attribute = attr;
  }

  /**
   * If called with <code>true</code> as an argument, indicates
   * that this instance should throw an <code>IllegalStateException</code>
   * if there is no object on the validation context's stack, or
   * if the accessor invoked on the current object on stack returns
   * <code>null</code>.
   * 
   * @param throwExc if <code>true</code>, an <code>IllegalStateException</code>
   * will be thrown by this instance's <code>validate()</code> method if
   * there is no current object on stack, or if the accessor returns <code>null</code>.
   */
  protected void throwExceptionOnNull(boolean throwExc) {
    _throw = throwExc;
  }

  /**
   * If this instance was specified an accessor (through the 
   * <code>setAttribute()</code> method), the accessor is invoked
   * on the current object in the given validation context.
   * <p>
   * If no accessor was specified, the current object remains.
   * <p>
   * If the current object is null (no object in stack), or if the accessor returns 
   * <code>null</code>, and if the instance's <code>throwExceptionOnNull(true)</code>
   * method was called, then this method throws an <code>IllegalStateException</code>.
   * <p>
   * If no problem is detected, this method calls its <code>doValidate()<code> method,
   * passing it either: a) the current object on the stack; or b) the object returned
   * by the specified accessor.
   * <p>
   * If the <code>doValidate()</code> method returns <code>false</code>, this instance
   * signals a validation error to the validation context's status.
   * 
   * @see #setAttribute(String)
   * @see #doValidate(Object)
   * 
   * @see org.sapia.validator.Rule#validate(ValidationContext)
   */
  public final void validate(ValidationContext context) {
    Object toValidate;

    if (_attribute == null) {
      toValidate = context.get();

      if ((toValidate == null) && _throw) {
        throw new IllegalStateException("No object on validation context stack at " + qualifiedName());
      }
    } else {
      try {
        toValidate = PropertyUtils.getProperty(context.get(), _attribute);

        if ((toValidate == null) && _throw) {
          throw new IllegalStateException("Attribute " + _attribute
            + " evaluates to null on rule " + qualifiedName());
        }
      } catch (Throwable err) {
        context.getStatus().error(this, err);

        return;
      }
    }

    if (!doValidate(toValidate)) {
      context.getStatus().error(this);
    }
  }

  /**
   * This template method must be implemented by inheriting classes, and is called
   * by this class' <code>validate()</code>.
   * <p>
   * Inheriting classes should implement their validation logic in this method, and
   * return <code>false</code> if the validation fails. 
   * 
   * @param toValidate the object to validate.
   *
   * @return <code>false</code> if the validation logic implemented by this method
   * detects a validation error.
   */
  protected abstract boolean doValidate(Object toValidate);
}
