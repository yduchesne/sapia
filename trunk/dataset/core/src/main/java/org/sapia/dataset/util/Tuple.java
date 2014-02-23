package org.sapia.dataset.util;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.sapia.dataset.func.ArgFunction;

/**
 * An instance of this class holds values in predefined field slots, addressed by index.
 * 
 * @author yduchesne
 *
 */
public class Tuple implements Iterable<Object> {

  private Object[] values;
  
  /**
   * @param values the values that this instance should hold.
   */
  public Tuple(Object...values) {
    this.values = values;
  }
  
  /**
   * @param values the values that this instance should hold.
   */
  public Tuple(List<Object> values) {
    this(values.toArray(new Object[values.size()]));
  }
  
  /**
   * @return the number of fields that this tuple has.
   */
  public int length() {
    return values.length;
  }
  
  /**
   * @param index the index of the tuple value to return.
   * @return the value at the given index, or <code>null</code> if no such value exists at that index.
   */
  public Object get(int index) {
    Checks.isFalse(index < 0 || index >= values.length, "Invalid index: %s. Got %s fields", index, values.length);
    return values[index];
  }
  
  /**
   * @param index the index of the tuple field value to return.
   * @return the value at the given index.
   * @throws IllegalArgumentException if no value exists at the given index.
   */
  public Object getNotNull(int index) throws IllegalArgumentException {
    return Checks.notNull(get(index), "Value is null at index %s", index);
  }
  
  /**
   * @param index the index of the tuple field value to return.
   * @param type the expected type of the field value.
   * @return the value at the given index, or <code>null</code> if no such value
   * exists at the given index.
   */
  public <T> T get(int index, Class<T> type) {
    Object value = get(index);
    if (value != null) {
      return type.cast(value);
    }
    return null;
  }

  /**
   * @param index the index of the tuple field value to return.
   * @param type the expected type of the field value.
   * @return the value at the given index.
   * @throws IllegalArgumentException if no value exists at the given index.
   */
  public <T> T getNotNull(int index, Class<T> type) throws IllegalArgumentException {
    return type.cast(getNotNull(index));
  }
  
  /**
   * @param type the type of the value that is expected.
   * @return the value of the given type that this instance holds.
   * @throws IllegalArgumentException if more than one value of the given type could be found.
   */
  @SuppressWarnings("unchecked")
  public <T> T get(Class<T> type) {
    T value = null;
    for (int i = 0; i < values.length; i++) {
      Object v = values[i];
      if (v != null && type.isAssignableFrom(v.getClass())) {
        Checks.isTrue(
            value == null, 
            "Ambiguous type: %s. More than one values of that type found %s", 
            type.getName(),
            Strings.toString(
                values, 
                new ArgFunction<Object, String>() {
                  @Override
                  public String call(Object arg) {
                    return arg != null ? arg.toString() : "null";
                  }
                }
            )
        );
        value = (T) v;
      }
    }
    return value;
  }
  
  /**
   * @param type the type of the value that is expected.
   * @return the value of the given type that this instance holds.
   * @throws IllegalArgumentException if more than one value (or none) 
   * of the given type could be found.
   */
  public <T> T getNotNull(Class<T> type) throws IllegalArgumentException {
    return Checks.notNull(get(type), "No value found for type: %s", type.getName());
  }
  
  @Override
  public Iterator<Object> iterator() {
    return new Iterator<Object>() {
      private int index;
      
      public boolean hasNext() {
        return index < values.length;
      }
      
      @Override
      public Object next() {
        if (index >= values.length) {
          throw new NoSuchElementException();
        }
        return values[index++];
      }
      
      @Override
      public void remove() {
      }
      
    };
  }
  
  @Override
  public String toString() {
    return Strings.toString(values, new ArgFunction<Object, String>() {
      @Override
      public String call(Object arg) {
        return arg != null ? arg.toString() : "null";
      }
    });
  }
}
