package org.sapia.validator.rules;

import org.apache.commons.beanutils.PropertyUtils;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectHandlerIF;
import org.sapia.validator.Rule;
import org.sapia.validator.Validatable;
import org.sapia.validator.ValidationContext;

/**
 * A rule implementation that will delegate its <code>validate()</code>
 * call to its encapsulated rule, if the current object on the validation
 * context's stack is not <code>null</code>, or if the accessor that is
 * specified does not return <code>null</code>.
 *
 * @see #setAttribute(String)
 *
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class IfDefined extends Rule implements ObjectHandlerIF{
  private String      _attribute;
  private Validatable _validatable;

  /**
   * Constructor for If.
   */
  public IfDefined() {
  }

  /**
   * Sets the name of the accessor that will be used to retreive
   * a given value from the current object on the validation context's stack.
   *
   * @param attr the name of an accessor.
   */
  public void setAttribute(String attr) {
    _attribute = attr;
  }

  /**
   * @see org.sapia.util.xml.confix.ObjectHandlerIF#handleObject(String, Object)
   */
  public void handleObject(String anElementName, Object anObject)
    throws ConfigurationException {
    if (_validatable != null) {
      throw new IllegalArgumentException(
        qualifiedName() + " rule can only take one rule/ruleset");
    }

    if (!(anObject instanceof Validatable)) {
      throw new IllegalArgumentException(
        qualifiedName() + " rule only takes a validatable object");
    }

    _validatable = (Validatable) anObject;
  }

  /**
   * @see org.sapia.validator.CompositeRule#validate(ValidationContext)
   */
  public void validate(ValidationContext ctx) {
    Object item;

    if (_attribute == null) {
      item = ctx.get();
    } else {
      try {
        if (ctx.get() == null) {
          throw new IllegalStateException(
            "No object on validation context stack at " + qualifiedName());
        }

        item = PropertyUtils.getProperty(ctx.get(), _attribute);
      } catch (Throwable err) {
        ctx.getStatus().error(this, err);

        return;
      }
    }

    if ((_validatable != null) && (item != null)) {
      _validatable.validate(ctx);
    }
  }
}
