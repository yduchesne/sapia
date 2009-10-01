package org.sapia.corus.server.processor;

import java.util.List;

import org.sapia.corus.admin.Arg;
import org.sapia.corus.admin.services.processor.ExecConfig;

public interface ExecConfigDatabase {

  public abstract List<ExecConfig> getConfigs();

  public abstract List<ExecConfig> getBootstrapConfigs();

  public abstract List<ExecConfig> getConfigsFor(Arg arg);

  public abstract void removeConfigsFor(Arg arg);

  public abstract ExecConfig getConfigFor(String name);

  public abstract void removeConfig(String name);

  public abstract void addConfig(ExecConfig btc);

}