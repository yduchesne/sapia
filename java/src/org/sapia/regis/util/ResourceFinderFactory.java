package org.sapia.regis.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ResourceFinderFactory {
  
  static final String GROUP_START_DELIM = "("; 
  static final String GROUP_END_DELIM = ")";
  static final String RESOURCE_DELIM = ",";

  static final String DELIMS = "(),";
  
  static ResourceFinder parse(String list){
    ResourceFinder finder = new ResourceFinder(list);
    StringTokenizer tokenizer = 
      new StringTokenizer(list, DELIMS, true);
    
    List group = null;
    while(tokenizer.hasMoreTokens()){
      String token = tokenizer.nextToken();
      if(isGroupStart(token)){
        if(group != null){
          finder.addResources(group);
        }
        group = new ArrayList();
      }
      else if(isGroupEnd(token)){
        if(group != null){
          if(group.size() > 0){
            finder.addResources(group);
          }
          group = null;          
        }
      }
      else if(isResourceDelim(token)){
        //noop
      }      
      else{
        if(group != null){
          group.add(token.trim());
        }
        else{
          finder.addResource(token.trim());
        }
      }
    }
    if(group != null){
      finder.addResources(group);
    }
    return finder;
  }
  
  private static boolean isGroupStart(String token){
    return token.length() == 1 && token.equals(GROUP_START_DELIM);
  }
  
  private static boolean isGroupEnd(String token){
    return token.length() == 1 && token.equals(GROUP_END_DELIM);
  }
  
  private static boolean isResourceDelim(String token){
    return token.length() == 1 && token.equals(RESOURCE_DELIM);
  }    
}
