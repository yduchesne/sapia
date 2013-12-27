/*
 * ValidateStep.java
 *
 * Created on September 9, 2005, 4:46 PM
 *
 */

package org.sapia.soto.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ClassUtils;
import org.sapia.soto.Debug;
import org.sapia.soto.Env;
import org.sapia.soto.EnvAware;
import org.sapia.soto.NotFoundException;
import org.sapia.soto.config.NullObjectImpl;
import org.sapia.soto.state.ExecContainer;
import org.sapia.soto.state.Executable;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.State;
import org.sapia.soto.state.Step;
import org.sapia.soto.state.StmKey;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;
import org.sapia.util.xml.confix.ObjectHandlerIF;
import org.sapia.validator.Status;

/**
 *
 * @author yduchesne
 */
public class ValidateStep extends ExecContainer
  implements Step, State, EnvAware, ObjectCreationCallback, ObjectHandlerIF{
  
  private StmKey _key;
  private Env _env;
  private ValidationService _validation;
  private List _rules = new ArrayList();
  private String _id;
  
  /** Creates a new instance of ValidateStep */
  public ValidateStep() {
  }
  
  public void setEnv(Env env){
    _env = env;
  }
  
  public void setId(String id){
    _id = id;
  }
  
  public String getId(){
    return _id;
  }
  
  public String getName(){
    return ClassUtils.getShortClassName(getClass());
  }
  
  public void setExportKey(String key){
    _key = StmKey.parse(key);
  }
  
  public void setValidationService(ValidationService validation){
    _validation = validation;
  }
  
  public RuleTag createRule(){
    RuleTag tag = new RuleTag();
    _rules.add(tag);
    return tag;
  }
  
  public Object onCreate() throws ConfigurationException{
    if(_validation == null){
      try{
        _validation = (ValidationService)_env.lookup(ValidationService.class);
      }catch(NotFoundException e){
        throw new ConfigurationException("Could not lookup validation service", e);
      }
    }
    return this;
  }
  
  public void handleObject(String name, Object o) throws ConfigurationException{
    if(o instanceof Executable){
      super.addExecutable((Executable)o);
    } else{
      throw new ConfigurationException("Expected instance of " + Executable.class.getName() +
        "; got: " + o.getClass().getName());
    }
  }
  
  public void execute(Result res){
    Status status = new Status(null);
    for(int i = 0; i < _rules.size(); i++){
      RuleTag rule = (RuleTag)_rules.get(i);
      if(Debug.DEBUG){
        if(res.getContext().hasCurrentObject()){
          Debug.debug(getClass(), "Validating " + res.getContext().currentObject() + " with: " + rule._name + ", " + rule._ruleSet);
        } else{
          Debug.debug(getClass(), "No object on context to validate with: " + rule._name + ", " + rule._ruleSet);
        }
      }
      try{
        Status tmp = _validation.validate(rule._name, rule._ruleSet, res.getContext().currentObject(), res.getContext().getLocale());
        status.addErrors(tmp.getErrors());
      }catch(Exception e){
        res.error("Could not perform validation", e);
        return;
      }
    }
    if(status.isError()){
      if(_key != null){
        if(_key.scopes != null && _key.scopes.length > 0){
          if(Debug.DEBUG){
            Debug.debug(getClass(), "Exporting validation result to " + _key);
          }
          res.getContext().put(_key.key, status, _key.scopes[0]);
        } else{
          res.error("Unable to export validation result; scope not specified: " + _key);
        }
      } else{
        res.getContext().push(status);
      }
      super.execute(res);
      res.abort();
    }
  }
  
  public static class RuleTag implements ObjectCreationCallback{
    
    private String _name;
    private String _ruleSet;
    
    public void setValidator(String name){
      _name = name;
    }
    public void setRule(String id){
      _ruleSet = id;
    }
    
    public Object onCreate() throws ConfigurationException{
      if(_name == null)
        throw new ConfigurationException("Validator not specified");
      if(_ruleSet == null)
        throw new ConfigurationException("Rule not specified");
      return new NullObjectImpl();
    }
  }
  
}
