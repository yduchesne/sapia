package org.sapia.corus.deployer;

import java.io.File;

import org.sapia.corus.LogicException;
import org.sapia.corus.admin.services.deployer.dist.Distribution;
import org.sapia.corus.server.deployer.DistributionDatabase;
import org.sapia.corus.taskmanager.tasks.TaskFactory;
import org.sapia.corus.taskmanager.v2.TaskExecutionContext;
import org.sapia.corus.taskmanager.v2.TaskV2;


/**
 * This task handles the extraction of deployment jars from the temporary
 * file (where they have been uploaded) to the deployment directory.  This
 * task ensures that a distribution will not overwrite an existing one,
 * and cleans up the temporary jar. Distributions are stored under the
 * deployment directory, according to the following pattern:
 * <p>
 * <code>distribution_name/version</code>
 * <p>
 * Each distribution directory has two additional directories:
 * <code>common</code> (where the jar is actually extracted) and,
 * eventually, <code>processes</code>, where each process instance has a specific
 * directory that it owns exclusively.
 *
 *
 * @author Yanick Duchesne
 */
public class DeployTask extends TaskV2 {
  private String            _fName;
  private String            _tmpDir;
  private String            _deployDir;
  private DistributionDatabase _store;

  DeployTask(DistributionDatabase store, String fileName, String tmpDir,
             String deployDir) {
    _fName     = fileName;
    _tmpDir    = tmpDir;
    _deployDir = deployDir;
    _store     = store;
  }

  @Override
  public Object execute(TaskExecutionContext ctx) throws Throwable {
    try {
    	
      int idx = _fName.lastIndexOf('.');

      if (idx < 0) {
        ctx.error("File name does not have a temporary extension: " + _fName);

        return null;
      }

      String shortName = _fName.substring(0, idx);
      File   src = new File(_tmpDir + File.separator + _fName);
      ctx.info("Deploying: " + shortName);

      // extraction corus.xml from archive and checking if already exists...
      Distribution dist    = Distribution.newInstance(src.getAbsolutePath());
      String       baseDir = _deployDir + File.separator + dist.getName() +
                             File.separator + dist.getVersion();
      dist.setBaseDir(baseDir);

      synchronized (_store) {
        File dest = new File(baseDir + File.separator + "common");
        File vms = new File(baseDir + File.separator + "processes");

        if (_store.containsDistribution(dist.getName(), dist.getVersion())) {
          ctx.error(new LogicException("Distribution already exists for: " +
                                         dist.getName() + " version: " +
                                         dist.getVersion()));

          return null;
        }

        if (dest.exists()) {
          ctx.error(new LogicException("Distribution already exists for: " +
                                         dist.getName() + " version: " +
                                         dist.getVersion()));

          return null;
        }

        // making distribution directories...
        if (!dest.exists() && !dest.mkdirs()) {
          ctx.error("Could not make directory: " + dest.getAbsolutePath());
        }

        vms.mkdirs();

        TaskV2 unjar = TaskFactory.newUnjarTask(src, dest);
        //ctx.execSyncNestedTask("UnjarTask", unjar);
        ctx.getTaskManager().executeAndWait(unjar);

        try {
          _store.addDistribution(dist);
          ctx.info("Distribution added to Corus");
        } catch (LogicException e) {
          // noop
        }
      }
    } catch (DeploymentException e) {
      ctx.error(e);
    } finally {
      TaskV2 deleteFile = TaskFactory.newDeleteFileTask(new File(_tmpDir +
                                                               File.separator +
                                                               _fName));
      ctx.getTaskManager().executeAndWait(deleteFile);
    }
    return null;
  }
}
