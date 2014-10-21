package org.sapia.corus.interop.client;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Locale;

/** 
 * Adapts the {@link StdLogOutput} interface to the {@link PrintStream} class.
 * 
 * @author yduchesne
 *
 */
class PrintStreamLogOutputAdapter extends PrintStream {
  
  private static final ByteArrayOutputStream NULL_STREAM = new ByteArrayOutputStream();
  
  private StdLogOutput delegate;
  
  /**
   * @param delegate the {@link StdLogOutput} to which to log.
   */
  PrintStreamLogOutputAdapter(StdLogOutput delegate) {
    super(NULL_STREAM);
    this.delegate = delegate;
  }
  
  // --------------------------------------------------------------------------
  // Empty implementation
  
  @Override
  public PrintStream append(char c) {
    return this;
  }
  
  @Override
  public PrintStream append(CharSequence csq) {
    return this;
  }
  
  @Override
  public PrintStream append(CharSequence csq, int start, int end) {
    return this;
  }
  
  @Override
  public boolean checkError() {
    return false;
  }
  
  @Override
  protected void clearError() {
  }
  
  @Override
  public void close() {
    delegate.close();
  }
  
  @Override
  public void flush() {
  }
  
  // --------------------------------------------------------------------------
  // Actual implementation
  
  @Override
  public void print(boolean b) {
  }
  
  @Override
  public void print(char c) {
  }
  
  @Override
  public void print(char[] s) {
  }
  
  @Override
  public void print(double d) {
  }
  
  @Override
  public void print(float f) {
  }
  
  @Override
  public void print(int i) {
  }
  
  @Override
  public void print(long l) {
  }
  
  @Override
  public void print(Object obj) {
  }
  
  @Override
  public void print(String s) {
  }
  
  // --------------------------------------------------------------------------
  // Actual implementation
  
  @Override
  public PrintStream format(Locale l, String format, Object... args) {
    return this;
  }

  @Override
  public PrintStream format(String format, Object... args) {
    return this;
  }
  
  @Override
  public PrintStream printf(Locale l, String format, Object... args) {
    delegate.log(String.format(l, format, args));
    return this;
  }
  
  @Override
  public PrintStream printf(String format, Object... args) {
    delegate.log(String.format(format, args));
    return this;
  }
  
  @Override
  public void println() {
    delegate.log("");
  }
  
  @Override
  public void println(boolean x) {
    delegate.log(Boolean.toString(x));
  }
  
  @Override
  public void println(char x) {
    delegate.log(Character.toString(x));
  }
  
  @Override
  public void println(double x) {
    delegate.log(Double.toString(x));
  }
  
  @Override
  public void println(char[] x) {
    delegate.log(new StringBuilder().append(x).toString());
  }
  
  @Override
  public void println(float x) {
    delegate.log(Float.toString(x));
  }
  
  @Override
  public void println(int x) {
    delegate.log(Integer.toString(x));
  }
  
  @Override
  public void println(long x) {
    delegate.log(Long.toString(x));
  }
  
  @Override
  public void println(Object x) {
    delegate.log(x != null ? x.toString() : "null");
  }
  
  @Override
  public void println(String x) {
    delegate.log(x != null ? x.toString(): "null");
  }
}
