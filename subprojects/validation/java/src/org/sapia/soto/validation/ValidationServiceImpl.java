/*
 * ValidationServiceImpl.java
 *
 * Created on September 9, 2005, 3:33 PM
 *
 */

package org.sapia.soto.validation;

import gnu.trove.THashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.sapia.soto.util.Param;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectHandlerIF;
import org.sapia.validator.Status;

/**
 *
 * @author yduchesne
 */
public class ValidationServiceImpl implements ValidationService, ObjectHandlerIF{
  
  private Map _validators = new THashMap();
  private List _globals = new ArrayList();
  
  /** Creates a new instance of ValidationServiceImpl */
  public ValidationServiceImpl() {
  }
  
  public Param createGlobal(){
    Param p = new Param();
    _globals.add(p);
    return p;
  }
  
  
  public Status validate(String validatorName, 
                                String ruleSetId, 
                                Object toValidate,
                                Locale locale) throws Exception{
    VladConfig conf = (VladConfig)_validators.get(validatorName);
    if(conf == null){
      throw new IllegalArgumentException("No validator found for: " + validatorName);
    }
    return conf.getValidator().validate(ruleSetId, toValidate, locale);
  }  
  
  public void handleObject(String name, Object o) throws ConfigurationException{
    if(o instanceof VladConfig){
      VladConfig vlad = (VladConfig)o;
      if(vlad.getName() == null){
        throw new ConfigurationException("Validator name not specified on " + vlad);
      }
      if(_validators.containsKey(vlad.getName())){
        throw new ConfigurationException("Validator already bound under name: " + vlad.getName());
      }
      
      for(int i = 0; i < _globals.size(); i++){
        Param p = (Param)_globals.get(i);
        if(p.getName() != null && p.getValue() != null){
          vlad.getValidator().addGlobal(p.getName(), p.getValue());
        }
      }
      
      _validators.put(vlad.getName(), vlad);
    }
    else{
      throw new ConfigurationException("Instance of " + getClass().getName() + 
        " expecting an instance of " + VladConfig.class.getName() + 
        ", got: " + o.getClass().getName() + " for element: " + name);
    }
  }
  
  
}
