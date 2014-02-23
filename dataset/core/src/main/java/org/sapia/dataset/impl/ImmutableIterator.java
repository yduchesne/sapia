package org.sapia.dataset.impl;

import java.util.Iterator;

/**
 * An {@link Iterator} implementation that is meant to wrap another {@link Iterator}.
 * <p>
 * This class forbids modification operations by leaving the {@link #remove()} method
 * empty.
 * 
 * @author yduchesne
 *
 */
public class ImmutableIterator<T> implements Iterator<T> {
  
  private Iterator<T> delegate;
  
  public ImmutableIterator(Iterator<T> delegate) {
    this.delegate = delegate;
  }
  
  @Override
  public boolean hasNext() {
    return delegate.hasNext();
  }
  
  @Override
  public T next() {
    return delegate.next();
  }
  
  public void remove() {
  }

}
