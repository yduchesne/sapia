package org.sapia.dataset.cli;

/**
 * Calls {@link CliSession#exit()}.
 * 
 * @author yduchesne
 *
 */
public class ExitCommand implements CliCommand {
  
  @Override
  public boolean accepts(CmdContext context) {
    return context.getLine().trim().equals("exit");
  }
  
  @Override
  public void run(CmdContext context) throws Exception {
    context.getSession().exit();
  }

}
