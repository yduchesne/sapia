package org.sapia.dataset.transform.formula;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.sapia.dataset.ColumnSet;
import org.sapia.dataset.Vector;
import org.sapia.dataset.impl.DefaultRowResult;
import org.sapia.dataset.impl.DefaultVector;
import org.sapia.dataset.util.Checks;

class FormulaVector implements Vector {
  
  private ColumnSet     columns;
  private Vector        delegate;
  private FormulaInfo[] formulaes;
  
  FormulaVector(ColumnSet columns, Vector delegate, FormulaInfo[] formulaes) {
    this.columns   = columns;
    this.delegate  = delegate;
    this.formulaes = formulaes;
  }
  
  @Override
  public Object get(int index) throws IllegalArgumentException {
    Checks.isTrue(
        index < delegate.size() + formulaes.length, 
        "Invalid index: %s. Got %s values",  delegate.size() + formulaes.length
    );
    if  (index < delegate.size()) {
      return delegate.get(index);
    }
    FormulaInfo f = formulaes[index - delegate.size()];
    
    DefaultRowResult result = new DefaultRowResult(columns);
    result.setVector(delegate);
    return f.apply(result);
  }

  @Override
  public Iterator<Object> iterator() {
    return new Iterator<Object>() {
      
      private int index;
      @Override
      public boolean hasNext() {
        return index < delegate.size() + formulaes.length;
      }
      
      @Override
      public Object next() {
        if (index >= delegate.size() + formulaes.length) {
          throw new NoSuchElementException();
        }
        return get(index++);
      }
      
      @Override
      public void remove() {
      }
    };
  }
  
  @Override
  public int size() {
    return delegate.size() + formulaes.length;
  }
  
  @Override
  public Vector subset(int... indices) throws IllegalArgumentException {
    Object[] toReturn = new Object[indices.length];
    for (int i = 0; i < indices.length; i++) {
      toReturn[i] = get(indices[i]);
    }
    return new DefaultVector(toReturn);
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
