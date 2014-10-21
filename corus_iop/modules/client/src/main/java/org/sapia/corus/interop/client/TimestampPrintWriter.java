package org.sapia.corus.interop.client;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A {@link PrintWriter} that writes a timestamp at the beginning of each line.
 * @author yduchesne
 *
 */
class TimestampPrintWriter extends PrintWriter {

  private DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

  TimestampPrintWriter(OutputStream delegate) {
    super(delegate, true);
  }
  
  public TimestampPrintWriter(Writer delegate) {
    super(delegate, true);
  }

  @Override
  public void println() {
    timestamp();
    super.println();
  }

  @Override
  public void println(boolean x) {
    timestamp();
    super.print(x);
    super.println();
  }

  @Override
  public void println(char x) {
    timestamp();
    super.print(x);
    super.println();
  }

  @Override
  public void println(char[] x) {
    timestamp();
    super.print(x);
    super.println();
  }

  @Override
  public void println(double x) {
    timestamp();
    super.print(x);
    super.println();
  }

  @Override
  public void println(float x) {
    timestamp();
    super.print(x);
    super.println();
  }

  @Override
  public void println(int x) {
    timestamp();
    super.print(x);
    super.println();;
  }

  @Override
  public void println(long x) {
    timestamp();
    super.print(x);
    super.println();;
  }

  @Override
  public void println(Object x) {
    timestamp();
    super.print(x);
    super.println();
  }

  @Override
  public void println(String x) {
    timestamp();
    super.print(x);
    super.println();
  }

  private synchronized void timestamp() {
    super.print("[" + format.format(new Date()) + "] ");
  }

}
