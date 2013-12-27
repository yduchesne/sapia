package org.sapia.regis.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sapia.regis.RegisLog;
import org.sapia.regis.util.Utils;
import org.sapia.regis.util.VarContext;
import org.sapia.util.xml.confix.ConfigurationException;
import org.sapia.util.xml.confix.ObjectCreationCallback;

public class StaticIncludeTag extends BaseTag implements ObjectCreationCallback{
  
  private String uri;
  private List params = new ArrayList();
  
  public Param createParam(){
    Param p = new Param();
    params.add(p);
    return p;
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }
  
  public Object onCreate() throws ConfigurationException {
    if(uri == null){
      throw new IllegalStateException("URI not specified on " + getTagName());
    }
    try{
      Map vars = new HashMap();
      for(int i = 0; i < params.size(); i++){
        Param p = (Param)params.get(i);
        vars.put(p.getName(), p.getValue());
      }
      RegisLog.debug(getClass(), "Loading static include '" + uri + "'");
      return VarContext.include(Utils.load(getClass(), uri), vars);
    }catch(Exception e){
      throw new ConfigurationException("Could not load resource: " + uri, e);
    }
  }

}
