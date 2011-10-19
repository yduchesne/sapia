/*
 * ValidationService.java
 *
 * Created on September 9, 2005, 3:00 PM
 *
 */

package org.sapia.soto.validation;

import java.util.Locale;
import org.sapia.validator.Status;

/**
 * This interface specifies the behavior of a validation service based
 * on the <a href="http://www.sapia-oss.org/projects/vlad/" target="vlad">Vlad</code> framework.
 *
 * @author yduchesne
 */
public interface ValidationService {
  
  
  /**
   * @param validatorName the name of the validator holding the specified rule set.
   * @param ruleSetId the identifier of the Vlad rule set that will process
   * the object to validate.
   * @param toValidate the <code>Object</code> to validate.
   * @param locale a <code>Locale</code>
   *
   * @throws Exception a problem occurs while performing the validation.
   * the given validator name.
   *
   * @return a validation <code>Status</code>
   */
  public Status validate(String validatorName, 
                         String ruleSetId, 
                         Object toValidate,
                         Locale locale)
                         throws Exception;
}
