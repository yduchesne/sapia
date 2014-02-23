package org.sapia.dataset.cli;

import org.sapia.dataset.util.ChainOR;

/**
 * Factory class that creates the command instances used by the {@link Cli}.
 *  
 * @author yduchesne
 *
 */
class CommandChainFactory {
  
  private CommandChainFactory() {
  }

  /**
   * @return the chain-of-responsibility holding the default commands.
   */
  static ChainOR<CmdContext> getDefaultCommands() {
    ChainOR<CmdContext> commands = new ChainOR<>();
    commands.addLink(new ExitCommand());
    commands.addLink(new ScriptCommand());
    commands.addLink(new ErrCommand());
    return commands;
  }
}
