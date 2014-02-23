package org.sapia.dataset.cli;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.sapia.dataset.util.Checks;

/**
 * A circular, FIFO buffer of errors.
 * 
 * @author yduchesne
 *
 */
public class ErrorBuffer {
  
  private static final int MAX_ERRORS = 100;
  
  private LinkedList<Throwable> errors = new LinkedList<>();
  
  ErrorBuffer() {
  }

  /**
   * @param err a {@link Throwable} instance, corresponding to an error that occurred.
   */
  void addError(Throwable err) {
    if (errors.size() >= MAX_ERRORS) {
      errors.removeFirst();
    }
    errors.add(err);
  }
  
  /**
   * @return this instance's {@link List} of errors.
   */
  public List<Throwable> getErrors() {
    return Collections.unmodifiableList(errors);
  }
  
  /**
   * @return the last {@link Throwable} in this instance.
   */
  public Throwable getLastError() {
    Checks.illegalState(errors.isEmpty(), "No errors in buffer");
    return errors.getLast();
  }
  
  /**
   * Removes all errors in this buffer. 
   */
  public void clear() {
    errors.clear();
  }
  
}
