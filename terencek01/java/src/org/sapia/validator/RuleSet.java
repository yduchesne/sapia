package org.sapia.validator;

import java.util.ArrayList;
import java.util.List;

import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectHandlerIF;

/**
 * This class models a set of rules
 *
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class RuleSet implements Validatable, ObjectHandlerIF {
  private List    _rules = new ArrayList();
  private String  _id;
  private boolean _stop;

  /**
   * Constructor for RuleSet.
   */
  public RuleSet() {
    super();
  }

  /**
   * Sets this instance's identifier.
   *
   * @param an identifier, as a string.
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
   * Adds a <code>Validatable</code> to this instance.
   *
   * @param v a <code>Validatable</code> instance.
   */
  public void addValidatable(Validatable v) {
    _rules.add(v);
  }

  /**
   * Adds a rule set to this instance and returns it.
   *
   * @return a <code>RuleSet</code> instance.
   */
  public RuleSet createRuleSet() {
    RuleSet set = new RuleSet();

    _rules.add(set);

    return set;
  }

  /**
   * @see CompositeRule#setStop(boolean)
   */
  public void setStop(boolean stop) {
    _stop = stop;
  }

  /**
   * @see org.sapia.validator.Validatable#validate(ValidationContext)
   */
  public final void validate(ValidationContext ctx) {
    Validatable current;

    for (int i = 0; i < _rules.size(); i++) {
      current = (Validatable) _rules.get(i);

      current.validate(ctx);

      if (ctx.getStatus().isError() && _stop) {
        break;
      }
    }
  }

  /**
   * @see org.sapia.util.xml.confix.ObjectHandlerIF#handleObject(String, Object)
   */
  public void handleObject(String name, Object validatable)
    throws ConfigurationException {
    if (validatable instanceof Validatable) {
      addValidatable((Validatable) validatable);
    } else {
      throw new ConfigurationException("Unexpected element: " + name);
    }
  }
}
