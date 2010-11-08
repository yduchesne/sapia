package org.sapia.corus.client.cli.command;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

import org.sapia.console.AbortException;
import org.sapia.console.Arg;
import org.sapia.console.InputException;
import org.sapia.console.Option;
import org.sapia.corus.client.cli.CliContext;
import org.sapia.corus.client.cli.CliError;

/**
 * This class contains the implementation of the 'err' command used
 * to display error logs on the corus CLI.
 * 
 * @author J-C Desrochers
 */
public class Err extends CorusCliCommand {

  public static final String OPTION_ERROR_LIST = "l";
  public static final String OPTION_ERROR_ID = "i";
  public static final String OPTION_ERROR_CLEAR = "c";

  /* (non-Javadoc)
   * @see org.sapia.corus.client.cli.command.CorusCliCommand#doExecute(org.sapia.corus.client.cli.CliContext)
   */
  protected void doExecute(CliContext aContext) throws AbortException, InputException {
    
    // 1. Command has no option/argument
    if (!aContext.getCommandLine().hasNext()) {
      doShowDetailsLastError(aContext);
      
    // 2. Command starts with an option
    } else if (aContext.getCommandLine().isNextOption()) {
      Option o = (Option) aContext.getCommandLine().next();
      if (OPTION_ERROR_LIST.equals(o.getName())) {
        try {
          if (o.getValue() == null) {
            doShowErrorList(aContext, Integer.MAX_VALUE);
          } else {
            doShowErrorList(aContext, Integer.parseInt(o.getValue()));
          }
        } catch (NumberFormatException nfe) {
          throw new InputException("Option " + o + " is invalid: it must have a numeric value");
        }
        
      } else if (OPTION_ERROR_ID.equals(o.getName())) {
        try {
          doShowErrorDetails(aContext, Integer.parseInt(o.getValue()));
        } catch (NumberFormatException nfe) {
          throw new InputException("Option " + o + " is invalid: it must have a numeric value");
        }

      } else if (OPTION_ERROR_CLEAR.equals(o.getName())) {
        doClearErrors(aContext);
        
      } else {
        throw new InputException("Option " + o + " is not supported");
      }
    
    // 3. Command starts with an argument
    } else {
      Arg a = (Arg) aContext.getCommandLine().next();
      throw new InputException("Argument " + a + " is not supported");
    }  
  }

  /**
   * Showing the details of the last error on the console.
   * 
   * @param aContext The CLI context.
   */
  private void doShowDetailsLastError(CliContext aContext) {
    List<CliError> errors = aContext.getErrors();
    if (errors.isEmpty()) {
      aContext.getConsole().println("Currently no error in memory");
    } else {
      displayErrorDetailsInTable(aContext, errors.get(0));
    }
  }
  
  /**
   * Showing the list of the last errors.
   * 
   * @param aContext The CLI context.
   * @param aMaxCount The maximum number of errors to show.
   */
  private void doShowErrorList(CliContext aContext, int aMaxCount) {
    aContext.getConsole().println("=============================================================================");
    aContext.getConsole().println("  ID  |    ERROR");
    aContext.getConsole().println("-----------------------------------------------------------------------------");

    int count = 0;
    for (Iterator<CliError> it = aContext.getErrors().iterator(); it.hasNext() && count++ < aMaxCount; ) {
      CliError error = it.next();
      
      aContext.getConsole().print(error.getId()+"\t\t");
      aContext.getConsole().println(error.getSimpleMessage());
    }
    
    aContext.getConsole().println("=============================================================================");
  }

  /**
   * Shows the details of an error on the console.
   * 
   * @param aContext The CLI context.
   * @param anErrorId The identifier of the error to show.
   * @exception InputException If no error is found for the identifier passed in.
   */
  private void doShowErrorDetails(CliContext aContext, int anErrorId) throws InputException {
    CliError foundError = null;
    for (Iterator<CliError> it = aContext.getErrors().iterator(); it.hasNext() && foundError == null; ) {
      CliError e = it.next();
      if (e != null && anErrorId == e.getId()) {
        foundError = e;
      }
    }
    
    if (foundError == null) {
      aContext.getConsole().println("No error found for id " + anErrorId);
    } else {
      displayErrorDetailsInTable(aContext, foundError);
    }
  }
  
  /**
   * Clears the errors from memory.
   * 
   * @param aContext
   */
  private void doClearErrors(CliContext aContext) {
    int count = aContext.removeAllErrors();
    if (count == 0) {
      aContext.getConsole().println("No error to clear from memory");
      
    } else if (count == 1) {
      aContext.getConsole().println("Cleared a single from memory");
      
    } else {
      aContext.getConsole().println("Cleared " + count + " errors from memory");
    }
  }

  private void displayErrorDetailsInTable(CliContext aContext, CliError anError) {
    aContext.getConsole().println("=============================================================================");
    aContext.getConsole().println(" ID\t    DATE    \t COMMAND LINE");
    aContext.getConsole().println("=============================================================================");

    aContext.getConsole().print("  " + anError.getId()+"\t");
    aContext.getConsole().print(new Timestamp(anError.getTimestamp())+"\t");
    aContext.getConsole().println(anError.getCommand().getName() + " " + anError.getCommandLine().toString());
    aContext.getConsole().println(anError.getSimpleMessage());
    if (anError.getCause() != null) {
      anError.getCause().printStackTrace(aContext.getConsole().out());
    }
    
    aContext.getConsole().println("=============================================================================");
    
  }
  
//  private void displayHeader(CliContext ctx) {
//    Table         hostTable;
//    Table         distTable;
//    Row           row;
//    Row           headers;
//
//    hostTable = new Table(ctx.getConsole().out(), 1, 78);
//    hostTable.drawLine('=');
//    row = hostTable.newRow();
//    row.getCellAt(0).append("Host: ").append(addr.toString());
//    row.flush();
//
//    hostTable.drawLine(' ');
//
//    distTable = new Table(ctx.getConsole().out(), 4, 20);
//    distTable.getTableMetaData().getColumnMetaDataAt(COL_DIST).setWidth(18);
//    distTable.getTableMetaData().getColumnMetaDataAt(COL_VERSION).setWidth(8);
//    distTable.getTableMetaData().getColumnMetaDataAt(COL_VMS).setWidth(23);
//    distTable.getTableMetaData().getColumnMetaDataAt(COL_PROFILES).setWidth(23);
//
//    headers = distTable.newRow();
//
//    headers.getCellAt(COL_DIST).append("Distribution");
//    headers.getCellAt(COL_VERSION).append("Version");
//    headers.getCellAt(COL_VMS).append("Process Configs");
//    headers.getCellAt(COL_PROFILES).append("Profiles");
//    headers.flush();
//  }
}
