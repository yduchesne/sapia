package org.sapia.dataset.io;

/**
 * A default implementation of the {@link ConsoleOutput}.
 * 
 * @author yduchesne
 *
 */
public class DefaultConsoleOutput implements ConsoleOutput {
  
  @Override
  public void print(Object content) {
    System.out.print(content);
  }
  
  @Override
  public void println(Object content) {
    System.out.println(content);
  }

}
