package org.sapia.corus.processor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.sapia.corus.admin.Arg;
import org.sapia.corus.admin.services.processor.ExecConfig;
import org.sapia.corus.db.DbMap;

public class ExecConfigDatabaseImpl implements ExecConfigDatabase{

  private DbMap<String, ExecConfig> _configs;
  
  public ExecConfigDatabaseImpl(DbMap<String, ExecConfig> configs) {
    _configs = configs;
  }
  
  /* (non-Javadoc)
   * @see org.sapia.corus.processor.ExecConfigDatabase#getConfigs()
   */
  public List<ExecConfig> getConfigs(){
    Iterator<ExecConfig> configs = _configs.values();
    List<ExecConfig> toReturn = new ArrayList<ExecConfig>();
    while(configs.hasNext()){
      toReturn.add(configs.next());
    }
    return toReturn;
  }
  
  /* (non-Javadoc)
   * @see org.sapia.corus.processor.ExecConfigDatabase#getBootstrapConfigs()
   */
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
  
  /* (non-Javadoc)
   * @see org.sapia.corus.processor.ExecConfigDatabase#getConfigsFor(org.sapia.corus.admin.CommandArg)
   */
  public List<ExecConfig> getConfigsFor(Arg arg){
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
  
  /* (non-Javadoc)
   * @see org.sapia.corus.processor.ExecConfigDatabase#removeConfigsFor(org.sapia.corus.admin.CommandArg)
   */
  public void removeConfigsFor(Arg arg){
    List<ExecConfig> configs = getConfigsFor(arg);
    for(ExecConfig c:configs){
      _configs.remove(c.getName());
    }
  }
  
  /* (non-Javadoc)
   * @see org.sapia.corus.processor.ExecConfigDatabase#getConfigFor(java.lang.String)
   */
  public ExecConfig getConfigFor(String name){
    return _configs.get(name);
  }
  
  /* (non-Javadoc)
   * @see org.sapia.corus.processor.ExecConfigDatabase#removeConfig(java.lang.String)
   */
  public void removeConfig(String name){
    _configs.remove(name);
  }
  
  /* (non-Javadoc)
   * @see org.sapia.corus.processor.ExecConfigDatabase#addConfig(org.sapia.corus.processor.ExecConfig)
   */
  public void addConfig(ExecConfig btc){
    _configs.put(btc.getName(), btc);
  }


}
