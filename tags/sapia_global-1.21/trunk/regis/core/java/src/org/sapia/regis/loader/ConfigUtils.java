package org.sapia.regis.loader;

import java.util.Collection;

public class ConfigUtils {

  public static Collection fillClasses(Collection collection, Class[] classes){
    for(int i = 0; i < classes.length; i++){
      collection.add(classes[i]);
    }
    return collection;
  }
  
  public static void checkAllowed(String parentName, String childName, Object instance, Class[] classes){
    
    for(int i = 0; i < classes.length; i++){
      if(classes[i].isAssignableFrom(instance.getClass())){
        return;
      }
    }
    System.out.println("========= " + childName + " not valid under " + parentName);
    throw new IllegalArgumentException(childName + " not valid under " + parentName);
  } 
}
