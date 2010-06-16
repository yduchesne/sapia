package org.sapia.corus.admin.cli.command;

import java.util.List;

import org.sapia.console.AbortException;
import org.sapia.console.Arg;
import org.sapia.console.CmdLine;
import org.sapia.console.InputException;
import org.sapia.corus.admin.Result;
import org.sapia.corus.admin.Results;
import org.sapia.corus.admin.cli.CliContext;
import org.sapia.corus.admin.services.processor.Process;
import org.sapia.corus.core.ClusterInfo;


/**
 * @author Yanick Duchesne
 */
public class Restart extends CorusCliCommand {

  @Override
  protected void doExecute(CliContext ctx) throws AbortException,
      InputException {
    
    String  pid    = null;
    String  osPid   = null; 
    CmdLine cmd = ctx.getCommandLine();

    // restart by process IDENTIDER
    if (cmd.containsOption(VM_ID_OPT, true)) {
      pid = cmd.assertOption(VM_ID_OPT, true).getValue();
      restartProcessByVmId(ctx, pid);

      while (cmd.hasNext()) {
        sleep(1000);
        if (cmd.isNextArg()) {
          Arg argument = cmd.assertNextArg();
          restartProcessByVmId(ctx, argument.getName());
        } else {
          cmd.next();
        }
      }

    // restart by OS PROCESS ID
    } else if (cmd.containsOption(OS_PID_OPT, true)) {
      osPid = cmd.assertOption(OS_PID_OPT, true).getValue();
      restartProcessByOsPid(ctx, osPid);
      
      while (cmd.hasNext()) {
        sleep(1000);
        if (cmd.isNextArg()) {
          Arg argument = cmd.assertNextArg();
          restartProcessByOsPid(ctx, argument.getName());
        } else {
          cmd.next();
        }
      }
      
    }
   
  }
  
  protected void restartProcessByVmId(CliContext ctx, String pid) throws InputException {
    Process processToRestart = null;
    Results<List<Process>> results = ctx.getCorus().getProcessorFacade().getProcesses(new ClusterInfo(false));
    while (results.hasNext() && processToRestart == null) {
      Result<List<Process>> result = results.next();
      for(Process process:result.getData()){
        if (process.getProcessID().equals(pid)) {
          processToRestart = process;
        }
      }
    }
    
    if (processToRestart != null) {
      restartProcess(ctx, processToRestart);
    } else {
      ctx.getConsole().println("ERROR: Could not restart process, no active process found for the process ID " + pid);
    }
  }
  
  protected void restartProcessByOsPid(CliContext ctx, String osPid) throws InputException {
    Process processToRestart = null;
    Results<List<Process>> results = ctx.getCorus().getProcessorFacade().getProcesses(new ClusterInfo(false));
    while (results.hasNext() && processToRestart == null) {
      Result<List<Process>> result = results.next();
      for(Process process:result.getData()){
        if (process.getOsPid() != null && process.getOsPid().equals(osPid)) {
          String pid = process.getProcessID();
          processToRestart = process;
          ctx.getConsole().println("Found VM " + pid + " associated to OS pid " + osPid);
        }
      }
    }

    if (processToRestart != null) {
      restartProcess(ctx, processToRestart);
    } else {
      ctx.getConsole().println("ERROR: Could not restart process, no active process found for OS pid " + osPid);
    }
  }
  
  protected void restartProcess(CliContext ctx, Process aProcess) throws InputException {
    try {
      ctx.getCorus().getProcessorFacade().restart(aProcess.getProcessID());
    } catch (Exception e) {
      throw new InputException(e.getMessage());
    }

    ctx.getConsole().println("Proceeding to restart of VM " + aProcess.getProcessID() + "...");
  }

}
