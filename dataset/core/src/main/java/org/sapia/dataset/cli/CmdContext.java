package org.sapia.dataset.cli;

/**
 * Holds the context required by {@link CliCommand} instances, and which
 * lasts for the duration of a command's execution.
 *  
 * @author yduchesne
 *
 */
public class CmdContext {

  private CliSession session;
  private String     line;
  
  CmdContext(CliSession session, String line) {
    this.session = session;
    this.line    = line;
  }
  
  /**
   * @return this instance's {@link CliSession}.
   */
  public CliSession getSession() {
    return session;
  }
  
  /**
   * @return the command-line that was passed.
   */
  public String getLine() {
    return line;
  }
}
