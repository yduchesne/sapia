package org.sapia.validator.rules;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.sapia.validator.BeanRule;

/**
 * A rule that validates a given Perl5 pattern.
 * 
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class RegEx extends BeanRule{
  
  private Pattern   _patternObj;

  /**
   * Constructor for RegExp.
   */
  public RegEx() {
    super.throwExceptionOnNull(true);
  }
  
  /**
   * Sets the Perl5 pattern to use for validation.
   * 
   * @param pattern a Perl5 pattern.
   */
  public void setPattern(String pattern){
      _patternObj = Pattern.compile(pattern);    
  }
  
  /**
   * @see org.sapia.validator.BeanRule#doValidate(Object)
   */
  protected boolean doValidate(Object toValidate) {
    if(_patternObj == null){
      throw new IllegalStateException("'pattern' attribute not defined on regExp rule at " + qualifiedName());
    }      
    Matcher matcher = _patternObj.matcher(toValidate.toString());
    return matcher.matches();
  }


}
