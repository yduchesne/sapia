package org.sapia.cocoon.util;

import java.util.Map;

import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.cocoon.components.modules.input.InputModule;
import org.apache.cocoon.processing.ProcessInfoProvider;
import org.apache.commons.lang.text.StrLookup;
import org.springframework.beans.factory.BeanFactory;

/**
 * This class extends the {@link StrLookup} class by providing lookup in
 * Cocoon's {@link InputModule}s. 
 * <p>
 * An instance of this class expects keys to be of the following format:
 * <pre>
 * input_module_name:variable_name
 * </pre> 
 * The key is parsed, and the value corresponding to the given variable name
 * is searched in the input module that is provided.
 * 
 * @author yduchesne
 *
 */
public class InputModuleStrLookup extends StrLookup{
  
  private BeanFactory _factory;
  private Map _objectModel;
  
  /**
   * 
   * @param factory the {@link BeanFactory} used to lookup input modules.
   * @param model Cocoon's object model (a {@link Map} of request, response, etc.).
   */
  public InputModuleStrLookup(BeanFactory factory){
    _factory = factory;
    _objectModel = ((ProcessInfoProvider)factory.getBean(ProcessInfoProvider.ROLE)).getObjectModel();
    if(_objectModel == null){
      throw new IllegalStateException("Could not find bean: " + ProcessInfoProvider.ROLE);
    }
  }
  
  public String lookup(String key) {
    String[] parts = key.split(":");
    if(parts.length == 2){
      InputModule module = (InputModule)_factory.getBean(InputModule.ROLE+"/"+parts[0]);
      if(module == null){
        throw new IllegalArgumentException("No input module found for: " + parts[0]);
      }
      try{
        Object value = module.getAttribute(parts[1], null, _objectModel);
        if(value == null){
          throw new IllegalArgumentException("No value found for: " + parts[1] + " in input module: " + parts[0]);
        }
        if(value instanceof String){
          return (String)value;
        }
        else{
          return value.toString();
        }
      }catch(ConfigurationException e){
        throw new IllegalStateException("Problem looking up variable: " + key, e);
      }
    }
    else{
      throw new IllegalArgumentException("Expected format: input_module_name:variable_name; got: " + key);
    }
  }
}

