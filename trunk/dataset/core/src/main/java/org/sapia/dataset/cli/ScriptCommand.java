package org.sapia.dataset.cli;

import java.io.File;

/**
 * Executes a given Groovy script (whose path must be provided as an argument).
 * 
 * @author yduchesne
 *
 */
public class ScriptCommand implements CliCommand {
  
  private static final String SCRIPT = "script";
  
  @Override
  public boolean accepts(CmdContext context) {
    return context.getLine().startsWith(SCRIPT);
  }

  @Override
  public void run(CmdContext context) throws Exception {
    String[] tokens = context.getLine().split(" ");
    if (tokens.length < 2) {
      context.getSession().message("Missing script file");
    } else {
      Object returnValue = context.getSession().getShell().evaluate(new File(tokens[1]));
      if (returnValue != null) {
        context.getSession().getOutput().println(returnValue);
      }
    }
  }
}
