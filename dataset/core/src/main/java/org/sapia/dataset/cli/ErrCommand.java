package org.sapia.dataset.cli;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrCommand implements CliCommand {
  
  private static final String ERR = "err";
  
  @Override
  public boolean accepts(CmdContext context) {
    return context.getLine().equals(ERR);
  }
  
  @Override
  public void run(CmdContext context) throws Exception {
    ErrorBuffer errs = context.getSession().getErrorBuffer();
    if (errs.getErrors().isEmpty()) {
      context.getSession().message("No error occurred");
    } else {
      Throwable lastError = errs.getLastError();
      StringWriter sw = new StringWriter();
      PrintWriter  pw = new PrintWriter(sw);
      lastError.printStackTrace(pw);
      pw.flush();
      context.getSession().message("Error stack trace:");
      context.getSession().getOutput().println(sw.toString());
      errs.clear();
    }
  }

}
