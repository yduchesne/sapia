package org.sapia.dataset.transform.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.sapia.dataset.Column;
import org.sapia.dataset.ColumnSet;
import org.sapia.dataset.ColumnSets;
import org.sapia.dataset.Dataset;
import org.sapia.dataset.help.Doc;
import org.sapia.dataset.impl.DefaultColumnSet;
import org.sapia.dataset.util.Checks;

/**
 * Allows creating views over given datasets, by including/excluding columns.
 * 
 * @author yduchesne
 *
 */
@Doc("Allows creating views over given datasets, by including/excluding columns")
public class Views {

  private Views() {
  }
  
  /**
   * @param dataset the {@link Dataset} the dataset over which to project a view.
   * @param excludedColumnNames the names of the columns that should be excluded.
   * @return a new {@link Dataset}, with only the non-excluded columns.
   */
  @Doc("Creates a view over a given dataset, excluding the speficied columns")
  public static Dataset exclude(
      @Doc("a dataset") Dataset dataset, 
      @Doc("the names of the columns to exclude") List<String> excludedColumnNames) {
    ColumnSet viewColumns = dataset.getColumnSet().includes(excludedColumnNames);
    return view(dataset, viewColumns);
  }

  /**
   * @param dataset the {@link Dataset} the dataset over which to project a view.
   * @param excludedColumnNames the names of the columns that should be excluded.
   * @return a new {@link Dataset}, with only the non-excluded columns.
   */
  @Doc("Creates a view over a given dataset, excluding the speficied columns")
  public static Dataset exclude(
      @Doc("a dataset") Dataset dataset, 
      @Doc("the names of the columns to exclude") String...excludedColumnNames) {
    ColumnSet viewColumns = dataset.getColumnSet().includes(excludedColumnNames);
    return view(dataset, viewColumns);
  }
  
  /**
   * @param dataset the {@link Dataset} the dataset over which to project a view.
   * @param includedColumnNames the names of the columns that should be included.
   * @return a new {@link Dataset}, with only the columns specified.
   */
  @Doc("Creates a view over a given dataset, including only the speficied columns")
  public static Dataset include(
      @Doc("a dataset") Dataset dataset, 
      @Doc("the names of the columns to include") List<String> includedColumnNames) {
    ColumnSet viewColumns = dataset.getColumnSet().includes(includedColumnNames);
    return view(dataset, viewColumns);
  }
  
  /**
   * @param dataset the {@link Dataset} the dataset over which to project a view.
   * @param includedColumnNames the names of the columns that should be included.
   * @return a new {@link Dataset}, with only the columns specified.
   */
  @Doc("Creates a view over a given dataset, including only the speficied columns")
  public static Dataset include(
      @Doc("a dataset") Dataset dataset, 
      @Doc("the names of the columns to include")  String...includedColumnNames) {
    ColumnSet viewColumns = dataset.getColumnSet().includes(includedColumnNames);
    return view(dataset, viewColumns);
  }
  
  /**
   * Assigns new column names to the given dataset.
   * 
   * @param dataset a {@link Dataset}.
   * @param newColumnNames the array of new column names.
   * @return a new {@link Dataset}, with the new column names.
   */
  @Doc("Renames the given dataset's columns, assigning to these the given column names")
  public static Dataset rename(
      @Doc("a dataset") Dataset dataset, 
      @Doc("new column names, for all the columns in the dataset (given in column order)") String...newColumnNames) {
    Checks.isTrue(dataset.getColumnSet().size() == newColumnNames.length, 
        "You must pass same number of new column names as there are columns in the dataset. " 
        + "Expected: %s, got: %s", dataset.getColumnSet().size(), newColumnNames.length);
    
    List<Column> columns = new ArrayList<>(newColumnNames.length);
    for (int i = 0; i < dataset.getColumnSet().size(); i++) {
      columns.add(new ViewColumn(i, newColumnNames[i], dataset.getColumnSet().get(i)));
    }
    ColumnSet columnSet = new DefaultColumnSet(columns);
    return new ViewDataset(dataset, columnSet, columnSet.getColumnIndices());
  }
  
  /**
   * Assigns new column names to selected columns in the given dataset.
   * 
   * @param dataset a {@link Dataset}.
   * @param oldNamesVsNewNames the {@link Map} holding the mappings of old names to new names.
   * @return a new {@link Dataset}, with the new column names.
   */
  @Doc("Renames the given dataset's selected columns, with the new names given")
  public static Dataset rename(
      @Doc("a dataset") Dataset dataset, 
      @Doc("a map holding the current names to replace as keys, and the new names as corresponding values") 
      Map<String, String> oldNamesVsNewNames) {
    List<Column> columns = new ArrayList<>(oldNamesVsNewNames.size());
    for (Column currentColumn : dataset.getColumnSet()) {
      if (oldNamesVsNewNames.containsKey(currentColumn.getName())) {
        String newName = Checks.notNull(
            oldNamesVsNewNames.get(currentColumn.getName()), 
            "No new names specified for column: %s", currentColumn.getName());
        columns.add(new ViewColumn(currentColumn.getIndex(), newName, currentColumn));
      } else {
        columns.add(new ViewColumn(currentColumn.getIndex(), currentColumn.getName(), currentColumn));
      }
    }
    ColumnSet columnSet = new DefaultColumnSet(columns);
    return new ViewDataset(dataset, columnSet, columnSet.getColumnIndices());
  }
  
  /**
   * Assigns automatically abbreviated names to the columns in the dataset, following
   * a best-effort algorithm
   * 
   * @param dataset a {@link Dataset}.
   * @return a new {@link Dataset}, with abbreviated column names.
   */
  @Doc("Assigns automatically abbreviated names to the columns in the dataset, using the existing column names, " + 
   " according to a built-in best-effort algorithm")
  public static Dataset rename(
      @Doc("a dataset") Dataset dataset) {
    ColumnSet renamedColumns = ColumnSets.rename(dataset.getColumnSet());
    return new ViewDataset(dataset, renamedColumns, renamedColumns.getColumnIndices());
  }
  
  private static Dataset view(Dataset dataset, ColumnSet columns) {
    int[] columnIndices = new int[columns.size()];
    List<Column> viewColumns = new ArrayList<>(columns.size());
    for (int i = 0; i < columns.size(); i++) {
      Column     delegate = columns.get(i);
      ViewColumn vcol     = new ViewColumn(i, delegate);
      columnIndices[i]    = delegate.getIndex();
      viewColumns.add(vcol);
    }
    return new ViewDataset(dataset, new DefaultColumnSet(viewColumns), columnIndices);
  }

}
