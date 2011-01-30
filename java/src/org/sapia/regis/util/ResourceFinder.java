package org.sapia.regis.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.sapia.util.text.SystemContext;

public class ResourceFinder {
  
  private List _resources = new ArrayList();
  private String _list;
  
  ResourceFinder(String list){
    _list = list;
  }
  
  public static InputStream findResource(String resourceList) throws FileNotFoundException, IOException{
    return ResourceFinderFactory.parse(resourceList).find(false);
  }
  
  public static InputStream getResource(String resourceList) throws IOException{
    return ResourceFinderFactory.parse(resourceList).find(true);
  }
  
  public static void findProperties(String resourceList, Properties props) throws FileNotFoundException, IOException{
    ResourceFinderFactory.parse(resourceList).doLoadProperties(false, props);
  }
  
  public static void loadProperties(String resourceList, Properties props) throws FileNotFoundException, IOException{
    ResourceFinderFactory.parse(resourceList).doLoadProperties(true, props);
  }    
  
  void addResource(String resource){
    _resources.add(resource);
  }
  
  void addResources(List resources){
    _resources.add(resources);
  }
  
  InputStream find(boolean ignoreNotFound) throws FileNotFoundException, IOException{
    for(int i = 0; i < _resources.size(); i++){
      Object res = _resources.get(i);
      if(res instanceof String){
        if(ignoreNotFound){
          try{
            return Utils.load(ResourceFinder.class, (String)res);
          }catch(FileNotFoundException e){
            continue;
          }
        }
        else{
          return Utils.load(ResourceFinder.class, (String)res);
        }
      }
      else{
        try{
          List resources = (List)res;
          for(int j = 0; j < resources.size(); j++){
            return Utils.load(ResourceFinder.class, (String)resources.get(j));
          }
        }catch(IOException e){
          //noop
        }
      }
    }
    if(ignoreNotFound){
      return null;
    }
    throw new FileNotFoundException("No resource could be found in: " + _list);
  }
  
  void doLoadProperties(boolean ignoreNotFound, Properties props) throws FileNotFoundException, IOException{
    for(int i = 0; i < _resources.size(); i++){
      Object res = _resources.get(i);
      if(res instanceof String){
        if(ignoreNotFound){
          try{
            doLoad(Utils.load(ResourceFinder.class, (String)res), props);
          }catch(FileNotFoundException e){
            continue;
          }
        }
        else{
          doLoad(Utils.load(ResourceFinder.class, (String)res), props);
        }
      }
      else{
        try{
          List resources = (List)res;
          for(int j = 0; j < resources.size(); j++){
            doLoad(Utils.load(ResourceFinder.class, (String)resources.get(j)), props);
          }
        }catch(IOException e){
          //noop
        }
      }
    }
  }  
  
  private void doLoad(InputStream is, Properties props) throws IOException{
    Properties newProps = new Properties(); 
    try{
      newProps.load(is);
    }finally{
      is.close();
    }
    newProps = Utils.replaceVars(new PropertiesContext(props, new SystemContext()), newProps);    
    Enumeration names = newProps.propertyNames();
    while(names.hasMoreElements()){
      String name = (String)names.nextElement();
      props.setProperty(name, newProps.getProperty(name));
    }
  }

}
