package org.sapia.corus.admin;

import java.util.ArrayList;
import java.util.List;

/**
 * An instance of this class aggregates {@link HostItem}s and {@link HostList}s.
 * 
 * @author Yanick Duchesne
 */
//@SuppressWarnings
public class Results<T> {
  private List<Result<T>>    _results = new ArrayList<Result<T>>();
  private boolean _incomplete = true;

  /**
   * @param result a {@link Result}.
   */
  public synchronized void addResult(Result<T> result) {
    _results.add(result);
    notify();
  }

  /**
   * @return <code>true</code> if this instance contains other objects.
   */
  public synchronized boolean hasNext() {
    if (_results.size() > 0) {
      return true;
    }

    while (_incomplete) {
      try {
        wait();
      } catch (InterruptedException e) {
        return false;
      }
    }

    return _results.size() > 0;
  }

  /**
   * @return the next object that this instance contains (the returned object
   * is removed from this instance).
   */
  public Result<T> next() {
    return _results.remove(0);
  }

  public synchronized void complete() {
    _incomplete = false;
    notifyAll();
  }
}
