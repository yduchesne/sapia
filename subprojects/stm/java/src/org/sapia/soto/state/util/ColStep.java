package org.sapia.soto.state.util;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang.ClassUtils;
import org.sapia.soto.state.Result;
import org.sapia.soto.state.Scope;
import org.sapia.soto.state.Step;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectHandlerIF;

public class ColStep implements Step,ObjectHandlerIF {

  private String _key;
  private String _value;
  private Collection _values;
  private String _scope;

  public ColStep(){
    _values=new ArrayList();
  }
  
  public void setKey(String key) {
    _key = key;
  }

  public void setValue(String value) {
    _value = value;
  }

  public void setScope(String scope) {
    _scope = scope;
  }

  /**
   * @see org.sapia.soto.state.Step#getName()
   */
  public String getName() {
    return ClassUtils.getShortClassName(getClass());
  }

  /**
   * @see org.sapia.soto.state.Executable#execute(org.sapia.soto.state.Result)
   */
  public void execute(Result st) {
    if(_key == null) {
      throw new IllegalStateException("Variable key not specified");
    }

    if(_scope == null) {
      throw new IllegalStateException("Variable scope not specified");
    }

    if(_value == null && _values==null) {
      throw new IllegalStateException("Variable value not specified");
    }

    Scope scope = (Scope) st.getContext().getScopes().get(_scope);

    if(scope == null) {
      throw new IllegalArgumentException("Scope not found: " + _scope);
    }
    
    Collection col=null;
    try{
      col=(Collection) scope.getVal(_key);
    }catch(ClassCastException cce){
      
    }
    
    if(col==null){
      col=new ArrayList();
    }
    if(_value!=null){
      col.add(_value);
    }
    
    if(!_values.isEmpty()){
      col.addAll(_values);
    }
    
    scope.putVal(_key, col);
  }

  public void handleObject(String anElementName, Object anObject) throws ConfigurationException {
    _values.add(anObject);
  }

}
