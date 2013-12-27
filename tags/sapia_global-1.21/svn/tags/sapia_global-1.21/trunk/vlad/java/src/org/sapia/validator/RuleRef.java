package org.sapia.validator;


/**
 * This class models a reference to a <code>Rule</code> instance. The
 * reference links to the associated rule through the latter's ID - which
 * must be specified in such a case.
 *
 * @see org.sapia.validator.Rule#getId()
 *
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class RuleRef implements Validatable {
  private String _ref;

  /**
   * Constructor for RuleRef.
   */
  public RuleRef() {
    super();
  }

  /**
   * Sets the identifier of the rule to which this instance refers.
   *
   * @param ref the identifier of the referred <code>Rule</code>.
   */
  public void setId(String ref) {
    _ref = ref;
  }

  /**
   * Returns the identifier of the rule to which this instance refers.
   *
   * @return the identifier of the referred <code>Rule</code>.
   */
  public String getId() {
    return _ref;
  }

  /**
   * @see org.sapia.validator.Validatable#validate(ValidationContext)
   */
  public void validate(ValidationContext ctx) {
    ctx.getConfig().getRule(_ref).validate(ctx);
  }
}
