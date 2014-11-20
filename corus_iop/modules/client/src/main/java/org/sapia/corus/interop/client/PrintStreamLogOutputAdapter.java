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
  
  StringBuilder buffer = new StringBuilder();
  
  /**
   * @param delegate the {@link StdLogOutput} to which to log.
   */
  PrintStreamLogOutputAdapter(StdLogOutput delegate) {
    super(NULL_STREAM);
    this.delegate = delegate;
  }
  
  /**
   * @return this instance's buffer, used for <code>print</code> calls.
   */
  StringBuilder getBuffer() {
    return buffer;
  }
  
  // --------------------------------------------------------------------------
  // Empty implementation
  
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
  // append + print methods

  @Override
  public PrintStream append(char c) {
    buffer.append(c);
    return this;
  }
  
  @Override
  public PrintStream append(CharSequence csq) {
    buffer.append(csq);
    return this;
  }
  
  @Override
  public PrintStream append(CharSequence csq, int start, int end) {
    buffer.append(csq, start, end);
    return this;
  }
  
  @Override
  public void print(boolean b) {
    buffer.append(b);
  }
  
  @Override
  public void print(char c) {
    buffer.append(c);
  }
  
  @Override
  public void print(char[] s) {
    buffer.append(s);
  }
  
  @Override
  public void print(double d) {
    buffer.append(d);
  }
  
  @Override
  public void print(float f) {
    buffer.append(f);
  }
  
  @Override
  public void print(int i) {
    buffer.append(i);
  }
  
  @Override
  public void print(long l) {
    buffer.append(l);
  }
  
  @Override
  public void print(Object obj) {
    buffer.append(obj);
  }
  
  @Override
  public void print(String s) {
    buffer.append(s);
  }
  
  // --------------------------------------------------------------------------
  // println methods
  
  @Override
  public PrintStream format(Locale l, String format, Object... args) {
    buffer.append(String.format(l, format, args));
    return this;
  }

  @Override
  public PrintStream format(String format, Object... args) {
    buffer.append(String.format(format, args));
    return this;
  }
  
  @Override
  public PrintStream printf(Locale l, String format, Object... args) {
    delegate.log(buffer() + String.format(l, format, args));
    return this;
  }
  
  @Override
  public PrintStream printf(String format, Object... args) {
    delegate.log(buffer() + String.format(format, args));
    return this;
  }
  
  @Override
  public void println() {
    delegate.log(buffer() + "");
  }
  
  @Override
  public void println(boolean x) {
    delegate.log(buffer() + Boolean.toString(x));
  }
  
  @Override
  public void println(char x) {
    delegate.log(buffer() + Character.toString(x));
  }
  
  @Override
  public void println(double x) {
    delegate.log(buffer() + Double.toString(x));
  }
  
  @Override
  public void println(char[] x) {
    delegate.log(buffer() + new StringBuilder().append(x).toString());
  }
  
  @Override
  public void println(float x) {
    delegate.log(buffer() + Float.toString(x));
  }
  
  @Override
  public void println(int x) {
    delegate.log(buffer() + Integer.toString(x));
  }
  
  @Override
  public void println(long x) {
    delegate.log(buffer() + Long.toString(x));
  }
  
  @Override
  public void println(Object x) {
    delegate.log(x != null ? buffer() + x.toString() : buffer() + "null");
  }
  
  @Override
  public void println(String x) {
    delegate.log(x != null ? buffer() + x.toString(): buffer() + "null");
  }
 
  private String buffer() {
    String toReturn = buffer.toString();
    buffer.delete(0, buffer.length());
    return toReturn;
  }
}
