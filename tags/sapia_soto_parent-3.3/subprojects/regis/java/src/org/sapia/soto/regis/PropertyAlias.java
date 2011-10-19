package org.sapia.soto.regis;

import java.util.Map;

import org.sapia.regis.Node;
import org.sapia.regis.Property;
import org.sapia.soto.util.Param;

public class PropertyAlias extends Param{
  
  private String _alias;
  public void setAlias(String alias){
    _alias = alias;
  }
  
  public String getAlias(){
    return _alias;
  }
  
  void setProperty(Node node, Map vars){
    if(getName() != null){
      if(_alias != null){
        Property prop = node.renderProperty(getName(), vars);
        if(!prop.isNull()){
          vars.put(_alias, prop.asString());
        }
      }
      else if(getValue() != null){
        vars.put(getName(), getValue());
      }
    }
  }

}
