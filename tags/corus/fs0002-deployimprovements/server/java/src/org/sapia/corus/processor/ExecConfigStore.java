package org.sapia.corus.processor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.sapia.corus.admin.CommandArg;
import org.sapia.corus.db.DbMap;

public class ExecConfigStore {

  private DbMap<String, ExecConfig> _configs;
  
  public ExecConfigStore(DbMap<String, ExecConfig> configs) {
    _configs = configs;
  }
  
  public List<ExecConfig> getConfigs(){
    Iterator<ExecConfig> configs = _configs.values();
    List<ExecConfig> toReturn = new ArrayList<ExecConfig>();
    while(configs.hasNext()){
      toReturn.add(configs.next());
    }
    return toReturn;
  }
  
  public List<ExecConfig> getBootstrapConfigs(){
    Iterator<ExecConfig> configs = _configs.values();
    List<ExecConfig> toReturn = new ArrayList<ExecConfig>();
    while(configs.hasNext()){
      ExecConfig ec = configs.next();
      if(ec.isStartOnBoot()){
        toReturn.add(ec);
      }
    }
    return toReturn;
  }
  
  public List<ExecConfig> getConfigsFor(CommandArg arg){
    Iterator<ExecConfig> configs = _configs.values();
    List<ExecConfig> toReturn = new ArrayList<ExecConfig>();
    while(configs.hasNext()){
      ExecConfig c = configs.next();
      if(arg != null && arg.matches(c.getName())){
        toReturn.add(c);
      }
    }
    return toReturn;
  }
  
  public void removeConfigsFor(CommandArg arg){
    List<ExecConfig> configs = getConfigsFor(arg);
    for(ExecConfig c:configs){
      _configs.remove(c.getName());
    }
  }
  
  public ExecConfig getConfigFor(String name){
    return _configs.get(name);
  }
  
  public void removeConfig(String name){
    _configs.remove(name);
  }
  
  public void addConfig(ExecConfig btc){
    _configs.put(btc.getName(), btc);
  }


}
