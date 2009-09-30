package org.sapia.corus.server.processor;

import java.util.List;

import org.sapia.corus.admin.CommandArg;
import org.sapia.corus.admin.services.processor.ExecConfig;

public interface ExecConfigDatabase {

  public abstract List<ExecConfig> getConfigs();

  public abstract List<ExecConfig> getBootstrapConfigs();

  public abstract List<ExecConfig> getConfigsFor(CommandArg arg);

  public abstract void removeConfigsFor(CommandArg arg);

  public abstract ExecConfig getConfigFor(String name);

  public abstract void removeConfig(String name);

  public abstract void addConfig(ExecConfig btc);

}