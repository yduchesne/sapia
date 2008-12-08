package org.sapia.regis.gui.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sapia.gumby.RenderContext;
import org.sapia.regis.Node;
import org.sapia.regis.Property;
import org.sapia.regis.RegisLog;
import org.sapia.regis.gui.GlobalContext;
import org.sapia.regis.gui.GuiConsts;
import org.sapia.regis.gui.command.NodeUpdateCommand;
import org.sapia.regis.gui.widgets.NodePanel;
import org.sapia.regis.impl.PropertyImpl;

public class NodeModel implements GuiConsts{

  protected Node node;
  
  protected List _props = new ArrayList();
  
  public NodeModel(Node node){
    this.node = node;
  }
  
  public Node getNode(){
    return this.node;
  }
  
  public synchronized void setNode(Node node){
    _props.clear();
    this.node = node;
  }
  
  public synchronized void addProperty(Property prop){
    _props.add(prop);
  }
  
  public synchronized List getProperties(){
    Collection keyColl = node.getPropertyKeys();
    RegisLog.debug(getClass(), "Got property keys " + keyColl);
    Iterator keys = keyColl.iterator();
    List properties = new ArrayList(keyColl.size());
    Set addedKeys = new HashSet();
    Map props = new HashMap();
    props = node.getProperties();
    RegisLog.Timer timer = RegisLog.start(getClass(), "node.getProperty()");    
    while(keys.hasNext()){
      String key = (String)keys.next();
      String prop = (String)props.get(key);
      if(!addedKeys.contains(key)){
        properties.add(new PropertyImpl(key, prop));
        addedKeys.add(key);        
      }
    }
    timer.end(getClass(), "node.getProperty()");    
    for(int i = 0; i < _props.size(); i++){
      Property prop = (Property)_props.get(i);
      if(!addedKeys.contains(prop.getKey())){
        properties.add(prop);
        addedKeys.add(prop.getKey());        
      }
    }
    RegisLog.debug(getClass(), "Got properties " + properties);    
    return properties;
  }
  
  public synchronized void update() throws Exception{
    NodePanel panel = (NodePanel)GlobalContext.getInstance()
      .getRenderContext().getEnv().get(NODE_PANE);
    RenderContext child = GlobalContext.getInstance().getRenderContext().newChildInstance();
    List properties = panel.getProperties();
    properties.addAll(_props);
    child.getEnv().put("properties", properties, "local");
    NodeUpdateCommand command = new NodeUpdateCommand(this);
    command.execute(child);
    _props.clear();
  }
  
  public String toString(){
    if(node.isRoot()){
      return "ROOT";
    }
    return node.getName();
  }  
  
}
