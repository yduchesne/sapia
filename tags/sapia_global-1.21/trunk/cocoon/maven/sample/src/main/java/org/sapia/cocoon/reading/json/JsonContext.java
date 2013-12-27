package org.sapia.cocoon.reading.json;

import java.util.Map;

import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;

public class JsonContext {
  
  private static ThreadLocal<Map> state = new ThreadLocal<Map>();
  
  static void setObjectModel(Map objectModel){
    state.set(objectModel);
  }
  
  static void unsetObjectModel(){
    state.set(null);
  }
  
  public static Map objectModel() {
    Map objectModel = state.get();
    if(objectModel == null){
      throw new IllegalStateException("Object model not set");
    }
    return objectModel;
  }
  
  public static Request getRequest(){
    return ObjectModelHelper.getRequest(objectModel());
  }

}
