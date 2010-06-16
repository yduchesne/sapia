package org.sapia.corus.admin.cli;

import org.sapia.console.CmdLine;
import org.sapia.console.Console;
import org.sapia.corus.admin.facade.CorusConnector;

public interface CliContext {

  public CorusConnector getCorus();

  public CmdLine getCommandLine();
  
  public Console getConsole();
  
}