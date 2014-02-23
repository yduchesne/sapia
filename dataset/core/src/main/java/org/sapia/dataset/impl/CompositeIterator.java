package org.sapia.dataset.impl;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An {@link Iterator} implementation that wraps an iterator of
 * other iterators.
 * <p>
 * An instance of this class manages iterating over these nested
 * iterators seamlessly.
 *  
 * @author yduchesne
 *
 */
public class CompositeIterator<T> implements Iterator<T> {
  
  private Iterator<Iterator<T>> iterators;
  private Iterator<T>           delegate;
  
  public CompositeIterator(Iterator<Iterator<T>> iterators) {
    this.iterators = iterators;
  }

  @Override
  public boolean hasNext() {
    if (delegate == null) {
      if(iterators.hasNext()) {
        delegate = iterators.next();
      } else {
        return false;
      }
    }
    if (!delegate.hasNext()) {
      if (iterators.hasNext()) {
        delegate = iterators.next();
        return delegate.hasNext();
      } else {
        return false;
      }
    }
    return true;
  }
  
  @Override
  public T next() {
    if (delegate == null || !delegate.hasNext()) {
      throw new NoSuchElementException();
    }
    return delegate.next();
  }
  
  @Override
  public void remove() {
  }

}
