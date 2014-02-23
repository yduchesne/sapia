package org.sapia.dataset;

import org.sapia.dataset.func.ArgFunction;
import org.sapia.dataset.util.Checks;
import org.sapia.dataset.util.Objects;
import org.sapia.dataset.util.Strings;

/**
 * An instance of this class allows to create keys over collections of {@link Vector}s.
 * It will be used to match some of the values of these vectors, given a specified column
 * set.
 * 
 * @author yduchesne
 *
 */
public class VectorKey implements Comparable<VectorKey> {

  private ColumnSet columns;
  private Object[] values;
  
  /**
   * @param columns the {@link ColumnSet} describing the vector structure.
   * @param columnValues the {@link Vector} whose values are to be used.
   */
  public VectorKey(ColumnSet columns, Vector columnValues) {
    this.columns = columns;
    values = new Object[columns.size()];
    int i = 0;
    for (Column col : columns) {
      values[i++] = columnValues.get(col.getIndex());
    }
  }
  
  public VectorKey(ColumnSet columns, Object[] columnValues) {
    this.columns = columns;
    values = new Object[columnValues.length];
    for (int i = 0; i < columnValues.length; i++) {
      values[i] = columnValues[i];
    }
  }


  /**
   * @return this instance's {@link ColumnSet}.
   */
  public ColumnSet getColumnSet() {
    return columns;
  }
  
  /**
   * @param index an index.
   * @return the value at the given index.
   */
  public Object get(int index) {
    Checks.bounds(index, values);
    return values[index];
  }
  
  /**
   * @return a copy of this instance's values.
   */
  public Object[] getValues() {
    Object[] toReturn = new Object[values.length];
    System.arraycopy(values, 0, toReturn, 0, values.length);
    return toReturn;
  }
  
  /**
   * @return this instance's size.
   */
  public int size() {
    return values.length;
  }
  
  /**
   * @return this instancde's hash code.
   */
  @Override
  public int hashCode() {
    return Objects.safeHashCode(values);
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof VectorKey) {
      VectorKey other = (VectorKey) obj;
      if (this.values.length != other.values.length) {
        return false;
      }
      for (int i = 0; i < this.values.length; i++) {
        if(!Objects.safeEquals(values[i], other.values[i])) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
  
  @Override
  public int compareTo(VectorKey o) {
    int cmp = 0;
    for (int i = 0; i < columns.size(); i ++) {
      Column col = columns.get(i);
      Object v1 = values[i];
      Object v2 = o.values[i];
      Checks.isTrue(col.getType().strategy().isAssignableFrom(v2), "Value %s not a %s. Cannot be compared with %s", v2, col.getType(), v1);
      cmp = col.getType().strategy().compareTo(v1, v2);
      if (cmp != 0) {
        return cmp;
      }
    }
    return cmp;
  }
  
  @Override
  public String toString() {
    return Strings.toString(this.values, new ArgFunction<Object, String>() {
      public String call(Object arg) {
        return arg != null ? arg.toString() : null;
      }
    });
  }
}
