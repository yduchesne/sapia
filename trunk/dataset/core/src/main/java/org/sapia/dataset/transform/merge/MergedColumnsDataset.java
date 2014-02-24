package org.sapia.dataset.transform.merge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.sapia.dataset.Column;
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
import org.sapia.dataset.impl.DefaultDataset;
import org.sapia.dataset.impl.DefaultIndex;
import org.sapia.dataset.impl.DefaultRowResult;
import org.sapia.dataset.impl.DefaultVector;
import org.sapia.dataset.impl.IndexDatasetAdapter;
import org.sapia.dataset.transform.slice.Slices;
import org.sapia.dataset.util.Checks;

/**
 * A {@link Dataset} implementation that merges the columns of multiple datasets into
 * a single dataset.
 * <p>
 * Column names must be unique across all datasets.
 * 
 * @author yduchesne
 *
 */
class MergedColumnsDataset implements Dataset {

  private static class ColumnRef {
    private int     index;
    private Dataset dataset;
   
    private ColumnRef(int index, Dataset dataset) {
      this.index   = index;
      this.dataset = dataset;
    }
  }
  
  // --------------------------------------------------------------------------
  
  private class InternalVector implements Vector {
    
    private int rowIndex;
        
    private InternalVector(int rowIndex) {
      this.rowIndex = rowIndex;
    }
    
    @Override
    public Object get(int index) throws IllegalArgumentException {
      ColumnRef ref = ref(index);
      return ref.dataset.getRow(rowIndex).get(ref.index);
    }
    
    @Override
    public Iterator<Object> iterator() {
      List<Iterator<Object>> iterators = new ArrayList<>();
      for (Dataset ds : datasets) {
        iterators.add(ds.getRow(rowIndex).iterator());
      }
      return new CompositeIterator<Object>(iterators.iterator());
    }
    
    @Override
    public int size() {
      return columnSet.size();
    }
    
    @Override
    public Vector subset(int... indices) throws IllegalArgumentException {
      Object[] values = new Object[indices.length];
      for (int i = 0; i < values.length; i++) {
        values[i] = get(indices[i]);
      }
      return new DefaultVector(values);
    }
    
    @Override
    public Object[] toArray() {
      Object[] values = new Object[size()];
      for (int i = 0; i < values.length; i++) {
        values[i] = get(i);
      }
     return values;
    }
  }
  
  // ==========================================================================
  
  private List<Dataset>           datasets;
  private Map<String, ColumnRef>  columnRefsByName = new HashMap<>();
  private Map<Integer, ColumnRef> columnRefsByIndex = new HashMap<>();
  private ColumnSet               columnSet;
  private int                     size;
  
  MergedColumnsDataset(List<Dataset> datasets) {
    int columnCount = 0;
    List<Column> columns = new ArrayList<>();
    size = -1;
    for (Dataset ds : datasets) {
      if (size == -1) {
        size = ds.size();
      } else {
        Checks.isTrue(size == ds.size(), "Merged datasets must have same size (expected %s, got %s)", size, ds.size());
      }
      for (Column c : ds.getColumnSet()) {
        ColumnRef ref = new ColumnRef(c.getIndex(), ds);
        if (columnRefsByName.containsKey(c.getName())) {
          throw new IllegalArgumentException("Columns already present in one of the specified datasets: " 
          + "when merging datasets, make sure that all column names are unique across all datasets");
        }
        columnRefsByName.put(c.getName(), ref);
        columnRefsByIndex.put(columnCount, ref);
        columns.add(c.copy(columnCount));
        columnCount++;
      }
    }
    columnSet = new DefaultColumnSet(columns);
  }
  
  @Override
  public Vector getRow(int rowIndex) throws IllegalArgumentException {
    return new InternalVector(rowIndex);
  }
  
  @Override
  public Vector getColumn(int colIndex) throws IllegalArgumentException {
    ColumnRef ref = ref(colIndex);
    return ref.dataset.getColumn(ref(colIndex).index);
  }
  
  @Override
  public Vector getColumn(String name) throws IllegalArgumentException {
    ColumnRef ref = ref(name);
    return ref.dataset.getColumn(ref(name).index);
  }
  
  @Override
  public ColumnSet getColumnSet() {
    return columnSet;
  }
  
  @Override
  public Dataset getColumnSubset(int colIndex, Criteria<Object> filter)
      throws IllegalArgumentException {
    Vector       col    = getColumn(colIndex);
    List<Vector> result = new ArrayList<>();
    for (Object rowItem : col) {
      if (filter.matches(rowItem)) {
        Vector row = new DefaultVector(Collections.singletonList(rowItem));
        result.add(row);
      }
    }
    Column original = column(colIndex);
    Column copy     = original.copy(0);
    return new DefaultDataset(Collections.singleton(copy), result);
  }
  
  @Override
  public Dataset getColumnSubset(String colName, Criteria<Object> filter)
      throws IllegalArgumentException {
    Column col = column(colName);
    return getColumnSubset(col.getIndex(), filter);
  }

  @Override
  public Dataset getSubset(Criteria<RowResult> filter) {
    List<Vector> result = new ArrayList<>();
    
    DefaultRowResult rowResult = new DefaultRowResult(this.columnSet);
    for (int i = 0; i < size(); i++) {
      Vector row = getRow(i);
      rowResult.setVector(row);
      if (filter.matches(rowResult)) {
        result.add(row);
      }
    }
    return new DefaultDataset(columnSet, result);
  }
  
  @Override
  public Iterator<Vector> iterator() {
    return new Iterator<Vector>() {
      
      private int index;
      
      @Override
      public boolean hasNext() {
        return index < size();
      }
      
      @Override
      public Vector next() {
        return getRow(index++);
      }
      
      @Override
      public void remove() {
      }
      
    };
  }
  
  @Override
  public IndexedDataset index(String... colNames) throws IllegalArgumentException {
    Index index = new DefaultIndex(new DatasetRowSetAdapter(this), columnSet, columnSet.includes(colNames));
    return new IndexDatasetAdapter(index);
  }
  
  @Override
  public IndexedDataset index(List<String> colNames)
      throws IllegalArgumentException {
    return index(colNames.toArray(new String[colNames.size()]));
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
    return Datasets.toString(head());
  }
  
  
  // --------------------------------------------------------------------------
  
  private Column column(int colIndex) {
    ColumnRef ref = ref(colIndex);
    return ref.dataset.getColumnSet().get(ref.index);
  }
  
  private Column column(String name) {
    ColumnRef ref = ref(name);
    return ref.dataset.getColumnSet().get(ref.index);
  }
  
  private ColumnRef ref(int colIndex) {
    return Checks.notNull(this.columnRefsByIndex.get(colIndex), "No column at index: %s", colIndex);
  }
  
  private ColumnRef ref(String colName) {
    return Checks.notNull(this.columnRefsByName.get(colName), "No column for name: %s", colName);
  }

}
