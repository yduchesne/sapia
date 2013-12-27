package org.sapia.validator.rules;

import org.sapia.validator.Rule;
import org.sapia.validator.ValidationContext;

/**
 * @author Yanick Duchesne
 * 28-Apr-2003
 */
public class TestRule extends Rule {
  private boolean _wasCalled;
  private boolean _createError;

  /**
   * Constructor for TestRule.
   */
  public TestRule(boolean createError) {
    _createError = createError;
  }

  public void reset() {
    _wasCalled = false;
  }

  public boolean wasCalled() {
    return _wasCalled;
  }

  /**
   * @see org.sapia.validator.Rule#validate(ValidationContext)
   */
  public void validate(ValidationContext context) {
    _wasCalled = true;

    if (_createError) {
      context.getStatus().error(this);
    }
  }
}
