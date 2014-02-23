package org.sapia.dataset.transform.formula;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.sapia.dataset.Column;
import org.sapia.dataset.ColumnSet;
import org.sapia.dataset.Dataset;
import org.sapia.dataset.Index;
import org.sapia.dataset.IndexedDataset;
import org.sapia.dataset.RowResult;
import org.sapia.dataset.Vector;
import org.sapia.dataset.algo.Criteria;
import org.sapia.dataset.impl.DatasetRowSetAdapter;
import org.sapia.dataset.impl.DefaultDataset;
import org.sapia.dataset.impl.DefaultIndex;
import org.sapia.dataset.impl.DefaultRowResult;
import org.sapia.dataset.impl.DefaultVector;
import org.sapia.dataset.impl.IndexDatasetAdapter;
import org.sapia.dataset.transform.slice.Slices;

class FormulaDataset implements Dataset {
  
  private Dataset       delegate;
  private FormulaInfo[] formulas;
  private ColumnSet     columns;
  
  FormulaDataset(Dataset delegate, ColumnSet columns, List<FormulaInfo> formulas) {
    this.delegate = delegate;
    this.columns  = columns;
    this.formulas = formulas.toArray(new FormulaInfo[formulas.size()]);
  }
  
  @Override
  public Vector getColumn(String name) throws IllegalArgumentException {
    Column col = this.columns.get(name);
    List<Object> items = new ArrayList<>(delegate.size());
    for (int i = 0; i < delegate.size(); i++) {
      Vector row = getRow(i);
      items.add(row.get(col.getIndex()));
    }
    return new DefaultVector(items);
  }
  
  @Override
  public Vector getColumn(int colIndex) throws IllegalArgumentException {
    Column col = this.columns.get(colIndex);
    List<Object> items = new ArrayList<>(delegate.size());
    for (int i = 0; i < delegate.size(); i++) {
      Vector row = getRow(i);
      items.add(row.get(col.getIndex()));
    }
    return new DefaultVector(items);
  }
  
  @Override
  public ColumnSet getColumnSet() {
    return columns;
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
    Column original = columns.get(colIndex);
    Column copy     = original.copy(0);
    return new DefaultDataset(Collections.singleton(copy), result);
  }
  
  @Override
  public Dataset getColumnSubset(String colName, Criteria<Object> filter)
      throws IllegalArgumentException {
    Column col = columns.get(colName);
    return getColumnSubset(col.getIndex(), filter);
  }
  
  @Override
  public Vector getRow(int rowIndex) throws IllegalArgumentException {
    return new FormulaVector(columns, delegate.getRow(rowIndex), formulas);
  }
  
  @Override
  public Dataset getSubset(Criteria<RowResult> filter) {
    List<Vector> subset = new ArrayList<>();
    DefaultRowResult result = new DefaultRowResult(columns);
    for (int i = 0; i < delegate.size(); i++) {
      Vector row = getRow(i);
      result.setVector(row);
      if (filter.matches(result)) {
        subset.add(row);
      }
    }
    return new DefaultDataset(columns, subset);
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
    return new Iterator<Vector>() {
      private int index;
      @Override
      public boolean hasNext() {
        return index < delegate.size();
      }
      
      @Override
      public Vector next() {
        if (index >= delegate.size()) {
          throw new NoSuchElementException();
        }
        return getRow(index++);
      }
      
      @Override
      public void remove() {
      }
    };
  }
  
  @Override
  public int size() {
    return delegate.size();
  }
}
