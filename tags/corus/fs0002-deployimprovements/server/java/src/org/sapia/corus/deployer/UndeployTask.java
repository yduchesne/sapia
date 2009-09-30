package org.sapia.corus.deployer;

import java.io.File;
import java.util.List;

import org.sapia.corus.admin.CommandArg;
import org.sapia.corus.admin.services.deployer.dist.Distribution;
import org.sapia.corus.server.deployer.DistributionDatabase;
import org.sapia.corus.taskmanager.core.Task;
import org.sapia.corus.taskmanager.core.TaskExecutionContext;
import org.sapia.corus.taskmanager.tasks.TaskFactory;


/**
 * This tasks remove a distribution from the corus server.
 * 
 * @author Yanick Duchesne
 */
public class UndeployTask extends Task {
  private CommandArg      _name;
  private CommandArg      _version;
  private DistributionDatabase _store;

  UndeployTask(DistributionDatabase store, CommandArg name, CommandArg version) {
    _store   = store;
    _name    = name;
    _version = version;
  }

  @Override
  public Object execute(TaskExecutionContext ctx) throws Throwable {
    List<Distribution> dists    = _store.getDistributions(_name, _version);
    for(Distribution dist:dists){
      File         distDir = new File(dist.getBaseDir());
      ctx.info("Undeploying distribution " + _name + ", version: " +
                 _version);
      Task deleteDir = TaskFactory.newDeleteDirTask(distDir);
      ctx.getTaskManager().executeAndWait(deleteDir);
      _store.removeDistribution(_name, _version);

      if (!distDir.exists()) {
        ctx.info("Undeployment successful");
      } else {
        ctx.warn(distDir.getAbsolutePath() +
                      " could not be completely removed");
      }
    }
    return null;
  }
}
