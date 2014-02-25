package org.sapia.dataset.transform.filter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mvel2.MVEL;
import org.sapia.dataset.Column;
import org.sapia.dataset.Dataset;
import org.sapia.dataset.Datatype;
import org.sapia.dataset.RowResult;
import org.sapia.dataset.Vector;
import org.sapia.dataset.algo.Criteria;
import org.sapia.dataset.func.ArgFunction;
import org.sapia.dataset.help.Doc;
import org.sapia.dataset.help.Example;
import org.sapia.dataset.impl.DefaultColumn;
import org.sapia.dataset.impl.DefaultDataset;
import org.sapia.dataset.impl.DefaultVector;
import org.sapia.dataset.transform.slice.Slices;
import org.sapia.dataset.util.Checks;
import org.sapia.dataset.value.NullValue;
import org.sapia.dataset.value.Value;

/**
 * Provides methods for filtering data.
 * 
 * @author yduchesne
 *
 */
@Doc("Provides methods for filtering data")
public class Filters {

  private Filters() {
  }
  
  /**
   * Removes <code>null</code> values from the given dataset, for the specified columns.
   * 
   * @param dataset the {@link Dataset} to filter.
   * @param columnNames the {@link List} of column names of the columns whose corresponding rows 
   * with <code>null</code> values should be removed.
   * @return a {@link Dataset} with the <code>null</code> values removed, for the specified
   * columns.
   */
  @Doc("Removes all rows with a null value in at least one of the columns specified")
  public static Dataset removeNulls(
      @Doc("a dataset") Dataset dataset, 
      @Doc("the name of the columns to check for null") List<String> columnNames) {
    final Set<String> nameSet = new HashSet<>(columnNames);
    return dataset.getSubset(new Criteria<RowResult>() {
      @Override
      public boolean matches(RowResult v) {
        for (String n : nameSet) {
          if (NullValue.isNull(v.get(n))) {
            return false;
          }
        }
        return true;
      }
    });
  }
  
  @Doc("Applies the given replacement function to the values of the specified column")
  public static Dataset replace(      
      @Doc("a dataset") Dataset dataset, 
      @Doc("the name of the column to process") String colName,
      @Doc("the datatype of the new values in the processed column") Datatype datatype,
      @Doc("the replacement function to use") ArgFunction<Object, Object> function) {
    
    List<Vector> newRows = new ArrayList<>();
    
    int colIndex = dataset.getColumnSet().get(colName).getIndex();
    for (Vector row : dataset) {
      Object[] newValues = new Object[row.size()];
      for (int i = 0; i < row.size(); i++) {
        if (i == colIndex) {
          Object val = function.call(row.get(i));
          if (datatype.strategy().isAssignableFrom(val)) {
            newValues[i] = val;
          } else {
            throw new IllegalArgumentException(String.format("Value %s cannot be assigned to new column type %s", datatype));
          }
        } else {
          newValues[i] = row.get(i);
        }
      }
      newRows.add(new DefaultVector(newValues));     
    }
    
    List<Column> newColumns = new ArrayList<>();
    for (int i = 0; i < dataset.getColumnSet().size(); i++) {
      if (i == colIndex) {
        newColumns.add(new DefaultColumn(i, datatype, colName));
      } else {
        newColumns.add(dataset.getColumnSet().get(i));
      }
    }
    
    return new DefaultDataset(newColumns, newRows);
  }
  
  
  /**
   * Removes <code>null</code> values from the given dataset, for the specified columns.
   * 
   * @param dataset the {@link Dataset} to filter.
   * @param columnNames the array of column names of the columns whose corresponding rows 
   * with <code>null</code> values should be removed.
   * @return a {@link Dataset} with the <code>null</code> values removed, for the specified
   * columns.
   */
  @Doc("Removes all rows with a null value in at least one of the columns specified")
  public static Dataset removeNulls(
      @Doc("a dataset") Dataset dataset, 
      @Doc("the name of the columns to check for null") String...columnNames) {
    return removeNulls(dataset, Arrays.asList(columnNames));
  }
  
