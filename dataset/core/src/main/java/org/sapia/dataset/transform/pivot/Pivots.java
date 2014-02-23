package org.sapia.dataset.transform.pivot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.sapia.dataset.Column;
import org.sapia.dataset.ColumnSet;
import org.sapia.dataset.Dataset;
import org.sapia.dataset.Datatype;
import org.sapia.dataset.Index;
import org.sapia.dataset.IndexedDataset;
import org.sapia.dataset.Vector;
import org.sapia.dataset.VectorKey;
import org.sapia.dataset.help.Doc;
import org.sapia.dataset.impl.DefaultColumn;
import org.sapia.dataset.impl.DefaultColumnSet;
import org.sapia.dataset.impl.DefaultIndex;
import org.sapia.dataset.impl.DefaultRowSet;
import org.sapia.dataset.impl.DefaultVector;
import org.sapia.dataset.util.Checks;
import org.sapia.dataset.util.Data;
import org.sapia.dataset.value.NullValue;
import org.sapia.dataset.value.Values;

/**
 * Given a dataset with the following structure:
 * 
 * <pre>country,year,gdp,military,internet</pre>
 * 
 * Consisting of GDP, military spending, and internet usage per country, per year. One
 * could wish to display GDP per year per country, as follows:
 * <pre>          
 *                 2000 2001 2002 2003
 * -----------------------------------
 *   Angola     
 *         GDP      120  100  105   95
 *   Antigua     
 *         GDP       12   10   10  9.5
 *   ...
 * </pre>
 * 
 * 
 * 
 * @author yduchesne
 *
 */
public class Pivots {
  
  private Pivots() {
  }

  /**
   * @param datasets one or more {@link Dataset}s to merge.
   * @return the {@link IndexedDataset} resulting from the merge operation.
   */
  @Doc("Merges one or more pivot datasets")
  public static IndexedDataset merge(@Doc("pivot datasets to merge") List<PivotDataset> datasets) {
    Checks.isFalse(datasets.size() == 0, "No datasets specified: one must at least be provided");
    if (datasets.size() == 1) {
      return datasets.get(0);
    }
    PivotDataset toMergeTo = datasets.get(0);
    List<Index> toMerge = new ArrayList<Index>();
    
    for (PivotDataset ds : Data.slice(datasets, 1, datasets.size())) {
      toMerge.add(ds.getIndex());
    }
    Index newIndex = toMergeTo.getIndex().mergeWith(toMerge);
    return new PivotDataset(newIndex);
  }
  
  /**
   * @param datasets one or more {@link Dataset}s to merge.
   * @return the {@link IndexedDataset} resulting from the merge operation.
   */
  @Doc("Merges one or more pivot datasets")
  public static IndexedDataset merge(@Doc("pivot datasets to aggregate") PivotDataset...datasets) {
    return merge(Data.list(datasets));
  }

  /**
   * @param dataset a {@link Dataset} for which to create a pivot.
   * @param summaryColumnName the name of the column acting as summary (or pivot) column.
   * @param dimensionColumnNames the names of the columns acting as so-called "dimensions".
   * @return the {@link IndexedDataset} resulting from the "pivoting" of the given dataset.
   */
  public static IndexedDataset pivot(
      @Doc("dataset for which to create a pivot") Dataset dataset, 
      @Doc("summary column name") String summaryColumName, 
      @Doc("dimension (or fact) column names") List<String> dimensionColumnNames) {
    return pivot(dataset, summaryColumName, dimensionColumnNames.toArray(new String[dimensionColumnNames.size()]));
  }

