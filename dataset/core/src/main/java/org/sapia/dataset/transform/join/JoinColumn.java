package org.sapia.dataset.transform.join;

import org.sapia.dataset.Column;
import org.sapia.dataset.Datatype;
import org.sapia.dataset.NominalSet;
import org.sapia.dataset.format.Format;
import org.sapia.dataset.parser.Parser;

/**
 * A {@link Column} implementation used in the context of joined datasets.
 *  
 * @author yduchesne
 *
 */
class JoinColumn implements Column {
  
  private int     index;
  private Column  delegate;
  
  JoinColumn(int index, Column delegate) {
    this.index = index;
    this.delegate = delegate;
  }
  
  @Override
  public NominalSet getNominalValues() {
    return delegate.getNominalValues();
  }

  @Override
  public int getIndex() {
    return index;
  }
  
  @Override
  public Format getFormat() {
    return delegate.getFormat();
  }
  
  @Override
  public String getName() {
    return delegate.getName();
  }
  
  @Override
  public Parser getParser() {
    return delegate.getParser();
  }
  
  @Override
  public Datatype getType() {
    return delegate.getType();
  }
  
  @Override
  public void setFormat(Format formatter) {
    delegate.setFormat(formatter);
  }
  
  @Override
  public void setParser(Parser parser) {
    delegate.setParser(parser);
  }
  
  @Override
  public Column copy(int newIndex) {
    return delegate.copy(newIndex);
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Column) {
      return delegate.equals((Column) obj);
    }
    return false;
  }
  
  @Override
  public String toString() {
    return delegate.toString();
  }
}