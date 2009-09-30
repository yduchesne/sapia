package org.sapia.corus.processor.task.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.sapia.console.CmdLine;
import org.sapia.corus.Consts;
import org.sapia.corus.LogicException;
import org.sapia.corus.admin.services.configurator.Configurator;
import org.sapia.corus.admin.services.configurator.Configurator.PropertyScope;
import org.sapia.corus.admin.services.deployer.dist.Distribution;
import org.sapia.corus.admin.services.deployer.dist.Env;
import org.sapia.corus.admin.services.deployer.dist.Port;
import org.sapia.corus.admin.services.deployer.dist.ProcessConfig;
import org.sapia.corus.admin.services.deployer.dist.Property;
import org.sapia.corus.admin.services.port.PortManager;
import org.sapia.corus.admin.services.port.PortUnavailableException;
import org.sapia.corus.admin.services.processor.ActivePort;
import org.sapia.corus.admin.services.processor.Process;
import org.sapia.corus.processor.ProcessInfo;
import org.sapia.corus.processor.task.TaskConfig;
import org.sapia.corus.taskmanager.Action;
import org.sapia.taskman.TaskContext;
import org.sapia.ubik.rmi.naming.remote.RemoteInitialContextFactory;
import org.sapia.ubik.util.Localhost;

/**
 * @author Yanick Duchesne
 */
public class ExecProcessAction implements Action{
  
  private TaskConfig  _config;
  private ProcessInfo _info;
  private Properties  _processProperties;
  
  public ExecProcessAction(TaskConfig config, 
                           ProcessInfo info,
                           Properties processProperties){
    _config = config;
    _info = info;
    _processProperties = processProperties;
  }
  
  /**
   * @see org.sapia.corus.taskmanager.Action#execute(org.sapia.taskman.TaskContext)
   */
  public boolean execute(TaskContext ctx) {
    ProcessConfig conf    = _info.getConfig();
    Process       process = _info.getProcess();
    Distribution  dist    = _info.getDistribution();
    PortManager   ports   = _config.getServices().lookup(PortManager.class);

    if (conf.getMaxKillRetry() >= 0) {
      process.setMaxKillRetry(conf.getMaxKillRetry());
    }

    if (conf.getShutdownTimeout() >= 0) {
      process.setShutdownTimeout(conf.getShutdownTimeout());
    }

    MakeProcessDirAction makeDir = ActionFactory.newMakeProcessDirAction(_info);
    makeDir.execute(ctx);
    File processDir = makeDir.getProcessDir();

    if (processDir == null) {
      return false;
    }

    process.setProcessDir(processDir.getAbsolutePath());
    process.setDeleteOnKill(conf.isDeleteOnKill());

    Env env = null;
     
    try{
      env = new Env(process.getDistributionInfo().getProfile(),
                      dist.getBaseDir(), dist.getCommonDir(),
                      process.getProcessDir(),
                      getProcessProps(conf, process, dist, ctx));
    }catch(PortUnavailableException e){
      process.releasePorts(ports);
      ctx.getTaskOutput().error(e);
      return false;
    }

    CmdLine cmd;
    try{
      cmd = conf.toCmdLine(env);
    }catch(LogicException e){
      process.releasePorts(ports);      
      ctx.getTaskOutput().error(e);
      return false;
    }

    if (cmd == null) {
      ctx.getTaskOutput().warning("No executable found for profile: " + env.getProfile());

      return false;
    }

    ctx.getTaskOutput().info("Executing process under: " + processDir + " ---> " + cmd.toString());
    boolean executed = ActionFactory.newExecCmdLineAction(processDir, cmd, process).execute(ctx);
    if(!executed){
      process.releasePorts(ports);
    }
    return executed;
  }
  
  private Property[] getProcessProps(ProcessConfig conf, Process proc,
                                     Distribution dist, TaskContext ctx) 
    throws PortUnavailableException{
    
    Configurator configurator = _config.getServices().lookup(Configurator.class);
    PortManager  portmgr      = _config.getServices().lookup(PortManager.class);
    
    List<Property>  props    = new ArrayList<Property>(10);
    String host     = null;
    try{
      host = Localhost.getLocalAddress().getHostAddress();
    }catch(Exception e){
      host = _config.getServerAddress().getHost();
    }
    int    port     = _config.getServerAddress().getPort();
    props.add(new Property("corus.server.host", host));
    props.add(new Property("corus.server.port", "" + port));
    if(System.getProperty(Consts.PROPERTY_CORUS_DOMAIN) != null){
      props.add(new Property("corus.server.domain", System.getProperty(Consts.PROPERTY_CORUS_DOMAIN)));
      props.add(new Property(RemoteInitialContextFactory.UBIK_DOMAIN_NAME, System.getProperty(Consts.PROPERTY_CORUS_DOMAIN)));
    }
    props.add(new Property("corus.distribution.name", dist.getName()));
    props.add(new Property("corus.distribution.version", dist.getVersion()));
    props.add(new Property("corus.process.dir", proc.getProcessDir()));
    props.add(new Property("corus.process.id", proc.getProcessID()));
    props.add(new Property("corus.process.poll.interval",
                           "" + conf.getPollInterval()));
    props.add(new Property("corus.process.status.interval",
                           "" + conf.getStatusInterval()));
    props.add(new Property("corus.process.profile",
                           proc.getDistributionInfo().getProfile()));
    props.add(new Property("user.dir", dist.getCommonDir()));
    
    
    Properties allProps = new Properties(_processProperties);
    Properties confProps =  configurator.getProperties(PropertyScope.PROCESS);

    Enumeration names = confProps.propertyNames();
    
    while(names.hasMoreElements()){
      String name = (String)names.nextElement();
      allProps.put(name, confProps.getProperty(name));
    }
    
    names = _processProperties.propertyNames();
     
    // process properties...
    while(names.hasMoreElements()){
      String name = (String)names.nextElement();
      String value = _processProperties.getProperty(name);
      if(value != null){
        if(value.indexOf(' ') > 0){
          value = "\"" + value + "\"";
        }
        ctx.getTaskOutput().info("Passing process property: " + name + "=" + value);
        props.add(new Property(name, value));  
      }
    }
    
    // process ports...
    List ports = conf.getPorts();
    Set added = new HashSet();
    for(int i = 0; i < ports.size(); i++){
      Port p = (Port)ports.get(i);
      if(!added.contains(p.getName())){
        int portInt = portmgr.aquirePort(p.getName());
        props.add(new Property("corus.process.port." + p.getName(), Integer.toString(portInt)));
        proc.addActivePort(new ActivePort(p.getName(), portInt));
        added.add(p.getName());
      }
    }

    return (Property[]) props.toArray(new Property[props.size()]);
  }  

}
