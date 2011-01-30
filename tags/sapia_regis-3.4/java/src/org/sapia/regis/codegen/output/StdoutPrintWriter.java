package org.sapia.regis.codegen.output;

import java.io.OutputStream;
import java.io.PrintWriter;

public class StdoutPrintWriter extends PrintWriter {

  public StdoutPrintWriter(OutputStream os) {
    super(os, true);
  }

  @Override
  public void close() {
    System.out
        .println("================================================================================");
  }

}
