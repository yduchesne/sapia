package org.sapia.regis.forms;

import java.util.HashMap;
import java.util.Map;

import org.sapia.regis.Node;
import org.sapia.regis.RWNode;

public class FormInstance {
  
  private Form _form;
  private Map _props = new HashMap();
  private RWNode _node;
  
  FormInstance(Form form, RWNode node){
    _form = form;
    _node = node;
  }
  
  public Form getForm(){
    return _form;
  }
  
  public Node getNode(){
    return _node;
  }
  
  public void assign(){
    _form.assign(_node, _props);
  }
  
  public void setProperty(String name, String value){
    _props.put(name, value);
  }
  
  public void clear(){
    _props.clear();
  }
  

}
