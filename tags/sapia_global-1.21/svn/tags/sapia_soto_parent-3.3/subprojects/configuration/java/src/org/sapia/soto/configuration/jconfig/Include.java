/*
 * Include.java
 *
 * Created on November 9, 2005, 5:26 PM
 */

package org.sapia.soto.configuration.jconfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;
import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.config.SotoIncludeContext;
import org.sapia.soto.util.Param;
import org.sapia.soto.util.Utils;
import org.sapia.util.text.TemplateContextIF;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

/**
 *
 * @author yduchesne
 */
public class Include implements ObjectCreationCallback, EnvAware{
  
  private String _config;
  private String _uri;
  private Env _env;
  private List _params = new ArrayList();
  
  /** Creates a new instance of Include */
  public Include() {
  }
  
  public void setConfiguration(String configName){
    _config = configName;
  }
  
  public void setConf(String configName){
    setConfiguration(configName);
  }  
  
  public void setName(String configName){
    setName(configName);
  }  
  
  public void setEnv(Env env){
    _env = env;
  }
  
  public void setUri(String uri){
    _uri = uri;
  }
  
  public Param createParam(){
    Param p = new Param();
    _params.add(p);
    return p;
  }
  
  public Object onCreate() throws ConfigurationException{
    if(_uri == null){
      throw new ConfigurationException("Include URI not set");
    }
    if(_config == null){
      throw new ConfigurationException("Configuration name not set");
    }
    

    String[] confNames = Utils.split(_config, ',', true);
    List confs = new ArrayList();
    for(int i = 0; i < confNames.length; i++){
      Configuration conf = 
        ConfigurationManager.getConfiguration(_config);
      if(conf != null){
        confs.add(conf);
      }
    }
    Map params = new HashMap();
    for(int i = 0; i < _params.size(); i++){
      Param p = (Param)_params.get(i);
      if(p.getName() != null && p.getValue() != null){
        params.put(p.getName(), p.getValue());
      }
    }
    TemplateContextIF ctx = new JConfigTemplateContext(
      confs, SotoIncludeContext.currentTemplateContext(), params);
    return _env.include(_uri, ctx);
  }
  
  static final class JConfigTemplateContext implements TemplateContextIF{
    
    private List _confs;
    private TemplateContextIF _parent;
    private Map _params;
    
    JConfigTemplateContext(List confs, 
                           TemplateContextIF parent,
                           Map params){
      _confs = confs;
      _parent = parent;
      _params = params;
    }
    
    public Object getValue(String str) {
      Object var = _params.get(str);
      if(var == null){
        for(int i = 0; i < _confs.size(); i++){
          Configuration conf = (Configuration)_confs.get(i);
          var = conf.getVariable(str);
          if(var != null){
            break;
          }
        }
      }
      if(var == null){
        var = _parent.getValue(str);
      }
      return var;
    }

    public void put(String str, Object obj) {
    }
  }
}
