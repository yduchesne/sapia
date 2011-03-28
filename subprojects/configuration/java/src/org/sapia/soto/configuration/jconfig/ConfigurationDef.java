/*
 * ConfigurationDef.java
 *
 * Created on August 10, 2005, 12:17 PM
 *
 */

package org.sapia.soto.configuration.jconfig;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;
import org.jconfig.ConfigurationManagerException;
import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.config.NullObjectImpl;
import org.sapia.soto.config.SotoIncludeContext;
import org.sapia.soto.util.Utils;
import org.sapia.util.text.TemplateContextIF;
import org.sapia.util.text.TemplateException;
import org.sapia.util.text.TemplateFactory;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

/**
 *
 * @author yduchesne
 */
public class ConfigurationDef implements EnvAware, ObjectCreationCallback{
  
  private Env _env;
  private String _uri, _name;
  private Map _vars = new HashMap();
  
  /**
   * Creates a new instance of ConfigurationDef 
   */
  public ConfigurationDef() {
    
  }
  
  public Variables createVars(){
    return new Variables(this);
  }
  
  public Variable createVar(){
    return new Variable(this);
  }  
  
  void addVariable(String name, String value){
    _vars.put(name, value);
  }    
  
  public void setEnv(Env env){
    _env = env;
  }
  
  public void setName(String name){
    _name = name;
  }
  
  public void setUri(String uri){
    _uri = uri;
  }
  
  public Object onCreate() throws ConfigurationException{
    if(_uri == null){
      throw new ConfigurationException("Configuration URI not specified for: " + _name);
    }
    if(_name == null){
      throw new ConfigurationException("Configuration name not specified for: " + _uri);
    }   
    TemplateFactory fac = new TemplateFactory();
    fac.setThrowExcOnMissingVar(false);
    InputStream is = null;
    try{
      is = _env.resolveStream(_uri);
      TemplateContextIF ctx = new ConfigDefTemplateContext(SotoIncludeContext.currentTemplateContext(), _vars);
      _vars = Utils.replaceVars(ctx, _vars);
      ctx = new ConfigDefTemplateContext(SotoIncludeContext.currentTemplateContext(), _vars);
      SotoConfigurationHandler handler = new SotoConfigurationHandler(_name, Utils.replaceVars(ctx, is, _uri), _uri);
      ConfigurationManager.getInstance().load(handler, _name);
      Configuration conf = ConfigurationManager.getConfiguration(_name);
      Iterator entries = _vars.entrySet().iterator();
      while(entries.hasNext()){
        Map.Entry entry = (Map.Entry)entries.next();
        if(entry.getKey() != null && entry.getValue() != null){
          if(entry.getValue() != null){
            conf.setVariable((String)entry.getKey(), (String)entry.getValue());
          }
        }
      }
      _vars = null;
      return new NullObjectImpl();
    }catch(IOException e){
      throw new ConfigurationException("Could not load configuration " + _uri, e);
    }catch(ConfigurationManagerException e){
      throw new ConfigurationException("Could not process configuration " + _uri, e);
    }catch(TemplateException e){
      throw new ConfigurationException("Could not substitute variables", e);
    }finally{
      if(is != null){
        try{is.close();}catch(IOException e){}
      }
    }
  }
  
  
  static final class ConfigDefTemplateContext implements TemplateContextIF{
    
    private TemplateContextIF _parent;
    private Map _params;
    
    ConfigDefTemplateContext(TemplateContextIF parent,
                             Map params){
      _parent = parent;
      _params = params;
    }
    
    public Object getValue(String str) {
      Object var = _params.get(str);
      if(var == null){
        var = _parent.getValue(str);
      }
      
      // workaround for JConfig bug...
      if(var == null){
        if(str.startsWith("system:")){
          return System.getProperty(str.substring("system:".length()));
        }
      }
      if(var == null){
        if(str.startsWith("env:")){
          return System.getenv(str.substring("env:".length()));
        }
      }      
      return var;
    }

    public void put(String str, Object obj) {
    }
  }  
}
