package org.sapia.dataset.transform.merge;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import org.sapia.dataset.ColumnSet;
import org.sapia.dataset.Dataset;
import org.sapia.dataset.Datasets;
import org.sapia.dataset.Index;
import org.sapia.dataset.IndexedDataset;
import org.sapia.dataset.RowResult;
import org.sapia.dataset.Vector;
import org.sapia.dataset.algo.Criteria;
import org.sapia.dataset.impl.CompositeIterator;
import org.sapia.dataset.impl.DatasetRowSetAdapter;
import org.sapia.dataset.impl.DefaultColumnSet;
import org.sapia.dataset.impl.DefaultIndex;
import org.sapia.dataset.impl.DefaultVector;
import org.sapia.dataset.impl.IndexDatasetAdapter;
import org.sapia.dataset.transform.slice.Slices;
import org.sapia.dataset.util.Checks;

/**
 * Holds multiple {@link Dataset}s, as one.
 * 
 * @author yduchesne
 *
 */
class MergedRowsDataset implements Dataset {

  /**
   * Holds a dataset and its start offset.
   */
  private static class DatasetInfo {
    private int     offset;
    private Dataset dataset;
    
    private DatasetInfo(int offset, Dataset ds) {
      this.offset = offset;
      this.dataset    = ds;
    }
    
  }
  
  // ==========================================================================
  
  private ColumnSet                     columns;
  private List<Dataset>                 datasets;
  private int                           size;
  private TreeMap<Integer, DatasetInfo> datasetsByOffset = new TreeMap<>();
 
  MergedRowsDataset(ColumnSet columns, List<Dataset> datasets) {
    this.columns  = columns;
    this.datasets = datasets;
    for (Dataset ds : datasets) {
      Checks.isTrue(columns.equals(ds.getColumnSet()), "Datasets must have same column (same name and same type, in same order)");
      datasetsByOffset.put(size, new DatasetInfo(size, ds));
      size += ds.size();
    } 
  }
  
  @Override
  public ColumnSet getColumnSet() {
    return columns;
  }
  
  @Override
  public Vector getColumn(int colIndex) throws IllegalArgumentException {
    List<Object> values = new ArrayList<>(size);
    for (Dataset ds : datasets) {
      Vector vec = ds.getColumn(colIndex);
      for (Object v : vec) {
        values.add(v);
      }
    }
    return new DefaultVector(values);
  }
  
  @Override
  public Vector getColumn(String name) throws IllegalArgumentException {
    List<Object> values = new ArrayList<>(size);
    for (Dataset ds : datasets) {
      Vector vec = ds.getColumn(name);
      for (Object v : vec) {
        values.add(v);
      }
    }
    return new DefaultVector(values);
  }
  
  @Override
  public Dataset getColumnSubset(int colIndex, Criteria<Object> filter)
      throws IllegalArgumentException {
    List<Dataset> toReturn = new ArrayList<>();
    for (Dataset ds : datasets) {
      toReturn.add(ds.getColumnSubset(colIndex, filter));
    }
    ColumnSet copy = new DefaultColumnSet(columns.get(colIndex).copy(0));
    return new MergedRowsDataset(copy, toReturn);
  }
  
  @Override
  public Dataset getColumnSubset(String colName, Criteria<Object> filter)
      throws IllegalArgumentException {
    List<Dataset> toReturn = new ArrayList<>();
    for (Dataset ds : datasets) {
      toReturn.add(ds.getColumnSubset(colName, filter));
    }
    ColumnSet copy = new DefaultColumnSet(columns.get(colName).copy(0));
    return new MergedRowsDataset(copy, toReturn);
  }
  
  @Override
  public Vector getRow(int rowIndex) throws IllegalArgumentException {
    Checks.isFalse(datasets.isEmpty() || rowIndex < 0 || rowIndex >= size, "Invalid index: %s. Dataset size is: %s", rowIndex, size);
    DatasetInfo inf = datasetsByOffset.floorEntry(rowIndex).getValue();
    int realIndex = rowIndex - inf.offset;
    return inf.dataset.getRow(realIndex);
  }
  
  @Override
  public Dataset getSubset(Criteria<RowResult> filter) {
    List<Dataset> toReturn = new ArrayList<>();
    for (Dataset ds : datasets) {
      toReturn.add(ds.getSubset(filter));
    }
    return new MergedRowsDataset(columns, toReturn);
  }
  
  @Override
  public IndexedDataset index(String... colNames) throws IllegalArgumentException {
    Index index = new DefaultIndex(new DatasetRowSetAdapter(this), columns, columns.includes(colNames));
    return new IndexDatasetAdapter(index);
  }
  
  @Override
  public IndexedDataset index(List<String> colNames)
      throws IllegalArgumentException {
    return index(colNames.toArray(new String[colNames.size()]));
  }
  
  @Override
  public Iterator<Vector> iterator() {
    List<Iterator<Vector>> iterators = new ArrayList<>(datasets.size());
    for (Dataset ds : datasets) {
      iterators.add(ds.iterator());
    }
    return new CompositeIterator<Vector>(iterators.iterator());
  }
  
  @Override
  public Dataset head() {
    return Slices.head(this);
  }
  
  @Override
  public Dataset tail() {
    return Slices.tail(this);
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public String toString() {
    return Datasets.toString(this);
  }
  
}
