package org.sapia.validator.rules;

import org.apache.commons.beanutils.PropertyUtils;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectHandlerIF;
import org.sapia.validator.*;

/**
 * Selects the value corresponding to the accessor specified on an instance
 * of this class and pushes it on the validation context stack.
 *
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class Select extends Rule implements ObjectHandlerIF {
  private String      _attribute;
  private Validatable _validatable;

  /**
   * Constructor for Select.
   */
  public Select() {
    super();
  }

  /**
   * Specifies the name of the accessor to invoke, and whose return
   * value should be pushed onto the validation context's stack.
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
        qualifiedName() + "  rule only takes a validatable object");
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

      if (item == null) {
        throw new IllegalStateException("No object on validation context stack at " + qualifiedName());
      }
    } else {
      try {
        if (ctx.get() == null) {
          throw new IllegalStateException(
            "No object on validation context stack at " + qualifiedName());
        }

        item = PropertyUtils.getProperty(ctx.get(), _attribute);
        
        if (item == null) {
          throw new IllegalStateException("Attribute " + _attribute
            + " evaluates to null at " + qualifiedName());
        }
      } catch (Throwable err) {
        ctx.getStatus().error(this, err);

        return;
      }
    }
    
    if (_validatable != null) {
      ctx.push(item);
      _validatable.validate(ctx);
      ctx.pop();
    }
  }
}
