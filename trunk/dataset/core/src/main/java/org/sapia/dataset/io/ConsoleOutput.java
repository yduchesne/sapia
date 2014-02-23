package org.sapia.dataset.io;

public interface ConsoleOutput {
  
  /**
   * @param content some {@link Object} to print to the console output.
   */
  public void println(Object content);
  
  /**
   * @param content some {@link Object} to print to the console output.
   */
  public void print(Object content);
}
