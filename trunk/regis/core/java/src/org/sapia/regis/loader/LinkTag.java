package org.sapia.regis.loader;

import org.sapia.regis.Node;

public class LinkTag {
  
  public static final String PREPEND = "prepend";
  public static final String APPEND  = "append";  
  
  private String type = APPEND; 
  private String id, path;
  
  public String getRef() {
    return id;
  }

  public void setRef(String id) {
    this.id = id;
  }  
  
  public void setPath(String path) {
    this.path = path;
  } 

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
  
  void create(ConfigContext ctx){
    if(id == null && path == null){
      throw new IllegalStateException("include id ref and path not defined");
    }
    
    boolean isPath = path != null;
    String idOrPath;
    if(id != null){
      idOrPath = id;
    }
    else{
      idOrPath = path;
    }
    Node node = (Node)ctx.getNodeFor(idOrPath, isPath);
    if(node == null){
      throw new IllegalStateException("Invalid link; no node found for id ref or path: " + idOrPath);
    }
    if(type.equals(PREPEND)){
      ctx.getParent().prependLink(node);
    }
    else{
      ctx.getParent().appendLink(node);      
    }
  }

}
