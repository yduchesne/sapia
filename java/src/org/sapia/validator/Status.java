package org.sapia.validator;

import java.util.ArrayList;
import java.util.List;

/**
 * Models a validation status. An instance of this class is encapsulated
 * by a <code>ValidationContext</code>. It holds the current validation
 * errors (if any).
 * <p>
 * From within the <code>Validate</code> method of a <code>Rule</code>, validation
 * errors must be signaled through one of this class' <code>error()</code>
 * methods, as in the following:
 * <pre>
 * // 'this' is the current Rule instance.
 * ...
 * public void validate(ValidationContext ctx){
 *   ctx.getStatus().error(this);
 * }
 * ...
 * </pre>
 *
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class Status {
  private ValidationContext _ctx;
  private List              _validationErrs = new ArrayList();

  /**
   * Constructor for Status.
   */
  public Status(ValidationContext ctx) {
    _ctx = ctx;
  }
  
  /**
   * Registers a validation error for the given rule.
   * 
   * @param r a <code>Rule</code>.
   * @see ValidationErr
   */
  public void error(Rule r) {
    String msg = r.getErrorMessageFor(_ctx.getLocale());
    String id = r.getId();
    addError(new ValidationErr(id, msg));
  }
  
  /**
   * Registers an error for the given <code>Rule</code>. Internally
   * creates a <code>ValidationException</code> that is assigned
   * to a <code>ValidationErr</code> that is added to this instance's list
   * of <code>ValidationErr</code>s.
   * 
   * @see ValidationErr
   */
  public void error(Rule r, String msg) {
    String id = r.getId();
    addError(new ValidationErr(id, new ValidationException(msg, id)));
  }
  
  /**
   * Registers an error for the given <code>Rule</code>. The <code>Throwable</code>
   * instance is is assigned to a <code>ValidationErr</code> that is added to this instance's list
   * of <code>ValidationErr</code>s.
   * 
   * @see ValidationErr
   */
  public void error(Rule r, Throwable err) {
    String id = r.getId();
    addError(new ValidationErr(id, err));
  }
  
  /**
   * Returns this instance's list of validation errors.
   * 
   * @return a <code>List</code> of <code>ValidationErr</code>.
   */
  public List getErrors() {
    return _validationErrs;
  }
  
  /**
   * Returns <code>true</code> if this instance contains one or more
   * validation errors.
   * 
   * @return <code>true</code> if this instance contains one or more
   * validation errors.
   */  
  public boolean isError() {
    return _validationErrs.size() > 0;
  }
  
  /**
   * @param err a <code>ValidationErr</code>
   */
  public void addError(ValidationErr err){
    _validationErrs.add(err);
  }
  
  /**
   * @param errs a <code>List</code> of <code>ValidationErr</code>s.
   */
  public void addErrors(List errs){
    for(int i = 0; i < errs.size(); i++){
      _validationErrs.add((ValidationErr)errs.get(i));
    }
  }  
  
  /**
   * Returns the validation errors that this instance holds, and whose 
   * identifier starts with the given one. The returned error objects are at 
   * the same time removed from this instance.
   *
   * @return a <code>List</code> of <code>ValidationErr</code>s.
   */
  public List removeErrorsFor(String id){
    List toReturn = new ArrayList();
    for(int i = 0; i < _validationErrs.size(); i++){
      ValidationErr err = (ValidationErr)_validationErrs.get(i);
      if(err.getId() != null && err.getId().startsWith(id)){
        toReturn.add(err);
        _validationErrs.remove(i--);
      }
    }
    return toReturn;
  }
  
  /**
   * Returns the validation errors that this instance holds. The returned
   * error objects are at the same time removed from this instance.
   *
   * @return a <code>List</code> of <code>ValidationErr</code>s.
   */
  public List removeErrors(){
    List lst = new ArrayList(_validationErrs);
    _validationErrs.clear();
    return lst;
  }

  /**
   * Returns the validation errors that do not have an ID defined. 
   * The returned error objects are at the same time removed from this instance.
   * 
   * @return a <code>List</code> of <code>ValidationErr</code>s.  
   */
  public List removeAnonymousErrors(){
    List toReturn = new ArrayList();
    for(int i = 0; i < _validationErrs.size(); i++){
      ValidationErr err = (ValidationErr)_validationErrs.get(i);
      if(err.getId() == null){
        toReturn.add(err);
        _validationErrs.remove(i--);
      }
    }
    return toReturn;
  }
}
