package org.sapia.console;


/**
 * @author Yanick Duchesne
 */
public class Quit implements Command {
	
  /**
   * @see org.sapia.console.Command#execute(Context)
   */
  public void execute(Context ctx) throws AbortException {
    throw new AbortException();
  }
}
