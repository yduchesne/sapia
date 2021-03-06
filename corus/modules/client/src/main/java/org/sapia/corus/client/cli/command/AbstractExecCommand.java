package org.sapia.corus.client.cli.command;

import java.io.OptionalDataException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.sapia.console.InputException;
import org.sapia.console.Option;
import org.sapia.corus.client.ClusterInfo;
import org.sapia.corus.client.Result;
import org.sapia.corus.client.Results;
import org.sapia.corus.client.cli.CliContext;
import org.sapia.corus.client.common.Delay;
import org.sapia.corus.client.facade.ProcessorFacade;
import org.sapia.corus.client.services.configurator.Tag;
import org.sapia.corus.client.services.deployer.dist.Distribution;
import org.sapia.corus.client.services.deployer.dist.ProcessConfig;
import org.sapia.corus.client.services.processor.Process;
import org.sapia.corus.client.services.processor.ProcessCriteria;
import org.sapia.ubik.net.ServerAddress;
import org.sapia.ubik.util.Collects;

/**
 * Base class implementing logic for blocking until process startup completion.
 * 
 * @author yduchesne
 * 
 */
public abstract class AbstractExecCommand extends CorusCliCommand {

  public static final int DEFAULT_EXEC_WAIT_TIME_SECONDS = 120;

  protected static final long WAIT_INTERVAL_MILLIS = 5000;
  protected static final OptionDef OPT_WAIT = new OptionDef("w", false);
  protected static final OptionDef OPT_HARD_KILL = new OptionDef("hard", false);
  protected static final List<OptionDef> AVAIL_OPTIONS = Collects.arrayToList(
      OPT_PROCESS_NAME, OPT_PROCESS_INSTANCES, OPT_DIST, OPT_VERSION, OPT_PROFILE,
      OPT_WAIT, OPT_CLUSTER
  );

  protected AbstractExecCommand() {
  }

  /**
   * @param ctx
   *          the {@link CliContext}.
   * @param criteria
   *          a {@link ProcessCriteria}.
   * @param seconds
   *          the max number of seconds to wait for.
   * @param cluster
   *          a {@link ClusterInfo}.
   */
  protected final void waitForProcessShutdown(CliContext ctx, ProcessCriteria criteria, int seconds, ClusterInfo cluster) {
    List<Process> currentProcesses = getProcessInstances(ctx.getCorus().getProcessorFacade(), criteria, cluster);

    Set<String> existing = new HashSet<String>();
    for (Process p : currentProcesses) {
      existing.add(p.getOsPid() + ":" + p.getProcessID());
    }

    Delay delay = new Delay(seconds, TimeUnit.SECONDS);
    int oldProcessCount = 0;
    do {
      currentProcesses = getProcessInstances(ctx.getCorus().getProcessorFacade(), criteria, cluster);
      oldProcessCount = 0;
      for (Process p : currentProcesses) {
        if (existing.contains(p.getOsPid() + ":" + p.getProcessID())) {
          oldProcessCount++;
        }
      }
      sleep(WAIT_INTERVAL_MILLIS);
    } while (oldProcessCount > 0 && delay.isNotOver());
  }

