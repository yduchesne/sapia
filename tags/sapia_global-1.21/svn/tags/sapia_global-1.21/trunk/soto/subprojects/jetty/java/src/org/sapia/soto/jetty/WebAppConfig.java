package org.sapia.soto.jetty;

import java.util.ArrayList;
import java.util.List;

import org.mortbay.jetty.security.UserRealm;
import org.mortbay.jetty.webapp.WebAppContext;
import org.sapia.soto.util.Param;

public class WebAppConfig extends WebAppContext{
  
  private List params = new ArrayList();

  public WebAppConfig(){
  }
  
  public void setUserRealm(UserRealm userRealm){
    super.getSecurityHandler().setUserRealm(userRealm);
  }  
  
  public String getLocation() {
    return super.getResourceBase();
  }
  
  public void setLocation(String location) {
    super.setResourceBase(location);
  }

  public String getVirtualHost() {
    if(super.getVirtualHosts() == null ||
       super.getVirtualHosts().length == 0)
      return null;
    else
      return super.getVirtualHosts()[0];
  }
  
  public void setVirtualHost(String host){
    super.setVirtualHosts(new String[]{host});
  }
  
  public Param createParam(){
    Param p = new Param();
    params.add(p);
    return p;
  }
  
  public List getParams(){
    return params;
  }
  
}
