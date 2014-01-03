package org.sapia.regis.loader;

import java.util.Iterator;

import org.sapia.regis.Node;
import org.sapia.regis.Property;

public class IncludeTag {
  
  private String id, path;
  
  public String getRef() {
    return id;
  }
  
  public void setPath(String path) {
    this.path = path;
  } 
  
  public void setRef(String id) {
    this.id = id;
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
    int index;
    if((index = idOrPath.indexOf("@")) > 0){

      String newId = idOrPath.substring(0, index);
      Node node = (Node)ctx.getNodeFor(newId, isPath);
      if(node == null){
        throw new IllegalStateException("Invalid include; no node found for id ref or path: " + newId);
      }
      if(index == idOrPath.length() - 1){      
        Iterator itr = node.getChildren().iterator();
        while(itr.hasNext()){
          ctx.getParent().addInclude((Node)itr.next());
        }
      }
      else{
        String propKey = idOrPath.substring(index+1);
        Property prop = node.getProperty(propKey);
        if(prop.isNull()){
          throw new IllegalStateException("Invalid include; no property found for " +  propKey + " under node ref or path: " + newId);
        }
        ctx.getParent().setProperty(prop.getKey(), prop.getValue());
      }
    }
    else{
      Node node = (Node)ctx.getNodeFor(idOrPath, isPath);
      if(node == null){
        throw new IllegalStateException("Invalid include; no node found for id ref or path: " + idOrPath);
      }      
      ctx.getParent().addInclude(node);
    }
  }

}
