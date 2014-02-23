package org.sapia.dataset.algo;

import java.util.Comparator;

/**
 * An instance of this class reverses the result of a given delegate comparator.
 * 
 * @author yduchesne
 */
public class ReverseComparator<T> implements Comparator<T> {
  
  private Comparator<T> delegate;
  
  public ReverseComparator(Comparator<T> delegate) {
    this.delegate = delegate;
  }
  
  @Override
  public int compare(T o1, T o2) {
    return -delegate.compare(o1, o2);
  }

}
