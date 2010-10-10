package org.sapia.validator.rules;

import org.apache.commons.beanutils.PropertyUtils;
import org.sapia.validator.CompositeRule;
import org.sapia.validator.ValidationContext;

import java.util.Collection;
import java.util.Iterator;

/**
 * Aggregates one-to-many nested validatable(s), iterating over the items
 * in a collection/iterator/array and passing each item to the
 * validate method of the nested validatable(s).
 *
 * @see org.sapia.validator.Validatable
 * @see org.sapia.validator.Validatable#validate(ValidationContext)
 *
 * @author Yanick Duchesne
 * <dl>
 * <dt><b>Copyright:</b><dd>Copyright &#169; 2002-2003 <a href="http://www.sapia-oss.org">Sapia Open Source Software</a>. All Rights Reserved.</dd></dt>
 * <dt><b>License:</b><dd>Read the license.txt file of the jar or visit the
 *        <a href="http://www.sapia-oss.org/license.html">license page</a> at the Sapia OSS web site</dd></dt>
 * </dl>
 */
public class ForEach extends CompositeRule {
  private String  _attribute;
  private boolean _ignoreNull;

  /**
   * Constructor for ForEach.
   */
  public ForEach() {
    super();
  }

  /**
   * Sets the name of the accessor that will be used to retreive
   * a collection or array to validate from the current object on the
   * validation context's stack.
   *
   * @param attr the name of an accessor.
   */
  public void setAttribute(String attr) {
    _attribute = attr;
  }

  /**
   * @see org.sapia.validator.CompositeRule#validate(ValidationContext)
   */
  public void validate(ValidationContext ctx) {
    Object   obj;
    Iterator items;

    if (_attribute == null) {
      obj = ctx.get();

      if (obj == null) {
        throw new IllegalStateException("No object on validation context stack at " + qualifiedName());
      }
    } else {
      try {
        if (ctx.get() == null) {
          throw new IllegalStateException(
            "No object on validation context stack at " + qualifiedName());
        }

        obj = PropertyUtils.getProperty(ctx.get(), _attribute);

        if (obj == null) {
          throw new IllegalStateException("Attribute " + _attribute
            + " evaluates to null at " + qualifiedName());
        }
      } catch (Throwable err) {
        ctx.getStatus().error(this, err);

        return;
      }
    }

    if (obj instanceof Collection) {
      items = ((Collection) obj).iterator();
    } else if (obj instanceof Object[]) {
      items = new ArrayIterator((Object[]) obj);
    } else if (obj instanceof Iterator) {
      items = (Iterator) obj;
    } else {
      throw new IllegalStateException(
        "forEach rule processes instances of java.util.Collection, java.util.Iterator, or arrays at " + qualifiedName());
    }

    if (items == null) {
      return;
    }

    Object  item;
    boolean valid = true;

    for (; items.hasNext();) {
      item = items.next();
      ctx.push(item);
      super.validate(ctx);

      if (ctx.getStatus().isError() && _stop) {
        ctx.pop();

        break;
      }

      ctx.pop();
    }
  }

  static class ArrayIterator implements Iterator {
    int      _count;
    Object[] _array;

    ArrayIterator(Object[] array) {
      _array = array;
    }

    /**
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
      return _count < _array.length;
    }

    /**
     * @see java.util.Iterator#next()
     */
    public Object next() {
      return _array[_count++];
    }

    /**
     * @see java.util.Iterator#remove()
     */
    public void remove() {
    }
  }
}