  /**
   * Removes <code>null</code> values from the given dataset, for any column.
   * 
   * @param dataset the {@link Dataset} to filter.
   * @return a dataset, with any row having <code>null</code> values filtered out.
   */
  @Doc("Removes all rows with at least one null, in any column")
  public static Dataset removeAnyNulls(Dataset dataset) {
    return removeNulls(dataset, dataset.getColumnSet().getColumnNames());
  }
  
  /**
   * Removes the given number of rows from the head of the dataset.
   * 
   * @param dataset the {@link Dataset} whose head should be removed.
   * @param numberOfRows the number of rows to remove from the head of the dataset.
   * @return a new {@link Dataset}, with the desired number of rows removed.
   */
  @Doc("Removes the given number of rows from the head of the dataset")
  public static Dataset removeHead(
      @Doc("a dataset") Dataset dataset, 
      @Doc("number of rows") int numberOfRows) {
    return Slices.slice(dataset, numberOfRows, dataset.size());
  }
  
  /**
   * Removes the given number of rows from the tail of the dataset.
   * 
   * @param dataset the {@link Dataset} whose tail should be removed.
   * @param numberOfRows the number of rows to remove from the tail of the dataset.
   * @return a new {@link Dataset}, with the desired number of rows removed.
   */
  @Doc("Removes the given number of rows from the tail of the dataset")
  public static Dataset removeTail(
      @Doc("a dataset") Dataset dataset, 
      @Doc("number of rows") int numberOfRows) {
    return Slices.slice(dataset, 0, dataset.size() - numberOfRows);
  }
  
  /**
   * Removes the given percentage of rows from the top of the dataset.
   * 
   * @param dataset the {@link Dataset} whose top should be removed.
   * @param percentage a percentage (between 0 and 1, inclusively) of the dataset.
   * @return a new {@link Dataset}, with the top removed.
   */
  @Doc("Removes a percentage of the data in the given dataset from the top of it")  
  public static Dataset removeTop(
    @Doc("a dataset") Dataset dataset, 
    @Doc("a percentage") double percentage) {
    Checks.isTrue(percentage >= 0 && percentage <= 1, "Percentage must be between 0 and 1, inclusively. Got: %s", percentage);
    int numberOfRows = (int) (percentage * (double) dataset.size());
    return removeHead(dataset, numberOfRows);
  }
  
  /**
   * Removes the given percentage of rows from the bottom of the dataset.
   * 
   * @param dataset the {@link Dataset} whose bottom should be removed.
   * @param percentage a percentage (between 0 and 1, inclusively) of the dataset.
   * @return a new {@link Dataset}, with the bottom removed.
   */
  @Doc("Removes a percentage of the data in the given dataset from the bottom of it")
  public static Dataset removeBottom(
    @Doc("a dataset") Dataset dataset, 
    @Doc("a percentage") double percentage) {
    Checks.isTrue(percentage >= 0 && percentage <= 1, "Percentage must be between 0 and 1, inclusively. Got: %s", percentage);
    int numberOfRows = (int) (percentage * (double) dataset.size());
    return removeTail(dataset, numberOfRows);
  }
  
  /**
   * @param dataset the {@link Dataset} from which to select a subset.
   * @param expression the MVEL expression to use as criteria.
   * @return a new {@link Dataset} holding the rows that matched the given criteria.
   */
  @Doc(value = "Selects a subset of the given dataset, using the provided filter expression",
       examples=  {
         @Example(caption = "Selecting all that is greated than a given value", content = "salary >= 1000" )
       })
  public static Dataset select(
    @Doc("a dataset from which to select a subset of data") final Dataset dataset, 
    @Doc("a filter expression") final String expression) {
    
    final Map<String, Object> context  = new HashMap<>();
    final Serializable        compiled = MVEL.compileExpression(expression);
    
    return dataset.getSubset(new Criteria<RowResult>() {
      
      @Override
      public boolean matches(RowResult v) {
        context.clear();
        for (String colName : dataset.getColumnSet().getColumnNames()) {
          if (v.get(colName) != null) {
            context.put(colName, v);      
          } 
        }
        Object returnValue = MVEL.executeExpression(compiled, context);
        if (returnValue == null || !(returnValue instanceof Boolean)) {
          throw new IllegalArgumentException(
              String.format("Expression doest not evaluate to boolean: %s", expression)
          );
        } else {
          return ((Boolean) returnValue).booleanValue();
        }
      }
    });
  }
}
