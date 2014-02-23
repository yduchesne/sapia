package org.sapia.dataset.computation;

import java.util.HashMap;
import java.util.Map;

import org.sapia.dataset.Column;
import org.sapia.dataset.ColumnSet;
import org.sapia.dataset.func.NoArgFunction;
import org.sapia.dataset.util.Checks;
import org.sapia.dataset.value.Value;

/**
 * Keeps arbitrary results (of computations for example) on a per-column basis.
 * 
 * @author yduchesne
 *
 */
public class ComputationResult {

  private ColumnSet columns;
  private Map<Column, Value> results = new HashMap<>();
  
  public ComputationResult(ColumnSet columns) {
    this.columns = columns;
  }
  
  /**
   * @return this intance's {@link ColumnSet}.
   */
  public ColumnSet getColumnSet() {
    return columns;
  }

  /**
   * @param col a {@link Column}.
   * @param value the value to associate to the given column.
   * @return this instance.
   */
  public ComputationResult set(Column col, Value value) {
    Checks.isTrue(columns.get(col.getName()) != null, "No column found for: %s", col.getName());
    this.results.put(col, value);
    return this;
  }
  
  /**
   * @param colName a {@link Column}.
   * @return the value associated to the given column.
   */
  public Value get(Column col) {
    return results.get(col);
  }
  
  /**
   * @param colName a {@link Column}.
   * @param defaultValue a default value which will be returned if no value is found for
   * the given column.
   * @return the value associated to the given column.
   */
  @SuppressWarnings("unchecked")
  public <V extends Value> V get(Column col, NoArgFunction<V> defaultVal) {
    V value = (V) results.get(col);
    if (value == null) {
      value = defaultVal.call();
      results.put(col, value);
    }
    return value;
  }

  /**
   * @param col the {@link Column}  whose corresponding value should be returned.
   * @param defaultVal the default value to return, if no value could be found for the
   * given column.
   * @return a {@link Value}.
   */
  @SuppressWarnings("unchecked")
  public <V extends Value> V get(Column col, V defaultVal) {
    V value = (V) results.get(col);
    if (value == null) {
      return defaultVal;
    }
    return value;
  }
  
  /**
   * @param colName a {@link Column}.
   * @return the value associated to the given column.
   */
  public Value getNotNull(Column col) {
    return Checks.notNull(results.get(col), "Not result found for column: %s", col.getName());
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (Map.Entry<Column, Value> entry : results.entrySet()) {
      builder.append(entry.getKey().getName()).append(": ").append(entry.getValue()).append(System.lineSeparator());
    }
    builder.append(System.lineSeparator());
    return builder.toString();
  }
}
