package org.sapia.regis.ant;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.sapia.regis.Node;
import org.sapia.regis.Path;
import org.sapia.regis.Property;
import org.sapia.regis.Registry;
import org.sapia.regis.RegistryContext;

public class CheckConfigTask extends Task{
  
  private File file;
  private List checks = new ArrayList();
  
  public void setProperties(File f){
    file = f;
  }
  
  public void execute() throws BuildException {
    if(file == null){
      throw new BuildException("Regis property file not set");
    }
    InputStream is = null;
    Properties props = new Properties();
    try{
      is = new FileInputStream(file);
      props.load(is);
    }catch(IOException e){
      try{
        is.close();
      }catch(Exception e2){}
      throw new BuildException("Could not load registry properties", e);      
    }
    RegistryContext ctx = new RegistryContext(props);
    Registry reg = null;
    try{
      reg = ctx.connect();
    }catch(Exception e){
      throw new BuildException("Could not instantiate registry", e);
    }
    performChecks(reg);
  }
  
  private void performChecks(Registry reg){
    for(int i = 0; i < checks.size(); i++){
      Check ch = (Check)checks.get(i);
      ch.execute(reg);
    }
  }
  
  public CheckProperty createCheckProperty(){
    CheckProperty check = new CheckProperty();
    checks.add(check);
    return check;
  }
  
  public CheckNode createCheckNode(){
    CheckNode check = new CheckNode();
    checks.add(check);
    return check;
  }  
  
  public static interface Check{
    public void execute(Registry reg) throws BuildException;
    public void execute(Node node) throws BuildException;
  }
  
  public static final class CheckProperty implements Check{
    
    private Path path;
    private String name, value;
    
    public void setName(String name) {
      this.name = name;
    }
    
    public void setValue(String value){
      this.value = value;
    }

    public void setPath(String pathStr) {
      this.path = Path.parse(pathStr);
    }

    public void execute(Registry reg) throws BuildException {
      if(path == null){
        throw new BuildException("Path not set on checkProperty");
      }
      
      Node node = reg.getRoot().getChild(path);
      if(node == null){
        throw new BuildException("No node for path: " + path);
      }
      
      execute(node);
    }
    
    public void execute(Node node) throws BuildException{
      if(name == null){
        throw new BuildException("Name not set on checkProperty");
      }      
      
      Property prop = node.renderProperty(name);
      if(prop.isNull()){
        throw new BuildException("No property found for: " + name + " on node: " + node.getAbsolutePath());
      }
     
      if(value != null){
        if(!prop.asString().equals(value)){
          throw new BuildException("Expected " + value + 
              " for property " + name + " on node: " + node.getAbsolutePath() + "; got: " + prop.asString());          
        }
      }
    }
  }
  
  public static final class CheckNode implements Check{
    
    private Path path;
    private int childCount = -1;
    private List checks = new ArrayList();
    
    public void setPath(String pathStr){
      path = Path.parse(pathStr);
    }
    
    public CheckProperty createCheckProperty(){
      CheckProperty prop = new CheckProperty();
      checks.add(prop);
      return prop;
    }

    public CheckNode createCheckNode(){
      CheckNode check = new CheckNode();
      checks.add(check);
      return check;
    }

    public void execute(Registry reg) throws BuildException {
      if(path == null){
        throw new BuildException("Path not set on checkNode");
      }
      
      Node node = reg.getRoot().getChild(path);
      if(node == null){
        throw new BuildException("No node for path: " + path);
      }
      
      for(int i = 0; i < checks.size(); i++){
        Check prop = (Check)checks.get(i);
        if(prop instanceof CheckNode){
          Path childPath = ((CheckNode)prop).path;
          if(childPath == null){
            throw new BuildException("Path not set on child checkNode of " + path);
          }
          Node child = node.getChild(childPath);
          if(child == null){
            throw new BuildException("No child node found for " + childPath + " under node: " + path);            
          }
          prop.execute(child);
        }
        else{
          prop.execute(node);
        }
      }
    }
    
    public void execute(Node node) throws BuildException{
      if(childCount > -1){
        int count = node.getChildren().size();
        if(count != childCount){
          throw new BuildException("Expected " + childCount + 
              " child nodes for " + node.getAbsolutePath() + "; got: " + count);
        }
      }
    }

    public void setChildCount(int childCount) {
      this.childCount = childCount;
    }

    public void setPath(Path path) {
      this.path = path;
    }
  }

}
