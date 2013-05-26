package org.sapia.corus.client.cli.command;

import org.sapia.console.AbortException;
import org.sapia.console.CmdLine;
import org.sapia.console.InputException;
import org.sapia.corus.client.cli.CliContext;
import org.sapia.corus.client.cli.CliError;
import org.sapia.corus.client.common.ArgFactory;
import org.sapia.corus.client.common.ProgressMsg;
import org.sapia.corus.client.common.ProgressQueue;
import org.sapia.corus.client.exceptions.deployer.RunningProcessesException;
import org.sapia.corus.client.services.deployer.DistributionCriteria;
import org.sapia.corus.client.services.deployer.FileCriteria;
import org.sapia.corus.client.services.deployer.ShellScriptCriteria;


/**
 * Performs undeployment.
 * 
 * @author Yanick Duchesne
 */
public class Undeploy extends CorusCliCommand {
  
  public static final String OPT_EXEC_CONFIG = "e";
  public static final String OPT_SCRIPT      = "s";
  public static final String OPT_FILE        = "f";
  
  /**
   * @see CorusCliCommand#doExecute(CliContext)
   */
  protected void doExecute(CliContext ctx)
                    throws AbortException, InputException {
    if (ctx.getCommandLine().containsOption(OPT_EXEC_CONFIG, false)){
      doUndeployExecConfig(ctx);
    } else if (ctx.getCommandLine().containsOption(OPT_SCRIPT, false)){
      doUndeployScript(ctx);
    } else if (ctx.getCommandLine().containsOption(OPT_FILE, false)){
      doUndeployFile(ctx);
    } else {
      doUndeployDist(ctx);
    }
  }
  
  private void doUndeployExecConfig(CliContext ctx) throws AbortException, InputException {
    String name = ctx.getCommandLine().assertOption(OPT_EXEC_CONFIG, true).getValue();
    ctx.getCorus().getProcessorFacade().undeployExecConfig(name, getClusterInfo(ctx));
  }
  
  private void doUndeployDist(CliContext ctx) throws AbortException, InputException {
    try {
      String  dist    = null;
      String  version = null;
      CmdLine cmd     = ctx.getCommandLine();
      
      if(cmd.hasNext() && cmd.isNextArg()){
        cmd.assertNextArg(new String[]{ARG_ALL});
        dist    = WILD_CARD;
        version = WILD_CARD;
      } else {
        dist    = cmd.assertOption(DIST_OPT, true).getValue();
        version = cmd.assertOption(VERSION_OPT, true).getValue();
      }
      
      super.displayProgress(ctx.getCorus().getDeployerFacade().undeployDistribution(
          DistributionCriteria.builder().name(dist).version(version).build(),
          getClusterInfo(ctx)),
          ctx);
      
    } catch (RunningProcessesException e) {
      CliError err = ctx.createAndAddErrorFor(this, e);
      ctx.getConsole().println(err.getSimpleMessage());
    } 
  }
  
  private void doUndeployScript(CliContext ctx) throws InputException {
    String alias = ctx.getCommandLine().assertOption(OPT_SCRIPT, true).getValue();
    ProgressQueue progress = ctx.getCorus().getScriptManagementFacade().removeScripts(
        ShellScriptCriteria.newInstance().setAlias(ArgFactory.parse(alias)), 
        getClusterInfo(ctx));
    doHandleProgress(progress, ctx);
  }
  
  private void doUndeployFile(CliContext ctx) throws InputException {
    String name = ctx.getCommandLine().assertOption(OPT_FILE, true).getValue();
    ProgressQueue progress = ctx.getCorus().getFileManagementFacade().deleteFiles(
        FileCriteria.newInstance().setName(ArgFactory.parse(name)), 
        getClusterInfo(ctx));
    doHandleProgress(progress, ctx);
  }  
  
  private void doHandleProgress(ProgressQueue progress, CliContext ctx) {
    while (progress.hasNext()) {
      for (ProgressMsg p : progress.next()) {
        if (p.isError()) {
          CliError err = ctx.createAndAddErrorFor(this, p.getError());
          ctx.getConsole().print(err.getSimpleMessage());
          throw new AbortException();
        }
        ctx.getConsole().print(p.getMessage().toString());
      }
    }    
  }
}
