package org.sapia.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

/**
 * This class models a validation rule.
 *
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public abstract class Rule implements Validatable, ObjectCreationCallback{
  private String        _id;
  private ErrorMessages _msgs = new ErrorMessages();
  private List          _tmp = new ArrayList();
  private String        _prefix, _localName;
  
  /**
   * Constructor for Rule.
   */
  public Rule() {
  }

  /**
   * Inits rule with the specified information.
   * 
   * @param this rule's namespace prefix.
   * @param this rule's local name.
   */  
  public void initName(String prefix, String localName){
    _prefix    = prefix;
    _localName = localName;
  }
  
  /**
   * @see #getId()
   */
  public void setId(String id) {
    _id = id;
  }

  /**
   * @see Validatable#getId()
   */
  public String getId() {
    return _id;
  }
  
  /**
   * Adds an error message to this instance and returns it.
   *
   * @return the created <code>ErrorMessage</code>.
   */
  public ErrorMessage createMessage() {
    ErrorMessage msg = new ErrorMessage();
    _tmp.add(msg);
    return msg;
  }

  /**
   * Sets this instance's error messages.
   * 
   * @param msgs some <code>ErrorMessages</code>.
   */
  public void setErrorMessages(ErrorMessages msgs){
    _msgs = msgs;
  }
  
  /**
   * @return this instance's <code>ErrorMessages</code>.
   */
  public ErrorMessages getErrorMessages(){
    return _msgs;
  }

  /**
   * Returns the error message corresponding to the
   * given <code>Locale</code>.
   *
   * @param a <code>Locale</code>.
   * @return an error message, or <code>null</code> if no error message could
   * be found for the given <code>Locale</code>.
   */
  public String getErrorMessageFor(Locale loc) {
    ErrorMessage msg = _msgs.getErrorMessageFor(loc);

    if (msg != null) {
      return msg.getValue();
    } else {
      return null;
    }
  }

  /**
   * @see Validatable#validate(ValidationContext)
   */
  public abstract void validate(ValidationContext context);
  
  /**
   * Returns the local name of this rule.
   * 
   * @return this instance's local name.
   */
  protected String localName(){
    return _localName;
  }
  
  /**
   * Returns the prefix of the namespace to which this rule belongs.
   * 
   * @return this instance's namespace prefix.
   */  
  protected String prefix(){
    return _prefix;
  }
  
  /**
   * Returns this rule's qualified name (prefix() + ':' + localName()).
   * 
   * @see #prefix()
   * @see #localName()
   * 
   * @return the qualified name of this rule.
   */
  protected String qualifiedName(){
    if(_prefix != null && _localName != null){
      return _prefix + ':' + _localName;
    }
    else{
      return getClass().getName();
    }
  }
  
  public Object onCreate() throws ConfigurationException{
    for(int i = 0; i < _tmp.size(); i++){
      _msgs.addErrorMessage((ErrorMessage)_tmp.get(i));
    }
    _tmp = null;
    return this;
  }
}
