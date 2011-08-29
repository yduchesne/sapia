package org.sapia.regis.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

import org.sapia.regis.DuplicateNodeException;
import org.sapia.regis.RWNode;
import org.sapia.regis.util.VarContext;

public class RegistryConfigLoader {
  
  private RWNode _root;
  
  public RegistryConfigLoader(RWNode root){
     _root = root;
  }

  public void load(File file, Map vars) throws IOException, DuplicateNodeException, Exception{
    doLoad(new FileInputStream(file), vars);
  }  
  
  public void load(File file) throws IOException, DuplicateNodeException, Exception{
    doLoad(new FileInputStream(file), Collections.EMPTY_MAP);
  }
  
  public void load(InputStream is, Map vars) throws IOException, DuplicateNodeException, Exception{
    doLoad(is, vars);
  }  

  public void load(InputStream is) throws IOException, DuplicateNodeException, Exception{
    doLoad(is, Collections.EMPTY_MAP);
  }  
  
  private RegistryTag doLoad(InputStream is, Map vars) 
    throws IOException, DuplicateNodeException, Exception{
    RegistryTag conf = (RegistryTag)VarContext.include(is, vars);
    conf.create(_root);
    return conf;
  }
  
}
