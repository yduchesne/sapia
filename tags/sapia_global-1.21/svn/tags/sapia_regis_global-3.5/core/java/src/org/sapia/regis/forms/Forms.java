package org.sapia.regis.forms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.sapia.regis.Node;

public class Forms {
  
  public static final String DEFAULT = "Default";
  
  private static Map _forms = new HashMap();
  

  public static Form create(String type){
    if(_forms.containsKey(type)){
      throw new IllegalStateException("Form already exists for: " + type);
    }
    Form f = new Form(type);
    _forms.put(type, f);
    return f;
  }
  
  public static Collection getForms(){
    return new ArrayList(_forms.values());
  }
  
  public static Form getFormFor(Node node){
    if(node.getType() == null || node.getType().equals(DEFAULT)){
      return null;
    } 
    Form f = (Form)_forms.get(node.getType());
    if(f == null){
      throw new IllegalArgumentException("No form for: " + node.getType());
    }
    return f;
  }
}