  /**
   * @param dataset a {@link Dataset} for which to create a pivot.
   * @param summaryColumnName the name of the column acting as summary column.
   * @param dimensionColumnNames the names of the columns acting as so-called "dimensions", or fact columns.
   * @return the {@link IndexedDataset} resulting from the "pivoting" of the given dataset.
   */
  public static IndexedDataset pivot(
      @Doc("dataset for which to create a pivot") Dataset dataset, 
      @Doc("summary column name") String summaryColumName, 
      @Doc("dimension (or fact) column names") String...dimensionColumnNames) {
    List<String>   indexColumnNames    = Arrays.asList(dimensionColumnNames);
    IndexedDataset indexed             = dataset.index(dimensionColumnNames);
    String         valueColumnName     = dimensionColumnNames[dimensionColumnNames.length - 1];
    Map<VectorKey, Map<Object, Double>> dimensionValuesBySummaryColumnValues = new TreeMap<>();
    Set<Object>    summaryColumnValues = new HashSet<>();
    
    for (VectorKey key : indexed.getKeys()) {
      for (Vector row : indexed.getRowset(key)) {
        Object summaryColumnValue = row.get(indexed.getColumnSet().get(summaryColumName).getIndex());
        if (NullValue.isNotNull(summaryColumnValue)) {
          Map<Object, Double> dimensionValues = dimensionValuesBySummaryColumnValues.get(key);
          if (dimensionValues == null) {
            dimensionValues = new HashMap<>();
            dimensionValuesBySummaryColumnValues.put(key, dimensionValues);
          }
          summaryColumnValues.add(summaryColumnValue);
          Object dimensionColumnValue = row.get(indexed.getColumnSet().get(valueColumnName).getIndex());
          Double currentValue = dimensionValues.get(summaryColumnValue);
          if (currentValue == null) {
            currentValue = new Double(0);
            dimensionValues.put(summaryColumnValue, currentValue);
          }
          currentValue = currentValue.doubleValue() + Values.doubleValue(dimensionColumnValue);
          dimensionValues.put(summaryColumnValue, currentValue);
        }
      }
    }
    
    AtomicInteger columnCount = new AtomicInteger();
    List<Column> columns = new ArrayList<>();
    for (String dimensionColumnName : dimensionColumnNames) {
      Column toCopy = indexed.getColumnSet().get(dimensionColumnName);
      if (dimensionColumnName.equals(dimensionColumnNames[dimensionColumnNames.length -1])) {
        columns.add(new DefaultColumn(columnCount.getAndIncrement(), Datatype.STRING, "fact_column"));
      } else {
        columns.add(new DefaultColumn(columnCount.getAndIncrement(), toCopy.getType(), toCopy.getName()));
      }
    }
    final Column summaryColumn = indexed.getColumnSet().get(summaryColumName);
    Map<Object, Column> columnsBySummaryColumnValues = new HashMap<>();
    List<Object> sortedSummaryColumnValues = new ArrayList<>(summaryColumnValues);
    Collections.sort(sortedSummaryColumnValues, new Comparator<Object>() {
      @Override
      public int compare(Object o1, Object o2) {
        return summaryColumn.getType().strategy().compareTo(o1, o2);
      }
    });
    for (Object summaryColumnValue : sortedSummaryColumnValues) {
      Column newColumn = new DefaultColumn(
          columnCount.getAndIncrement(), 
          Datatype.NUMERIC, 
          summaryColumn.getFormat().formatValue(
              summaryColumn.getType(), 
              summaryColumnValue
          ).trim()
      );
      columns.add(newColumn);
      columnsBySummaryColumnValues.put(summaryColumnValue, newColumn);
    }
    
    ColumnSet    columnSet = new DefaultColumnSet(columns);
    List<Vector> rows      = new ArrayList<>(dimensionValuesBySummaryColumnValues.size());
    for (Map.Entry<VectorKey, Map<Object, Double>> entry : dimensionValuesBySummaryColumnValues.entrySet()) {
      Object[] keyValues    = entry.getKey().getValues();
      Object[] vectorValues = new Object[columns.size()];
      for (int i = 0; i < keyValues.length; i++) {
        if (i == keyValues.length - 1) {
          vectorValues[i] = dimensionColumnNames[i];
        } else {
          vectorValues[i] = keyValues[i];
        }
      }
      Map<Object, Double> dimensionValues = entry.getValue();
      for (Map.Entry<Object, Double> dimensionValue : dimensionValues.entrySet()) {
        Column dimensionValueCol = columnsBySummaryColumnValues.get(dimensionValue.getKey());
        vectorValues[dimensionValueCol.getIndex()] = dimensionValue.getValue();
      }
      rows.add(new DefaultVector(vectorValues));
    }
    
    List<String> newIndexColumnNames = Data.slice(indexColumnNames, indexColumnNames.size() - 1);
    newIndexColumnNames.add("fact_column");

    Index index = new DefaultIndex(new DefaultRowSet(rows), columnSet, columnSet.includes(newIndexColumnNames));
    return new PivotDataset(index);
  }
  
}
