package org.sapia.regis.loader;

import java.util.ArrayList;
import java.util.List;

import org.sapia.regis.DuplicateNodeException;
import org.sapia.regis.RWNode;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectHandlerIF;

public class NodeTag extends BaseTag implements ObjectHandlerIF {
  
  public static final String OP_CREATE         = "create";
  public static final String OP_UPDATE         = "update";  
  public static final String OP_UPDATE_OVW     = "update-overwrite";  
  public static final String OP_DELETE         = "delete";
  
  static final Class[] ALLOWED = new Class[]{
    IncludeTag.class,
    NodeTag.class,
    LinkTag.class,
    PropertyTag.class,
    String.class
  };
  
  private String name, id;
  private List nodes = new ArrayList();
  private List links = new ArrayList();  
  private List includes = new ArrayList();  
  private List properties = new ArrayList();
  private String operation;
  private boolean inherits;
 
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }  
  
  public void setOperation(String operation) {
    this.operation = operation;
  }
  
  public String getOperation(){
    return this.operation;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public void setInheritsParent(boolean inherits) {
    this.inherits = inherits;
  }  
  
  public LinkTag createLink(){
    LinkTag link = new LinkTag();
    links.add(link);
    return link;
  }  
  
  public IncludeTag createInclude(){
    IncludeTag incl = new IncludeTag();
    includes.add(incl);
    return incl;
  }  
  
  public NodeTag createNode(){
    NodeTag node = new NodeTag();
    nodes.add(node);
    return node;
  }  
  
  public PropertyTag createProperty(){
    PropertyTag prop = new PropertyTag();
    properties.add(prop);
    return prop;
  }
  
  void create(ConfigContext ctx) throws DuplicateNodeException{
    if(operation == null){
      operation = ctx.getDefaultOperation();
    }
    if(name == null){
      throw new IllegalStateException("Registry node name not set");
    }
    if(operation.equals(OP_DELETE)){
      ctx.getParent().deleteChild(name);
      return;
    }
    RWNode node = (RWNode)ctx.getParent().getChild(name);
    if(node == null){
      node = (RWNode)ctx.getParent().createChild(name);
    }
    if(operation.equals(OP_CREATE)){
      node.deleteChildren();
      node.deleteProperties();
      node.deleteIncludes();
      node.deleteLinks();
    }
    else if(operation.equals(OP_UPDATE_OVW)){
      node.deleteProperties();
    }
    
    node.setInheritsParent(inherits);
    
    if(id != null){
      if(ctx.getNodes().containsKey(id)){
        throw new IllegalStateException("node element already defined for id: " + id);
      }
      ctx.getNodes().put(id, node);
    }
    
    for(int i = 0; i < properties.size(); i++){
      PropertyTag prop = (PropertyTag)properties.get(i);
      if(prop.getName() != null && prop.getValue() != null){
        node.setProperty(prop.getName(), prop.getValue());
      }
    }
    ConfigContext child = new ConfigContext(node, ctx.getNodes());    
    child.setDefaultOperation(ctx.getDefaultOperation());
    for(int i = 0; i < links.size(); i++){
      LinkTag linkCfg = (LinkTag)links.get(i);
      linkCfg.create(child);
    }
    
    for(int i = 0; i < includes.size(); i++){
      IncludeTag includeCfg = (IncludeTag)includes.get(i);
      includeCfg.create(child);
    }
    
    for(int i = 0; i < nodes.size(); i++){
      NodeTag nodeCfg = (NodeTag)nodes.get(i);
      nodeCfg.create(child);
    }
  }
  
  public void handleObject(String name, Object child) throws ConfigurationException {
    ConfigUtils.checkAllowed(super.getTagName(), name, child, ALLOWED);
    if(child instanceof NodeTag){
      this.nodes.add(child);
    }
    else if(child instanceof PropertyTag){
      this.properties.add(child);
    }    
    else if(child instanceof LinkTag){
      this.links.add(child);
    }    
    else if(child instanceof IncludeTag){
      this.includes.add(child);
    }
    else if (child instanceof String) {
      this.properties.add(CustomPropertyTag.create(name, (String) child));
    }
  }

}
