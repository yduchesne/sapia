package org.sapia.corus.client.cli.command;

import java.util.List;

import org.sapia.console.AbortException;
import org.sapia.console.CmdLine;
import org.sapia.console.InputException;
import org.sapia.console.table.Row;
import org.sapia.console.table.Table;
import org.sapia.corus.client.ClusterInfo;
import org.sapia.corus.client.Result;
import org.sapia.corus.client.Results;
import org.sapia.corus.client.cli.CliContext;
import org.sapia.corus.client.cli.TableDef;
import org.sapia.corus.client.services.processor.Process;
import org.sapia.corus.client.services.processor.ProcessCriteria;
import org.sapia.ubik.net.ServerAddress;

/**
 * Displays process info.
 * 
 * @author Yanick Duchesne
 */
public class Ps extends CorusCliCommand {
  
  private static final TableDef PROC_TBL = TableDef.newInstance()
      .createCol("dist", 15)
      .createCol("version", 7)
      .createCol("profile", 8)
      .createCol("name", 11)
      .createCol("pid", 14)
      .createCol("ospid", 6)
      .createCol("status", 9);
  
  private static final TableDef PROC_PORTS_TBL = TableDef.newInstance()
      .createCol("dist", 15)
      .createCol("version", 7)
      .createCol("profile", 8)
      .createCol("name", 11)
      .createCol("pid", 14)
      .createCol("ports", 15);

  private static TableDef TITLE_TBL = TableDef.newInstance()
      .createCol("val", 78);
  
  // --------------------------------------------------------------------------
  
  private static final String TERMINATING = "shutd.";
  private static final String ACTIVE      = "act.";
  private static final String RESTART     = "rest.";  
  private static final String SUSPENDED   = "susp.";  
  
  private static final String OPT_PORTS   = "ports";
  
  // --------------------------------------------------------------------------  
  
  @Override
  protected void doExecute(CliContext ctx)
                    throws AbortException, InputException {
    String  dist    = null;
    String  version = null;
    String  profile = null;
    String  vmName  = null;
    String  pid = null;
    boolean displayPorts = false;

    CmdLine cmd = ctx.getCommandLine();

    if (cmd.containsOption(DIST_OPT, true)) {
      dist = cmd.assertOption(DIST_OPT, true).getValue();
    }

    if (cmd.containsOption(VERSION_OPT, true)) {
      version = cmd.assertOption(VERSION_OPT, true).getValue();
    }

    if (cmd.containsOption(PROFILE_OPT, true)) {
      profile = cmd.assertOption(PROFILE_OPT, true).getValue();
    }

    if (cmd.containsOption(VM_NAME_OPT, true)) {
      vmName = cmd.assertOption(VM_NAME_OPT, true).getValue();
    }

    if (cmd.containsOption(VM_ID_OPT, true)) {
      pid = cmd.assertOption(VM_ID_OPT, true).getValue();
    }
    
    displayPorts = cmd.containsOption(OPT_PORTS, false);

    ClusterInfo cluster = getClusterInfo(ctx);

    Results<List<Process>> res;

    if (pid != null) {
      try {
        Process proc = ctx.getCorus().getProcessorFacade().getProcess(pid);
        displayHeader(ctx.getCorus().getContext().getAddress(), ctx, displayPorts);
        displayProcess(proc, ctx, displayPorts);
      } catch (Exception e) {
        ctx.getConsole().println(e.getMessage());
      }
    } else {
      ProcessCriteria criteria = ProcessCriteria.builder()
        .name(vmName)
        .distribution(dist)
        .version(version)
        .profile(profile)
        .build();
        
      res = ctx.getCorus().getProcessorFacade().getProcesses(criteria, cluster);
      displayResults(res, ctx, displayPorts);
    }
  }

  private void displayResults(Results<List<Process>> res, CliContext ctx, boolean displayPorts) {
    
    while (res.hasNext()) {
      Result<List<Process>> result = res.next();
      displayHeader(result.getOrigin(), ctx, displayPorts);
      for(Process proc:result.getData()){
        displayProcess(proc, ctx, displayPorts);
      }
    }
  }

  private void displayProcess(Process proc, CliContext ctx, boolean displayPorts) {
    Table procTable = displayPorts ? PROC_PORTS_TBL.createTable(ctx.getConsole().out()) : PROC_TBL.createTable(ctx.getConsole().out());
        
    procTable.drawLine('-', 0, CONSOLE_WIDTH);

    Row row = procTable.newRow();
    row.getCellAt(PROC_TBL.col("dist").index()).append(proc.getDistributionInfo().getName());
    row.getCellAt(PROC_TBL.col("version").index()).append(proc.getDistributionInfo().getVersion());
    row.getCellAt(PROC_TBL.col("profile").index()).append(proc.getDistributionInfo().getProfile());
    row.getCellAt(PROC_TBL.col("name").index()).append(proc.getDistributionInfo().getProcessName());
    row.getCellAt(PROC_TBL.col("pid").index()).append(proc.getProcessID());
    if(displayPorts){    
      row.getCellAt(PROC_PORTS_TBL.col("ports").index()).append(proc.getActivePorts().toString());
    }
    else{
      row.getCellAt(PROC_TBL.col("ospid").index()).append(proc.getOsPid() == null ? "n/a" : proc.getOsPid());
      
      switch (proc.getStatus()) {
        case KILL_CONFIRMED:
        case KILL_REQUESTED:
          row.getCellAt(PROC_TBL.col("status").index()).append(TERMINATING);        
          break;
        case SUSPENDED:
          row.getCellAt(PROC_TBL.col("status").index()).append(SUSPENDED);        
          break;
        case RESTARTING:
          row.getCellAt(PROC_TBL.col("status").index()).append(RESTART);        
          break;                    
        case ACTIVE:
          row.getCellAt(PROC_TBL.col("status").index()).append(ACTIVE);        
          break;          
        default:
          row.getCellAt(PROC_TBL.col("status").index()).append("n/a");        
          break;
      }
    }
    row.flush();
  }

  private void displayHeader(ServerAddress addr, CliContext ctx, boolean displayPorts) {
    Table procTable = displayPorts ? PROC_PORTS_TBL.createTable(ctx.getConsole().out()) : PROC_TBL.createTable(ctx.getConsole().out());
    Table titleTable = TITLE_TBL.createTable(ctx.getConsole().out());

    titleTable.drawLine('=', 0, CONSOLE_WIDTH);
    
    Row row = titleTable.newRow();
    row.getCellAt(TITLE_TBL.col("val").index()).append("Host: ").append(addr.toString());
    row.flush();

    procTable.drawLine(' ', 0, CONSOLE_WIDTH);

    Row headers = procTable.newRow();
    headers.getCellAt(PROC_TBL.col("dist").index()).append("Distribution");
    headers.getCellAt(PROC_TBL.col("version").index()).append("Version");
    headers.getCellAt(PROC_TBL.col("profile").index()).append("Profile");
    headers.getCellAt(PROC_TBL.col("name").index()).append("Name");
    headers.getCellAt(PROC_TBL.col("pid").index()).append("PID");
    if(displayPorts){
      headers.getCellAt(PROC_PORTS_TBL.col("ports").index()).append("Ports");    
    }
    else{
      headers.getCellAt(PROC_TBL.col("ospid").index()).append("OS PID");    
      headers.getCellAt(PROC_TBL.col("status").index()).append("Status");          
    }
    headers.flush();
  }
}
