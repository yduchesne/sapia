package org.sapia.corus.client.cli.command;

import org.sapia.console.AbortException;
import org.sapia.console.InputException;
import org.sapia.console.table.Row;
import org.sapia.console.table.Table;
import org.sapia.corus.client.Result;
import org.sapia.corus.client.Results;
import org.sapia.corus.client.cli.CliContext;
import org.sapia.corus.client.cli.TableDef;
import org.sapia.corus.client.services.cluster.ClusterStatus;
import org.sapia.ubik.net.TCPAddress;


/**
 * @author Yanick Duchesne
 */
public class Cluster extends CorusCliCommand {

  private static final TableDef TBL = TableDef.newInstance()
        .createCol("host", 14)
        .createCol("port", 8)
        .createCol("role", 30);
  
  // --------------------------------------------------------------------------
  
  @Override 
  protected void doExecute(CliContext ctx)
                    throws AbortException, InputException {
  	
  	if(ctx.getCommandLine().hasNext()) {
  		ctx.getCommandLine().assertNextArg(new String[]{"status"});
  		displayStatus(ctx);
  	} else {
  		displayStatus(ctx);
  	}
  }

  private void displayStatus(CliContext ctx) {
    displayHeader(ctx);
    Results<ClusterStatus> results = ctx.getCorus().getCluster().getClusterStatus(getClusterInfo(ctx)); 
    Table      table = TBL.createTable(ctx.getConsole().out());
    
    table.drawLine('=', 0, CONSOLE_WIDTH);

    while(results.hasNext()) {
    	Result<ClusterStatus> status = results.next();
      TCPAddress addr = (TCPAddress) status.getOrigin();
      Row row  = table.newRow();
      row.getCellAt(TBL.col("host").index()).append(addr.getHost());
      row.getCellAt(TBL.col("port").index()).append("" + addr.getPort());
      row.getCellAt(TBL.col("role").index()).append(status.getData().getRole().name());
      row.flush();
    }
  }

  private void displayHeader(CliContext ctx) {
    Table table = TBL.createTable(ctx.getConsole().out());
    Row   headers   = table.newRow();
    headers.getCellAt(TBL.col("host").index()).append("Host");
    headers.getCellAt(TBL.col("port").index()).append("Port");
    headers.getCellAt(TBL.col("role").index()).append("Role");
    headers.flush();
  }
}