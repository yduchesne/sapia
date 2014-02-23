package org.sapia.dataset.cli;

import org.sapia.dataset.util.ChainOR;

/**
 * Designed along the command pattern: models a command, executed in the
 * context of the command-line.
 * 
 * @author yduchesne
 *
 */
public interface CliCommand extends ChainOR.Link<CmdContext> {

  /**
   * @param context the current {@link CmdContext} instance.
   * @throws Exception if an error occurs running this command.
   */
  public void run(CmdContext context) throws Exception;
}
