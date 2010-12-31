package org.sapia.validator;


/**
 * This class models a reference to a <code>RuleSet</code>.
 *
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class RuleSetRef implements Validatable {
  private String _ref;

  /**
   * Constructor for RuleSetRef.
   */
  public RuleSetRef() {
  }

  /**
   * Sets the identifier of the rule set to which this instance
   * refers.
   *
   * @param ref the identifier of the referred <code>RuleSet</code>.
   */
  public void setId(String ref) {
    _ref = ref;
  }

  /**
   * Returns the identifier of the rule set to which this instance
   * refers.
   *
   * @return the identifier of the <code>RuleSet</code> to which this instance
   * refers.
   */
  public String getId() {
    return _ref;
  }

  /**
   * @see org.sapia.validator.Validatable#validate(ValidationContext)
   */
  public void validate(ValidationContext ctx) {
    ctx.getConfig().getRuleSet(_ref).validate(ctx);
  }
}
