/*
 * VladConfig.java
 *
 * Created on September 9, 2005, 3:02 PM
 *
 */

package org.sapia.soto.validation;

import java.util.ArrayList;
import java.util.List;
import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.resource.Resource;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;
import org.sapia.validator.Vlad;

/**
 *
 * @author yduchesne
 */
public class VladConfig implements EnvAware, ObjectCreationCallback{
  
  private List _defUris = new ArrayList();
  private Resource _ruleResource;
  private Env _env;
  private String _name;
  private Vlad _validator = new Vlad();
  private long _lastModified = -1;
  
  /**
   * Creates a new instance of VladConfig 
   */
  public VladConfig() {
  }
  
  public void setEnv(Env env){
    _env = env;
  }
  
  public void addDef(String uri){
    _defUris.add(uri);
  }
  
  public void setUri(String rules) throws Exception{
    _ruleResource = _env.resolveResource(rules);
  }
  
  public void setName(String name){
    _name = name;
  }
  
  public String getName(){
    return _name;
  }
  
  public Object onCreate() throws ConfigurationException{
    if(_name == null){
      throw new ConfigurationException("Validator name not set");
    }
    if(_ruleResource == null){
      throw new ConfigurationException("Validation rules not set");
    }
    
    _validator = loadValidator();
    
    return this;
  }
  
  public Vlad getValidator() throws ConfigurationException{
    if(_ruleResource.lastModified() != _lastModified){
      _validator = loadValidator();
    }
    return _validator;
  }
  
  private synchronized Vlad loadValidator() throws ConfigurationException{
    if(_validator != null && _ruleResource.lastModified() == _lastModified){
      return _validator;
    }
    
    Vlad validator = new Vlad();
    if(_validator != null){
      validator.addGlobals(_validator.getGlobals());
    }
    String uri = null;
    for(int i = 0; i < _defUris.size() ; i++){
      try{
        validator.loadDefs(_env.resolveStream(uri = (String)_defUris.get(i)));
      }catch(Exception e){
        throw new ConfigurationException("Could not load validation rule definitions: " + uri + " for validator: " + _name, e);
      }
    }
 
    try{
      validator.load(_ruleResource.getInputStream());
    }catch(Exception e){
      e.printStackTrace();
      throw new ConfigurationException("Could not load validation rules: " + _ruleResource.getURI() + " for validator: " + _name, e);
    }
    _lastModified = _ruleResource.lastModified();
    return validator;
  }
  
}
