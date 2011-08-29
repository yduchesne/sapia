package org.sapia.regis.codegen;

import org.sapia.regis.Node;
import org.sapia.regis.Registry;

public class RootHelper {
  
  protected static <T extends NodeCapable> T getRootFrom(Class<T> rootClass, Registry reg){
    try{
      return rootClass.getConstructor(new Class[]{Node.class}).newInstance(new Object[]{reg.getRoot()});
    }catch(Exception e){
      throw new IllegalStateException("Could not create root", e);
    }
    
  }

}
