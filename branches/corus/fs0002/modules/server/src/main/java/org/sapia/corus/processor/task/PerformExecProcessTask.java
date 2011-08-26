package org.sapia.corus.processor.task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.sapia.console.CmdLine;
import org.sapia.corus.client.exceptions.port.PortUnavailableException;
import org.sapia.corus.client.services.configurator.InternalConfigurator;
import org.sapia.corus.client.services.configurator.Configurator.PropertyScope;
import org.sapia.corus.client.services.deployer.dist.Distribution;
import org.sapia.corus.client.services.deployer.dist.Port;
import org.sapia.corus.client.services.deployer.dist.ProcessConfig;
import org.sapia.corus.client.services.deployer.dist.Property;
import org.sapia.corus.client.services.file.FileSystemModule;
import org.sapia.corus.client.services.os.OsModule;
import org.sapia.corus.client.services.port.PortManager;
import org.sapia.corus.client.services.processor.ActivePort;
import org.sapia.corus.client.services.processor.Process;
import org.sapia.corus.core.Consts;
import org.sapia.corus.deployer.config.EnvImpl;
import org.sapia.corus.processor.ProcessInfo;
import org.sapia.corus.taskmanager.core.Task;
import org.sapia.corus.taskmanager.core.TaskExecutionContext;
import org.sapia.corus.taskmanager.core.TaskParams;
import org.sapia.ubik.rmi.naming.remote.RemoteInitialContextFactory;
import org.sapia.ubik.util.Localhost;

/**
 * Actually performs the execution of the OS process corresponding to the given {@link ProcessInfo}.
 * 
 * @author yduchesne
 */
public class PerformExecProcessTask extends Task<Boolean, TaskParams<ProcessInfo, Properties, Void, Void>>{

  @Override
  public Boolean execute(TaskExecutionContext ctx,
      TaskParams<ProcessInfo, Properties, Void, Void> params) throws Throwable {
    
    ProcessInfo   info              = params.getParam1();
    Properties    processProperties = params.getParam2();
    ProcessConfig conf              = info.getConfig();
    Process       process           = info.getProcess();
    Distribution  dist              = info.getDistribution();
    PortManager   ports             = ctx.getServerContext().getServices().getPortManager();
    OsModule      os                = ctx.getServerContext().getServices().getOS();

    if (conf.getMaxKillRetry() >= 0) {
      process.setMaxKillRetry(conf.getMaxKillRetry());
    }

    if (conf.getShutdownTimeout() >= 0) {
      process.setShutdownTimeout(conf.getShutdownTimeout());
    }

    File processDir = makeProcessDir(ctx, info);

    if (processDir == null) {
      return false;
    }
    
    process.setProcessDir(processDir.getAbsolutePath());
    process.setDeleteOnKill(conf.isDeleteOnKill());

    EnvImpl env = null;

    try {
      env = new EnvImpl(
          ctx.getServerContext().getHomeDir(), 
          process.getDistributionInfo().getProfile(), 
          dist.getBaseDir(), 
          dist.getCommonDir(), 
          process.getProcessDir(),
          getProcessProps(conf, process, dist, ctx, processProperties)
      );
    } catch (PortUnavailableException e) {
      process.releasePorts(ports);
      ctx.error(e);
      return false;
    }

    CmdLine cmd;
    try {
      cmd = conf.toCmdLine(env);
    } catch (Exception e) {
      process.releasePorts(ports);
      ctx.error(e);
      return false;
    }

    if (cmd == null) {
      ctx.warn(String.format("No executable found for profile: %s", env.getProfile()));
      process.releasePorts(ports);
      return false;
    }

    ctx.info(String.format("Executing process under: %s ---> %s", processDir, cmd.toString()));
    
    try {
      process.setOsPid(os.executeProcess(callback(ctx), processDir, cmd));
    } catch (IOException e) {
      ctx.error("Process could not be started", e);
      process.releasePorts(ports);
      return false;
    }

    ctx.info(String.format("Process started; corus pid: %s", process.getProcessID()));

    if (process.getOsPid() == null) {
      ctx.warn(String.format("No os pid available for:  %s", process));
    } else {
      ctx.info(String.format("OS pid: %s", process.getOsPid()));
    }
    return true;  
  }
  
