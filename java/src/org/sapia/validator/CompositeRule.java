package org.sapia.validator;

import java.util.ArrayList;
import java.util.List;

import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectHandlerIF;

/**
 * An instance of this class is intended to hold other validation rules.
 *
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class CompositeRule extends Rule implements ObjectHandlerIF {
  private List      _rules = new ArrayList();
  protected boolean _stop = true;

  /**
   * Constructor for CompositeRule.
   */
  public CompositeRule() {
  }

  /**
   * Adds a <code>Validatable</code> to this instance.
   *
   * @param a <code>Validatable</code>.
   */
  public void addValidatable(Validatable v) {
    _rules.add(v);
  }

  /**
   * Sets the "stop" flag to <code>true</code> or <code>false</code>. This
   * flag meaning is as follows: when an instance of this class processes
   * its nested rules, it will continue with the next rule even if the
   * previous one generated an error, provided this flag is set to
   * <code>false</code>.
   * <p>
   * This feature allows for subsequent rules to be processed even if
   * validation errors have previously been created; thus, in such a case,
   * the <code>ValidationContext</code> could potentially contain more
   * than one instance of <code>ValidationErr</code>.
   *
   * @param stop this instance's "stop" flag.
   * @see ValidationContext
   * @see ValidationErr
   */
  public void setStop(boolean stop) {
    _stop = stop;
  }

  /**
   * Creates a rule set and returns it.
   *
   * @return a <code>RuleSet</code>.
   */
  public RuleSet createRuleSet() {
    RuleSet rs = new RuleSet();

    _rules.add(rs);

    return rs;
  }

  /**
   * @see org.sapia.util.xml.confix.ObjectHandlerIF#handleObject(String, Object)
   */
  public void handleObject(String name, Object validatable)
    throws ConfigurationException {
    if (validatable instanceof Validatable) {
      addValidatable((Validatable) validatable);
    } else {
      throw new ConfigurationException("Unexpected element: " + name + " at " + qualifiedName());
    }
  }

  /**
   * @see org.sapia.validator.Rule#validate(ValidationContext)
   */
  public void validate(ValidationContext ctx) {
    Validatable current;
    boolean     valid = true;

    for (int i = 0; i < _rules.size(); i++) {
      current = (Validatable) _rules.get(i);

      current.validate(ctx);

      if (ctx.getStatus().isError() && _stop) {
        break;
      }
    }
  }
}