  /**
   * @param ctx
   *          the {@link CliContext}.
   * @param criteria
   *          a {@link ProcessCriteria}.
   * @param instancesPerHost
   *          the expected number of started instances per host.
   * @param seconds
   *          the max number of seconds to wait for.
   * @param cluster
   *          a {@link ClusterInfo}.
   */
  protected final void waitForProcessStartup(CliContext ctx, ProcessCriteria criteria, int instancesPerHost, int seconds, ClusterInfo cluster) {
    Delay delay = new Delay(seconds, TimeUnit.SECONDS);

    // determining which hosts may have the running processes: they must have
    // the
    // distribution, and the tags corresponding to these processes
    Results<List<Distribution>> distResults = ctx.getCorus().getDeployerFacade().getDistributions(criteria.getDistributionCriteria(), cluster);
    Set<Distribution> dists = new HashSet<Distribution>();

    Map<ServerAddress, Set<Tag>> hostsWithDist = new HashMap<ServerAddress, Set<Tag>>();
    while (distResults.hasNext()) {
      Result<List<Distribution>> distResult = distResults.next();
      if (!distResult.getData().isEmpty()) {
        dists.addAll(distResult.getData());
      }
      hostsWithDist.put(distResult.getOrigin().getEndpoint().getServerAddress(), new HashSet<Tag>());
    }

    // distribution set should not be empty, but considering anyway
    if (!dists.isEmpty()) {

      // obtaining the tags for each targeted host in the cluster
      Results<Set<Tag>> tagResults = ctx.getCorus().getConfigFacade().getTags(cluster);

      for (Result<Set<Tag>> t : tagResults) {
        Set<Tag> hostTags = hostsWithDist.get(t.getOrigin());
        if (hostTags != null) {
          hostTags.addAll(t.getData());
        }
      }

      // now checking which hosts are supposed to run the processes, based on
      // the tags
      Set<ServerAddress> targets = new HashSet<ServerAddress>();
      for (Distribution dist : dists) {

        List<ProcessConfig> procConfigs = dist.getProcesses(criteria.getName());
        for (ProcessConfig procConfig : procConfigs) {
          for (ServerAddress host : hostsWithDist.keySet()) {
            Set<Tag> hostTags = hostsWithDist.get(host);
            Set<Tag> procTags = new HashSet<Tag>();
            procTags.addAll(Tag.asTags(dist.getTagSet()));
            procTags.addAll(Tag.asTags(procConfig.getTagSet()));
            if (procTags.isEmpty() || hostTags.containsAll(procTags)) {
              targets.add(host);
            }
          }
        }
      }

      // we've got the target hosts: checking on them
      if (instancesPerHost > 0 && !targets.isEmpty()) {
        int totalExpectedInstances = instancesPerHost * targets.size();
        int currentCount = 0;
        ClusterInfo targetInfo = new ClusterInfo(true);
        targetInfo.addTargets(targets);
        while (delay.isNotOver()) {
          currentCount = 0;
          Results<List<Process>> processes = ctx.getCorus().getProcessorFacade().getProcesses(criteria, targetInfo);
          while (processes.hasNext()) {
            Result<List<Process>> proc = processes.next();
            currentCount += proc.getData().size();
          }
          if (currentCount < totalExpectedInstances) {
            sleep(WAIT_INTERVAL_MILLIS);
          } else {
            break;
          }
        }

        if (currentCount < totalExpectedInstances) {
          throw new IllegalStateException("Expected number of processes not started. Got: " + currentCount + ", expected: " + totalExpectedInstances);
        } else {
          ctx.getConsole().println(String.format("Completed startup of %s process(es) on: %s", totalExpectedInstances, targets));
        }
      }
    }
  }

  /**
   * @param ctx
   *          the {@link CliContext}.
   * @return the {@link Option} corresponding to the <code>-w</code>
   *         command-line option.
   */
  protected static Option getWaitOption(CliContext ctx) throws InputException {
    return getOpt(ctx, OPT_WAIT.getName());
  }
  
  /**
   * @param ctx
   *          the {@link CliContext}.
   * @return the {@link OptionalDataException} corresponding to the  <code>-hard</code>
   *         command-line option.
   */
  protected static boolean isHardKillOption(CliContext ctx) throws InputException {
    return getOpt(ctx, OPT_HARD_KILL.getName()) != null;
  }

  /**
   * @param processor
   *          a {@link ProcessorFacade}.
   * @param criteria
   *          a {@link ProcessCriteria}.
   * @param cluster
   *          a {@link ClusterInfo}.
   * @return the number of instances corresponding to the given criteria.
   */
  protected static int getProcessInstanceCount(ProcessorFacade processor, ProcessCriteria criteria, ClusterInfo cluster) {
    Results<List<Process>> processes = processor.getProcesses(criteria, cluster);
    int instances = 0;
    while (processes.hasNext()) {
      Result<List<Process>> processList = processes.next();
      if (processList == null) {
        break;
      }
      instances += processList.getData().size();
    }
    return instances;
  }

  /**
   * @param processor
   *          a {@link ProcessorFacade}.
   * @param criteria
   *          a {@link ProcessCriteria}.
   * @param cluster
   *          a {@link ClusterInfo}.
   * @return the {@link List} of {@link Process} instances.
   */
  protected static List<Process> getProcessInstances(ProcessorFacade processor, ProcessCriteria criteria, ClusterInfo cluster) {
    Results<List<Process>> processes = processor.getProcesses(criteria, cluster);
    List<Process> instances = new ArrayList<Process>();
    while (processes.hasNext()) {
      Result<List<Process>> processList = processes.next();
      if (processList == null) {
        break;
      }
      instances.addAll(processList.getData());
    }
    return instances;
  }

}