  private File makeProcessDir(TaskExecutionContext ctx, ProcessInfo info) {
    FileSystemModule fs = ctx.getServerContext().lookup(FileSystemModule.class);
    File processDir = new File(info.getDistribution().getProcessesDir()
        + File.separator + info.getProcess().getProcessID());

    if (info.isRestart() && !fs.exists(processDir)) {
      ctx.warn(
          "Process directory: " + processDir
              + " does not exist; restart aborted");
      return null;
    } else {
      try{
        fs.createDirectory(processDir);
  
        if (!fs.exists(processDir)) {
          ctx.warn(
              String.format(
                  "Could not make process directory: %s; startup aborted", 
                  processDir.getAbsolutePath()
              )
          );
          return null;
        }
      }catch(IOException e){
        ctx.error(
            String.format(
                "Could not make process directory: %s; startup aborted", 
                processDir.getAbsolutePath()
            ),
            e
        );
        return null;
      }
    }
    return processDir;
  }
  
  @SuppressWarnings(value={"unchecked", "deprecation"})
  private Property[] getProcessProps(ProcessConfig conf, Process proc,
      Distribution dist, TaskExecutionContext ctx, Properties processProperties)
      throws PortUnavailableException {

    InternalConfigurator configurator = ctx.getServerContext().getServices().lookup(
        InternalConfigurator.class);
    PortManager portmgr = ctx.getServerContext().getServices().lookup(
        PortManager.class);

    List<Property> props = new ArrayList<Property>(10);
    String host = null;
    try {
      host = Localhost.getLocalAddress().getHostAddress();
    } catch (Exception e) {
      host = ctx.getServerContext().getServerAddress().getHost();
    }
    int port = ctx.getServerContext().getServerAddress().getPort();
    props.add(new Property("corus.home", ctx.getServerContext().getHomeDir()));
    props.add(new Property("corus.server.host", host));
    props.add(new Property("corus.server.port", "" + port));
    if (System.getProperty(Consts.PROPERTY_CORUS_DOMAIN) != null) {
      props.add(new Property("corus.server.domain", System
          .getProperty(Consts.PROPERTY_CORUS_DOMAIN)));
      props.add(new Property(RemoteInitialContextFactory.UBIK_DOMAIN_NAME,
          System.getProperty(Consts.PROPERTY_CORUS_DOMAIN)));
    }
    props.add(new Property("corus.distribution.name", dist.getName()));
    props.add(new Property("corus.distribution.version", dist.getVersion()));
    props.add(new Property("corus.process.dir", proc.getProcessDir()));
    props.add(new Property("corus.process.id", proc.getProcessID()));
    props.add(new Property("corus.process.poll.interval", ""
        + conf.getPollInterval()));
    props.add(new Property("corus.process.status.interval", ""
        + conf.getStatusInterval()));
    props.add(new Property("corus.process.profile", proc.getDistributionInfo()
        .getProfile()));
    props.add(new Property("user.dir", dist.getCommonDir()));

    Properties allProps = new Properties(processProperties);
    Properties confProps = configurator.getInternalProperties(PropertyScope.PROCESS);

    Enumeration names = confProps.propertyNames();

    while (names.hasMoreElements()) {
      String name = (String) names.nextElement();
      allProps.put(name, confProps.getProperty(name));
    }

    names = processProperties.propertyNames();

    // process properties...
    while (names.hasMoreElements()) {
      String name = (String) names.nextElement();
      String value = processProperties.getProperty(name);
      if (value != null) {
        if (value.indexOf(' ') > 0) {
          value = "\"" + value + "\"";
        }
        ctx.info("Passing process property: " + name + "=" + hideIfPassword(name, value));
        props.add(new Property(name, value));
      }
    }

    // process ports...
    List<Port> ports = conf.getPorts();
    Set<String> added = new HashSet<String>();
    for (int i = 0; i < ports.size(); i++) {
      Port p = ports.get(i);
      if (!added.contains(p.getName())) {
        int portInt = portmgr.aquirePort(p.getName());
        props.add(new Property("corus.process.port." + p.getName(), Integer
            .toString(portInt)));
        proc.addActivePort(new ActivePort(p.getName(), portInt));
        added.add(p.getName());
      }
    }

    return (Property[]) props.toArray(new Property[props.size()]);
  }
  
  private String hideIfPassword(String name, String value){
    if(name.contains("password")){
      return "********";
    }
    else return value;
  }
  
  private OsModule.LogCallback callback(final TaskExecutionContext ctx){
    return new OsModule.LogCallback() {
      @Override
      public void error(String msg) {
        ctx.error(msg);
      }
      
      @Override
      public void debug(String msg) {
        ctx.debug(msg);
      }
    };
  }
}
