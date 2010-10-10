/*
 * AssignStep.java
 *
 * Created on December 3, 2005, 3:40 PM
 */

package org.sapia.soto.jython.stm;

import org.python.core.PyObject;
import org.sapia.soto.jython.bean.Converters;
import org.sapia.soto.jython.bean.PropertyPath;
import org.sapia.soto.state.AbstractStep;
import org.sapia.soto.state.Result;
import org.sapia.soto.util.Utils;

/**
 * @author yduchesne
 */
public class AssignStep extends AbstractStep{
  
  private String[] _scopes;
  
  private String _type, _from;
  private PropertyPath _to;
  
  /** Creates a new instance of Form */
  public AssignStep() {
  }
  
  public void setScopes(String scopes){
    _scopes = Utils.split(scopes, ',', true);
  }
  
  public void setFrom(String key){
    _from = key;
  }
  
  public void setTo(String property){
    _to = PropertyPath.parse(property);
  }
  
  public void setType(String type){
    _type = type;
  }
  
  public void execute(Result res){
    if(_from == null){
      throw new IllegalStateException("'from' not set");
    }
    if(_to == null){
      throw new IllegalStateException("'to' not set");
    }    
    
    if(!res.getContext().hasCurrentObject()){
      return;
    }
   
    Object val;
    if(!(res.getContext().currentObject() instanceof PyObject)){
      res.error("Instance of " + PyObject.class.getName() + " expected " +
        "on context stack");
      return;
    }
    PyObject target = (PyObject)res.getContext().currentObject();
    if(_scopes == null){
      val = res.getContext().get(_from);
    }
    else{
      val = res.getContext().get(_from, _scopes);
    }
    if(val != null){
      if(_type != null){
        val = Converters.convert(_type, val);
      }
      _to.setOrAdd(target, val);
    }
  }
  
}
