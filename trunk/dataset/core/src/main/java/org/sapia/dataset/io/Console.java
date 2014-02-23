package org.sapia.dataset.io;

import org.sapia.dataset.util.DefaultRef;
import org.sapia.dataset.util.Ref;

/**
 * The console.
 * 
 * @author yduchesne
 *
 */
public class Console {
  
  private static Ref<ConsoleOutput> output = new DefaultRef<ConsoleOutput>(new DefaultConsoleOutput());

  private Console() {
  }
  
  /**
   * @return the {@link ConsoleOutput} implementation that is used.
   */
  public static ConsoleOutput getOutput() {
    return output.get();
  }
  
  /**
   * @param out the {@link ConsoleOutput} to use.
   */
  public static void setConsoleOutput(Ref<ConsoleOutput> out) {
    output = out;
  }

  /**
   * @param toOutput an {@link Object} to output.
   */
  public static void println(Object toOutput) {
    output.get().println(toOutput);
  }
  
  /**
   * @param toOutput an {@link Object} to output.
   */
  public static void print(Object toOutput) {
    output.get().print(toOutput);
  }

  /**
   * @param msg a message, which may include formatting.
   * @param args the message's arguments.
   * @see String#format(String, Object...)
   */
  public static void println(String msg, Object...args) {
    output.get().println(String.format(msg, args));
  }
}
